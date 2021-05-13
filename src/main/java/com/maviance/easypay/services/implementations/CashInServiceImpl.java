package com.maviance.easypay.services.implementations;

import com.maviance.easypay.commands.CashInCommand;
import com.maviance.easypay.config.Checks;
import com.maviance.easypay.exceptions.CustomException;
import com.maviance.easypay.model.Request;
import com.maviance.easypay.repositories.RequestRepo;
import com.maviance.easypay.services.interfaces.CashInService;
import com.maviance.easypay.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.maviance.s3pjavaclient.ApiException;
import org.maviance.s3pjavaclient.api.CollectionApi;
import org.maviance.s3pjavaclient.model.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Set;

import static com.maviance.easypay.utils.Constants.email;

@Service
@Slf4j
public class CashInServiceImpl implements CashInService {
    private final RequestRepo requestRepo;
    private final CollectionApi collectionApi;
    private final Checks checks;

    public CashInServiceImpl(RequestRepo requestRepo, CollectionApi collectionApi, Checks checks) {
        this.requestRepo = requestRepo;
        this.collectionApi = collectionApi;
        this.checks = checks;
    }

    @Override
    public String reimburse(String cashOutPTN) {
        Request request = requestRepo.findBySourcePTN(cashOutPTN);
        request.setStatus(Request.Status.REIMBURSED);
        try {
            if (checks.isS3pAvailable()) {
                CashInCommand cashInCommand = new CashInCommand(request.getSource(), request.getSourceServiceNumber(), request.getAmountDebitedFromSource(), null);
                return s3pCashIn(request, cashInCommand);
            }
            throw new CustomException("Server Unavailable", HttpStatus.SERVICE_UNAVAILABLE);
        } catch (ApiException e) {
            log.error(e.getResponseBody());
            throw new CustomException("Error During CashIn", HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @Override
    public String fundTransferCashIn(CashInCommand cashInCommand, String sourcePTN) {
        try {
            if (checks.isS3pAvailable()) {
                Request request = requestRepo.findBySourcePTN(sourcePTN);
                if (request == null) {
                    log.error("No previously CashOut Corresponding To PTN");
                    throw new CustomException("No previously CashOut Corresponding To PTN", HttpStatus.NOT_ACCEPTABLE);
                }

                request.configWithPaymentCommand(cashInCommand);
                s3pCashIn(request, cashInCommand);
                return request.getRequestId().toString();
            }
            throw new CustomException("Server Unavailable", HttpStatus.SERVICE_UNAVAILABLE);
        } catch (ApiException e) {
            log.error(e.getResponseBody());
            throw new CustomException("An Error During Cash In", HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    private String s3pCashIn(Request request, CashInCommand cashInCommand) throws ApiException {
        Set<org.maviance.s3pjavaclient.model.Service> services = Constants.SERVICES;
        Integer sourceServiceId = services.stream()
                .filter(service -> service.getType() == org.maviance.s3pjavaclient.model.Service.TypeEnum.CASHIN
                        && service.getTitle().toLowerCase().contains(cashInCommand.getDestination().toLowerCase()))
                .mapToInt(org.maviance.s3pjavaclient.model.Service::getServiceid).findFirst()
                .orElseThrow(() -> {
                    log.error("No Service with name {}.", cashInCommand.getDestination());
                    return new CustomException("No Service with the given name", HttpStatus.BAD_REQUEST);
                });
        Cashin cashin = collectionApi.cashinGet(sourceServiceId).get(0);
        log.info("Initiating Quote Request for {} Cash In", cashInCommand.getDestination());
        QuoteRequest quoteRequest = new QuoteRequest();
        quoteRequest.setAmount(cashInCommand.getAmount());
        quoteRequest.setPayItemId(cashin.getPayItemId());
        Quotestd offer = collectionApi.quotestdPost(quoteRequest);
        log.info("Successful Quote Cash In Request");
        log.info("{}", offer);
        log.info("Initiating Collection for Cash In");
        CollectionstdRequest collection = new CollectionstdRequest();
        collection.setCustomerPhonenumber("" + cashInCommand.getDestinationServiceNumber());
        collection.setCustomerEmailaddress(email);
        collection.setQuoteId(offer.getQuoteId());
        collection.setServiceNumber("" + cashInCommand.getDestinationServiceNumber());
        collection.setCustomerName("Lowe Florian");
        Collectionstd payment = collectionApi.collectstdPost(collection);
        log.info("Collection Cash In Successful");
        request.setSourcePTN(payment.getPtn());
        requestRepo.save(request);
        return payment.getPtn();
    }


}
