package com.maviance.easypay.services.implementations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maviance.easypay.commands.CashOutCommand;
import com.maviance.easypay.commands.FlutterWaveValidationCmd;
import com.maviance.easypay.config.Checks;
import com.maviance.easypay.exceptions.CustomException;
import com.maviance.easypay.model.CardPaymentPinResponse;
import com.maviance.easypay.model.GeneralCardPaymentResponse;
import com.maviance.easypay.model.Request;
import com.maviance.easypay.repositories.RequestRepo;
import com.maviance.easypay.services.interfaces.CashOutService;
import com.maviance.easypay.utils.Constants;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.maviance.s3pjavaclient.ApiException;
import org.maviance.s3pjavaclient.api.CollectionApi;
import org.maviance.s3pjavaclient.api.HistoryApi;
import org.maviance.s3pjavaclient.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Set;

import static com.maviance.easypay.utils.Constants.email;

@org.springframework.stereotype.Service
@Slf4j
public class CashOutServiceImpl implements CashOutService {

    @Value("${flutterwave.base.url}")
    private String flutterWaveBaseUrl;
    private final CollectionApi collectionApi;
    private final RequestRepo requestRepo;
    private final HistoryApi historyApi;
    private final Checks checks;
    private final RestTemplate restTemplate;

    public CashOutServiceImpl(CollectionApi collectionApi, RequestRepo requestRepo,
                              HistoryApi historyApi, Checks checks, RestTemplate restTemplate) {
        this.collectionApi = collectionApi;
        this.requestRepo = requestRepo;
        this.historyApi = historyApi;
        this.checks = checks;
        this.restTemplate = restTemplate;
    }

    @Override
    public String s3pCashOut(CashOutCommand cashOutCommand) {
        Request request = new Request();
        request.configWithCashOutCommand(cashOutCommand);
        request.setStatus(Request.Status.PENDING);
        request = requestRepo.save(request);
        try {
            return s3pCashOut(request, cashOutCommand);
        } catch (ApiException e) {
            log.error(e.getResponseBody());
            throw new CustomException("Error During CashOut", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private String s3pCashOut(Request request, CashOutCommand cashOutCommand) throws ApiException {
        Set<Service> services = Constants.SERVICES;
        Integer sourceServiceId = services.stream()
                .filter(service -> service.getType() == Service.TypeEnum.CASHOUT
                        && service.getTitle().toLowerCase().contains(cashOutCommand.getSource().toLowerCase()))
                .mapToInt(Service::getServiceid).findFirst()
                .orElseThrow(() -> {
                    log.error("No Service with name {}.", cashOutCommand.getSource());
                    return new CustomException("No Service with the given name", HttpStatus.BAD_REQUEST);
                });
        Cashout cashout = collectionApi.cashoutGet(sourceServiceId).get(0);
        log.info("Initiating Quote Request for Cash out");
        QuoteRequest quoteRequest = new QuoteRequest();
        quoteRequest.setAmount(cashOutCommand.getAmountWithFee());
        quoteRequest.setPayItemId(cashout.getPayItemId());
        Quotestd offer = collectionApi.quotestdPost(quoteRequest);
        log.info("Successful Quote Cash Out Request");
        log.info("{}", offer);
        log.info("Initiating Collection for Cash Out");
        CollectionstdRequest collection = new CollectionstdRequest();
        collection.setCustomerPhonenumber(cashOutCommand.getSourceServiceNumber());
        collection.setCustomerEmailaddress(email);
        collection.setQuoteId(offer.getQuoteId());
        collection.setServiceNumber(cashOutCommand.getSourceServiceNumber());
        collection.setCustomerName("Lowe Florian");
        Collectionstd payment = collectionApi.collectstdPost(collection);
        log.info("Collection Cash Out Successful");
        request.setSourcePTN(payment.getPtn());
        requestRepo.save(request);
        return payment.getPtn();
    }

    @Override
    public Boolean isS3PCashOutSuccessful(String cashOutPtn) {
        log.debug("Verifying if transaction with PTN: {} is Successful", cashOutPtn);
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

    @Override
    public GeneralCardPaymentResponse initiateCardCashOut(CashOutCommand cashOutCommand) {
        log.info("Initiating Card Cash Out");
        HttpEntity<FlutterWaveChargeRequest> requestHttpEntity = new HttpEntity<>(new FlutterWaveChargeRequest(cashOutCommand.getSourceCardDetails().getEncryptedPayload()));
        final ResponseEntity<String> responseEntity = restTemplate.postForEntity(flutterWaveBaseUrl.concat("/charges?type=card"), requestHttpEntity, String.class);
        try {
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                final ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(responseEntity.getBody());
                final String mode = jsonNode.get("meta").get("authorization").get("mode").asText();
                final GeneralCardPaymentResponse paymentResponse = new GeneralCardPaymentResponse();
                paymentResponse.setMode(mode);
                if (mode.equals("redirect")) {
                    String transactionId = jsonNode.get("data").get("id").asText();
                    String redirect = jsonNode.get("meta").get("authorization").get("redirect").asText();
                    paymentResponse.setTransactionId(transactionId);
                    paymentResponse.setRedirect(redirect);

                    log.info("Saving Request");
                    Request request = new Request();
                    request.configWithCashOutCommand(cashOutCommand);
                    request.setStatus(Request.Status.PENDING);
                    request.setSourcePTN(transactionId);
                    requestRepo.save(request);
                }
                log.info("Completed Card Cashout request");
                return paymentResponse;
            }
        } catch (JsonProcessingException e) {
            log.error("Error Occurred during Cash Out");
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public CardPaymentPinResponse authenticateCashOutWithPin(CashOutCommand cashOutCommand) {
        log.info("Authenticating Cash Out With PIN");
        Request request = new Request();
        request.configWithCashOutCommand(cashOutCommand);
        request.setStatus(Request.Status.PENDING);
        requestRepo.save(request);
        HttpEntity<FlutterWaveChargeRequest> requestHttpEntity = new HttpEntity<>(new FlutterWaveChargeRequest(cashOutCommand.getSourceCardDetails().getEncryptedPayload()));
        final ResponseEntity<String> responseEntity = restTemplate.postForEntity(flutterWaveBaseUrl.concat("/charges?type=card"), requestHttpEntity, String.class);
        try {
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                final ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(responseEntity.getBody());
                final String flutterWaveRef = jsonNode.get("data").get("flw_ref").asText();
                final String message = jsonNode.get("data").get("processor_response").asText();
                final String txId = jsonNode.get("data").get("id").asText();
                final boolean isSuccessful = jsonNode.get("data").get("status").asText().equals("successful");
                request.setSourcePTN(txId);
                requestRepo.save(request);
                log.info("Authenticated Completed");
                return new CardPaymentPinResponse(flutterWaveRef, message, txId, isSuccessful);
            }
        } catch (JsonProcessingException e) {
            log.error("Authentication Failed");
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public String validateCardPayment(FlutterWaveValidationCmd flutterWaveValidationCmd) {
        log.info("Validating Payment.");
        HttpEntity<FlutterWaveValidationCmd> requestHttpEntity = new HttpEntity<>(flutterWaveValidationCmd);
        final ResponseEntity<String> responseEntity = restTemplate.postForEntity(flutterWaveBaseUrl.concat("/validate-charge"), requestHttpEntity, String.class);
        try {
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                final ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(responseEntity.getBody());
                log.info("Completed Payment Validation");
                return jsonNode.get("data").get("status").asText();
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public Boolean isCardTransactionSuccessful(String txId) {
        log.info("Verify Transaction with Id: {}", txId);
        final ResponseEntity<String> responseEntity = restTemplate.getForEntity(flutterWaveBaseUrl.concat("/transactions/{tx_id}/verify"), String.class, txId);
        try {
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                final ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(responseEntity.getBody());
                final boolean isSuccessful = jsonNode.get("data").get("status").asText().equals("successful");
                if (isSuccessful) {
                    final Request request = requestRepo.findBySourcePTN(txId);
                    if (request == null) {
                        log.error("A Request with the given transaction Id is supposed to exist.");
                        throw new CustomException("Invalid Transaction ID", HttpStatus.BAD_REQUEST);
                    }
                    request.setStatus(Request.Status.SUCCESS);
                    requestRepo.save(request);
                }
                log.info("Status of transaction with Id : {}",isSuccessful);
                return isSuccessful;
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return false;
    }


    @Getter
    @Setter
    @NoArgsConstructor
    static class FlutterWaveChargeRequest {
        private String client;

        FlutterWaveChargeRequest(String client) {
            this.client = client;
        }
    }


}
