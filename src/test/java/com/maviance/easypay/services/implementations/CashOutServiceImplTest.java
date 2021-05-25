package com.maviance.easypay.services.implementations;

import com.maviance.easypay.commands.CashOutCommand;
import com.maviance.easypay.config.Checks;
import com.maviance.easypay.exceptions.CustomException;
import com.maviance.easypay.model.Request;
import com.maviance.easypay.repositories.RequestRepo;
import com.maviance.easypay.services.interfaces.CashOutService;
import com.maviance.easypay.utils.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.maviance.s3pjavaclient.ApiException;
import org.maviance.s3pjavaclient.api.CollectionApi;
import org.maviance.s3pjavaclient.api.HistoryApi;
import org.maviance.s3pjavaclient.model.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {CashOutServiceImpl.class})
class CashOutServiceImplTest {

    @MockBean
    private RequestRepo requestRepo;

    @MockBean
    private Checks checks;

    @MockBean
    private HistoryApi historyApi;

    @MockBean
    private CollectionApi collectionApi;

    @Autowired
    private CashOutService cashOutService;
    private final Historystd historystd = new Historystd();
    private final String cashoutptn = "cashoutptn";

    @BeforeEach
    void setUp() {
        historystd.setStatus(Historystd.StatusEnum.SUCCESS);
    }

    @Test
    void cashOut() throws ApiException {
        CashOutCommand cashOutCommand = new CashOutCommand("orange Money", "698223844", null, 200.0f);
        Request request = new Request();
        request.configWithCashOutCommand(cashOutCommand);

        Service service = new Service();
        service.setType(Service.TypeEnum.CASHOUT);
        service.setTitle("Orange Money");
        service.setServiceid(1);
        Constants.SERVICES.add(service);

        Cashout cashout = new Cashout();
        cashout.setPayItemId("1");

        Mockito.when(collectionApi.cashoutGet(1)).thenReturn(Collections.singletonList(cashout));

        Quotestd offer = new Quotestd();
        offer.setQuoteId("1");
        Mockito.when(collectionApi.quotestdPost(Mockito.any())).thenReturn(offer);

        Collectionstd payment = new Collectionstd();
        payment.setPtn("ptn1");
        Mockito.when(collectionApi.collectstdPost(Mockito.any())).thenReturn(payment);

        Mockito.when(requestRepo.save(Mockito.any())).thenReturn(request);

        String ptn = cashOutService.s3pCashOut(cashOutCommand);
        assertEquals("ptn1", ptn);

    }

    @Test
    void cashout_NoMatchingServiceID() {
        CashOutCommand cashOutCommand = new CashOutCommand("orange Money", "698223844", null, 200.0f);
        Request request = new Request();
        request.configWithCashOutCommand(cashOutCommand);

        Mockito.when(requestRepo.save(Mockito.any())).thenReturn(request);

        CustomException exception = assertThrows(CustomException.class, () -> cashOutService.s3pCashOut(cashOutCommand));
        assertEquals(HttpStatus.BAD_REQUEST,exception.getHttpStatus());
    }

    @Test
    void isCashOutSuccessful_S3pAvailable() throws ApiException {
        Mockito.when(historyApi.historystdGet(cashoutptn, null, null, null))
                .thenReturn(Collections.singletonList(historystd));
        Mockito.when(checks.isS3pAvailable()).thenReturn(true);

        boolean isCashoutSuccessful = cashOutService.isS3PCashOutSuccessful(cashoutptn);
        assertTrue(isCashoutSuccessful);
    }

    @Test
    void isCashOutSuccessful_S3pUnavailable() {
        Mockito.when(checks.isS3pAvailable()).thenReturn(false);
        CustomException exception = assertThrows(CustomException.class, () -> cashOutService.isS3PCashOutSuccessful(cashoutptn));
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, exception.getHttpStatus());
    }

    @Test
    void isCashOutSuccessful_S3pFails() throws ApiException {
        Mockito.when(checks.isS3pAvailable()).thenReturn(true);
        Mockito.when(historyApi.historystdGet(cashoutptn, null, null, null))
                .thenThrow(new ApiException("History Error"));
        CustomException exception = assertThrows(CustomException.class, () -> cashOutService.isS3PCashOutSuccessful(cashoutptn));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getHttpStatus());
    }

}