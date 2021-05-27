package com.maviance.easypay.services.implementations;

import com.maviance.easypay.commands.AirtimePaymentCmd;
import com.maviance.easypay.config.Checks;
import com.maviance.easypay.exceptions.CustomException;
import com.maviance.easypay.model.Request;
import com.maviance.easypay.repositories.RequestRepo;
import com.maviance.easypay.services.interfaces.AirtimeService;
import com.maviance.easypay.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.maviance.s3pjavaclient.ApiException;
import org.maviance.s3pjavaclient.api.CollectionApi;
import org.maviance.s3pjavaclient.model.*;
import org.springframework.http.HttpStatus;

import java.util.Set;

import static com.maviance.easypay.utils.Constants.email;

@org.springframework.stereotype.Service
@Slf4j
public class AirtimeServiceImpl implements AirtimeService {

    private final RequestRepo requestRepo;
    private final CollectionApi collectionApi;
    private final Checks checks;

    public AirtimeServiceImpl(RequestRepo requestRepo, CollectionApi collectionApi, Checks checks) {
        this.requestRepo = requestRepo;
        this.collectionApi = collectionApi;
        this.checks = checks;
    }

    @Override
    //this is just for custom amount
    public String executeTopup(AirtimePaymentCmd airtimeCmd,String sourcePTN) {
        try {
            if (checks.isS3pAvailable()) {
                Request request = requestRepo.findBySourcePTN(sourcePTN);
                if (request == null) {
                    log.error("No previously CashOut Corresponding To PTN");
                    throw new CustomException("No previously CashOut Corresponding To PTN", HttpStatus.NOT_ACCEPTABLE);
                }

                request.configWithPaymentCommand(airtimeCmd);
                s3pAirtimeTopup(request, airtimeCmd);
                return request.getRequestId().toString();
            }
            throw new CustomException("Server Unavailable", HttpStatus.SERVICE_UNAVAILABLE);
        } catch (ApiException e) {
            log.error(e.getResponseBody());
            throw new CustomException("An Error During Airtime Topup", HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    private void s3pAirtimeTopup(Request request, AirtimePaymentCmd airtimeCmd) throws ApiException {
        Set<Service> services = Constants.SERVICES;
        Integer destinationServiceId = services.stream()
                .filter(service -> service.getType() == Service.TypeEnum.TOPUP &&
                        service.getTitle().toLowerCase().contains(airtimeCmd.getDestination().toLowerCase()))
                .mapToInt(Service::getServiceid).findFirst()
                .orElseThrow(() -> {
                    log.error("No Service with name {}.",airtimeCmd.getDestination());
                    return new CustomException("No Service with the given name", HttpStatus.BAD_REQUEST);
                });
        Topup topup = collectionApi.topupGet(destinationServiceId).get(0);
        topup.setAmountLocalCur(airtimeCmd.getAmount());
        log.info("Initiating Quote Request for Topup");
        QuoteRequest quoteRequest = new QuoteRequest();
        quoteRequest.setAmount(topup.getAmountLocalCur());
        quoteRequest.setPayItemId(topup.getPayItemId());

        Quotestd offer = collectionApi.quotestdPost(quoteRequest);
        log.info("Successful quote request");
        log.info("{}", offer);
        log.info("Initiating Collection for Topup");
        CollectionstdRequest collection = new CollectionstdRequest();
        collection.setCustomerPhonenumber(request.getSourceServiceNumber());
        collection.setCustomerEmailaddress(email);
        collection.setQuoteId(offer.getQuoteId());
        collection.setServiceNumber(airtimeCmd.getDestinationServiceNumber());
        collection.setCustomerName("Lowe Florian");
        Collectionstd payment = collectionApi.collectstdPost(collection);
        log.info("Collection Successful");
        request.setDestinationPTN(payment.getPtn());
        request.setStatus(Request.Status.SUCCESS);
        requestRepo.save(request);
    }
}
