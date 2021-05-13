package com.maviance.easypay.services.implementations;

import com.maviance.easypay.commands.TVSubscriptionPaymentCmd;
import com.maviance.easypay.config.Checks;
import com.maviance.easypay.exceptions.CustomException;
import com.maviance.easypay.model.Request;
import com.maviance.easypay.model.SubscriptionOffer;
import com.maviance.easypay.repositories.RequestRepo;
import com.maviance.easypay.services.interfaces.TVSubscriptionService;
import com.maviance.easypay.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.maviance.s3pjavaclient.ApiException;
import org.maviance.s3pjavaclient.api.CollectionApi;
import org.maviance.s3pjavaclient.model.*;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.stream.Collectors;

import static com.maviance.easypay.utils.Constants.email;

@SuppressWarnings("DuplicatedCode")
@org.springframework.stereotype.Service
@Slf4j
public class TVSubscriptionServiceImpl implements TVSubscriptionService {
    private final CollectionApi collectionApi;
    private final Checks checks;
    private final RequestRepo requestRepo;

    public TVSubscriptionServiceImpl(CollectionApi collectionApi, Checks checks, RequestRepo requestRepo) {
        this.collectionApi = collectionApi;
        this.checks = checks;
        this.requestRepo = requestRepo;
    }

    @Override
    public List<SubscriptionOffer> getAllOffers(String providerName) {
        try {
            Integer serviceId = Constants.SERVICES.stream().filter(service -> service.getTitle().toLowerCase().contains(providerName.toLowerCase()))
                    .findFirst().orElseThrow(() -> new CustomException("No provider with the Given Name", HttpStatus.BAD_REQUEST)).getServiceid();
            final List<Product> products = collectionApi.productGet(serviceId);
            if (products.isEmpty()) {
                throw new CustomException("No Product for Given Service ID", HttpStatus.BAD_REQUEST);
            }
            return products.stream().map(SubscriptionOffer::new)
                    .collect(Collectors.toList());
        } catch (ApiException e) {
            log.error(e.getResponseBody());
            throw new CustomException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public String payTvOffer(TVSubscriptionPaymentCmd tvSubscriptionPaymentCmd, String sourcePTN) {
        try {
            if (checks.isS3pAvailable()) {
                Request request = requestRepo.findBySourcePTN(sourcePTN);
                if (request == null) {
                    log.error("No previously CashOut Corresponding To PTN");
                    throw new CustomException("No previously CashOut Corresponding To PTN", HttpStatus.NOT_ACCEPTABLE);
                }
                request.configWithPaymentCommand(tvSubscriptionPaymentCmd);
                s3pTvSubscription(request, tvSubscriptionPaymentCmd);
                return request.getRequestId().toString();
            }
            throw new CustomException("Server Unavailable", HttpStatus.SERVICE_UNAVAILABLE);
        } catch (ApiException e) {
            log.error(e.getResponseBody());
            throw new CustomException("An Error During Tv Subscription", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void s3pTvSubscription(Request request, TVSubscriptionPaymentCmd tvSubscriptionPaymentCmd) throws ApiException {

        log.info("Initiating Quote Request for TV Plan Payment");
        QuoteRequest quoteRequest = new QuoteRequest();
        quoteRequest.setAmount(tvSubscriptionPaymentCmd.getAmount());
        quoteRequest.setPayItemId(tvSubscriptionPaymentCmd.getPlanId());

        Quotestd offer = collectionApi.quotestdPost(quoteRequest);
        log.info("Successful quote request");
        log.info("{}", offer);
        log.info("Initiating Collection for TV Subscription");
        CollectionstdRequest collection = new CollectionstdRequest();
        collection.setCustomerPhonenumber("" + tvSubscriptionPaymentCmd.getNotificationPhoneNumber());
        collection.setCustomerEmailaddress(email);
        collection.setQuoteId(offer.getQuoteId());
        collection.setServiceNumber("" + tvSubscriptionPaymentCmd.getDestinationServiceNumber());
        collection.setCustomerName("Lowe Florian");
        Collectionstd payment = collectionApi.collectstdPost(collection);
        log.info("Collection Successful");
        request.setDestinationPTN(payment.getPtn());
        request.setStatus(Request.Status.SUCCESS);
        requestRepo.save(request);
    }
}
