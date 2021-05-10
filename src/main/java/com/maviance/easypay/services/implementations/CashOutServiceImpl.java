package com.maviance.easypay.services.implementations;

import com.maviance.easypay.commands.CashOutCommand;
import com.maviance.easypay.config.Checks;
import com.maviance.easypay.exceptions.CustomException;
import com.maviance.easypay.model.Request;
import com.maviance.easypay.repositories.RequestRepo;
import com.maviance.easypay.services.interfaces.CashOutService;
import com.maviance.easypay.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.maviance.s3pjavaclient.ApiException;
import org.maviance.s3pjavaclient.api.CollectionApi;
import org.maviance.s3pjavaclient.api.HistoryApi;
import org.maviance.s3pjavaclient.model.*;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Set;

import static com.maviance.easypay.utils.Constants.email;
import static com.maviance.easypay.utils.Constants.phoneNumber;

@org.springframework.stereotype.Service
@Slf4j
public class CashOutServiceImpl implements CashOutService {
    private final CollectionApi collectionApi;
    private final RequestRepo requestRepo;
    private final HistoryApi historyApi;
    private final Checks checks;

    public CashOutServiceImpl(CollectionApi collectionApi, RequestRepo requestRepo, HistoryApi historyApi, Checks checks) {
        this.collectionApi = collectionApi;
        this.requestRepo = requestRepo;
        this.historyApi = historyApi;
        this.checks = checks;
    }

    @Override
    public String cashOut(CashOutCommand cashOutCommand) {
        Request request = new Request();
        request.configWithCashOutCommand(cashOutCommand);
        request.setStatus(Request.Status.PENDING);
        request = requestRepo.save(request);
        try {
            return s3pCashOut(request, cashOutCommand);
        } catch (ApiException e) {
            log.error(e.getMessage());
            throw new CustomException("Error During CashOut", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    private String s3pCashOut(Request request, CashOutCommand airtimeCmd) throws ApiException {
        Set<Service> services = Constants.services;
        Integer sourceServiceId = services.stream()
                .filter(service -> service.getType() == Service.TypeEnum.CASHOUT
                        && service.getTitle().toLowerCase().contains(airtimeCmd.getSource().toLowerCase()))
                .mapToInt(Service::getServiceid).findFirst()
                .orElseThrow(() -> {
                    log.error("No Service with name {}.",airtimeCmd.getSource());
                    return new CustomException("No Service with the given name", HttpStatus.BAD_REQUEST);
                });
        Cashout cashout = collectionApi.cashoutGet(sourceServiceId).get(0);
        log.info("Initiating Quote Request for Cash out");
        QuoteRequest quoteRequest = new QuoteRequest();
        quoteRequest.setAmount(airtimeCmd.getAmountWithFee());
        quoteRequest.setPayItemId(cashout.getPayItemId());
        Quotestd offer = collectionApi.quotestdPost(quoteRequest);
        log.info("Successful Quote Cash Out Request");
        log.info("{}", offer);
        log.info("Initiating Collection for Cash Out");
        CollectionstdRequest collection = new CollectionstdRequest();
        collection.setCustomerPhonenumber(phoneNumber);
        collection.setCustomerEmailaddress(email);
        collection.setQuoteId(offer.getQuoteId());
        collection.setServiceNumber("" + airtimeCmd.getSourceServiceNumber());
        collection.setCustomerName("Lowe Florian");
        Collectionstd payment = collectionApi.collectstdPost(collection);
        log.info("Collection Cash Out Successful");
        request.setSourcePTN(payment.getPtn());
        requestRepo.save(request);
        return payment.getPtn();
    }

    @Override
    public Boolean isCashOutSuccessful(String cashOutPtn) {
        try {
            if (checks.isS3pAvailable()) {
                List<Historystd> historystds = historyApi.historystdGet(cashOutPtn, null, null, null);
                if (historystds.size() != 1) {
                    log.error("A PTN is supposed to be unique");
                    throw new CustomException("PTN not Unique", HttpStatus.BAD_REQUEST);
                }
                return historystds.get(0).getStatus() == Historystd.StatusEnum.SUCCESS;
            }
            log.error("S3P is not Online.");
            throw new CustomException("Server not Available", HttpStatus.SERVICE_UNAVAILABLE);
        } catch (ApiException e) {
            log.error(e.getResponseBody());
            throw new CustomException("Error Occurred during verification", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
