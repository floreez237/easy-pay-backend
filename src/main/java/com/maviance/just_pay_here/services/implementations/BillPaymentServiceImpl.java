package com.maviance.just_pay_here.services.implementations;

import com.maviance.just_pay_here.commands.BillFetchCommand;
import com.maviance.just_pay_here.commands.BillPaymentCmd;
import com.maviance.just_pay_here.config.Checks;
import com.maviance.just_pay_here.exceptions.CustomException;
import com.maviance.just_pay_here.model.BillInfo;
import com.maviance.just_pay_here.model.Request;
import com.maviance.just_pay_here.repositories.RequestRepo;
import com.maviance.just_pay_here.services.interfaces.BillPaymentService;
import com.maviance.just_pay_here.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.maviance.s3pjavaclient.ApiException;
import org.maviance.s3pjavaclient.api.CollectionApi;
import org.maviance.s3pjavaclient.model.*;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.stream.Collectors;

import static com.maviance.just_pay_here.utils.Constants.email;


@org.springframework.stereotype.Service
@Slf4j
public class BillPaymentServiceImpl implements BillPaymentService {
    private final CollectionApi collectionApi;
    private final Checks checks;
    private final RequestRepo requestRepo;

    public BillPaymentServiceImpl(CollectionApi collectionApi, Checks checks, RequestRepo requestRepo) {
        this.collectionApi = collectionApi;
        this.checks = checks;
        this.requestRepo = requestRepo;
    }

    @Override
    public List<BillInfo> getAllOpenBills(BillFetchCommand billFetchCommand) {
        String merchantCode = Constants.MERCHANTS.stream()
                .filter(merchant -> merchant.getName().toLowerCase().equals(billFetchCommand.getProvider().toLowerCase()))
                .findFirst().orElseThrow(() -> new CustomException("No Matching Merchant", HttpStatus.BAD_REQUEST))
                .getMerchant();
        Integer serviceId = Constants.SERVICES.stream()
                .filter(service -> service.getMerchant().trim().equals(merchantCode.trim()) && service.getType() == Service.TypeEnum.SEARCHABLE_BILL)
                .findFirst().orElseThrow(() -> new CustomException("No Service of type Searchable Bill provided by the merchant", HttpStatus.BAD_REQUEST))
                .getServiceid();
        try {
            if (checks.isS3pAvailable()) {
                log.info("Fetching All {} Open Bills for service number: {}", billFetchCommand.getProvider(), billFetchCommand.getContractNumber());
                return collectionApi.billGet(merchantCode, serviceId, billFetchCommand.getContractNumber())
                        .stream().map(BillInfo::new).collect(Collectors.toList());
            }
            log.error("S3P is not Available");
            throw new CustomException("S3P is not Available", HttpStatus.SERVICE_UNAVAILABLE);
        } catch (ApiException e) {
            log.error(e.getResponseBody());
            throw new CustomException("Error Occurred During Fetch", HttpStatus.SERVICE_UNAVAILABLE);
        }


    }

    @Override
    public String executeBillPayment(BillPaymentCmd billPaymentCmd, String sourcePTN) {
        try {
            if (checks.isS3pAvailable()) {
                Request request = requestRepo.findBySourcePTN(sourcePTN);
                if (request == null) {
                    log.error("No previously CashOut Corresponding To PTN");
                    throw new CustomException("No previously CashOut Corresponding To PTN", HttpStatus.NOT_ACCEPTABLE);
                }

                request.configWithPaymentCommand(billPaymentCmd);
                s3pBillPayment(request, billPaymentCmd);
                return request.getRequestId().toString();
            }
            throw new CustomException("Server Unavailable", HttpStatus.SERVICE_UNAVAILABLE);
        } catch (ApiException e) {
            log.error(e.getResponseBody());
            throw new CustomException("An Error During Bill Payment", HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    private void s3pBillPayment(Request request, BillPaymentCmd billPaymentCmd) throws ApiException {
        log.info("Initiating Quote Request for Bill Payment");
        QuoteRequest quoteRequest = new QuoteRequest();
        quoteRequest.setAmount(billPaymentCmd.getAmount());
        quoteRequest.setPayItemId(billPaymentCmd.getBillPayItemId());

        Quotestd offer = collectionApi.quotestdPost(quoteRequest);
        log.info("Successful quote request");
        log.info("{}", offer);
        log.info("Initiating Collection for Bill Payment");
        CollectionstdRequest collection = new CollectionstdRequest();
        collection.setCustomerPhonenumber(request.getSourceServiceNumber());
        collection.setCustomerEmailaddress(email);
        collection.setQuoteId(offer.getQuoteId());
        collection.setServiceNumber(billPaymentCmd.getDestinationServiceNumber());
        collection.setCustomerName("Lowe Florian");
        log.info("{}",collection);
        Collectionstd payment = collectionApi.collectstdPost(collection);
        log.info("Collection Successful");
        request.setDestinationPTN(payment.getPtn());
        request.setStatus(Request.Status.SUCCESS);
        requestRepo.save(request);
    }
}

