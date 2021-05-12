package com.maviance.easypay.services.implementations;

import com.maviance.easypay.commands.CashOutCommand;
import com.maviance.easypay.exceptions.CustomException;
import com.maviance.easypay.model.CardDetails;
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

    public CashInServiceImpl(RequestRepo requestRepo, CollectionApi collectionApi) {
        this.requestRepo = requestRepo;
        this.collectionApi = collectionApi;
    }

    @Override
    public String reimburse(String cashOutPTN) {
        Request request = requestRepo.findBySourcePTN(cashOutPTN);
        request.setStatus(Request.Status.REIMBURSED);
        try {
            CashInCommand cashInCommand = new CashInCommand(request.getSource(), request.getSourceServiceNumber(), null, request.getAmountDebitedFromSource());
            return s3pCashIn(request, cashInCommand);
        } catch (ApiException e) {
            log.error(e.getResponseBody());
            throw new CustomException("Error During CashIn", HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    private String s3pCashIn(Request request, CashInCommand cashInCommand) throws ApiException {
        Set<org.maviance.s3pjavaclient.model.Service> services = Constants.services;
        Integer sourceServiceId = services.stream()
                .filter(service -> service.getType() == org.maviance.s3pjavaclient.model.Service.TypeEnum.CASHIN
                        && service.getTitle().toLowerCase().contains(cashInCommand.getSource().toLowerCase()))
                .mapToInt(org.maviance.s3pjavaclient.model.Service::getServiceid).findFirst()
                .orElseThrow(() -> {
                    log.error("No Service with name {}.", cashInCommand.getSource());
                    return new CustomException("No Service with the given name", HttpStatus.BAD_REQUEST);
                });
        Cashin cashin = collectionApi.cashinGet(sourceServiceId).get(0);
        log.info("Initiating Quote Request for {} Cash In", cashInCommand.getSource());
        QuoteRequest quoteRequest = new QuoteRequest();
        quoteRequest.setAmount(cashInCommand.getAmountWithFee());
        quoteRequest.setPayItemId(cashin.getPayItemId());
        Quotestd offer = collectionApi.quotestdPost(quoteRequest);
        log.info("Successful Quote Cash In Request");
        log.info("{}", offer);
        log.info("Initiating Collection for Cash In");
        CollectionstdRequest collection = new CollectionstdRequest();
        collection.setCustomerPhonenumber("" + cashInCommand.getSourceServiceNumber());
        collection.setCustomerEmailaddress(email);
        collection.setQuoteId(offer.getQuoteId());
        collection.setServiceNumber("" + cashInCommand.getSourceServiceNumber());
        collection.setCustomerName("Lowe Florian");
        Collectionstd payment = collectionApi.collectstdPost(collection);
        log.info("Collection Cash In Successful");
        request.setSourcePTN(payment.getPtn());
        requestRepo.save(request);
        return payment.getPtn();
    }

    private static class CashInCommand extends CashOutCommand {
        public CashInCommand(String source, String sourceServiceNumber, CardDetails sourceCardDetails, Float amountWithFee) {
            super(source, sourceServiceNumber, sourceCardDetails, amountWithFee);
        }
    }

}
