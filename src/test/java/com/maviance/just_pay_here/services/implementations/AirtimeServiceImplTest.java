package com.maviance.just_pay_here.services.implementations;

import com.maviance.just_pay_here.commands.AirtimePaymentCmd;
import com.maviance.just_pay_here.config.Checks;
import com.maviance.just_pay_here.exceptions.CustomException;
import com.maviance.just_pay_here.model.Request;
import com.maviance.just_pay_here.repositories.RequestRepo;
import com.maviance.just_pay_here.services.interfaces.AirtimeService;
import com.maviance.just_pay_here.utils.Constants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.maviance.s3pjavaclient.ApiException;
import org.maviance.s3pjavaclient.api.CollectionApi;
import org.maviance.s3pjavaclient.model.Collectionstd;
import org.maviance.s3pjavaclient.model.Quotestd;
import org.maviance.s3pjavaclient.model.Service;
import org.maviance.s3pjavaclient.model.Topup;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {AirtimeServiceImpl.class})
class AirtimeServiceImplTest {
    @MockBean
    private RequestRepo requestRepo;

    @MockBean
    private CollectionApi collectionApi;

    @MockBean
    private Checks checks;

    @Autowired
    private AirtimeService airtimeService;

    @Test
    void executeTopup_s3pAvailable() throws ApiException {
        AirtimePaymentCmd airtimeCommand = new AirtimePaymentCmd("orange", "698223844", 200.0f);
        Request request = new Request();
        request.configWithPaymentCommand(airtimeCommand);
        request.setRequestId(UUID.randomUUID());

        Service service = new Service();
        service.setType(Service.TypeEnum.TOPUP);
        service.setTitle("Orange");
        service.setServiceid(1);
        Constants.SERVICES.add(service);

        Topup topup = new Topup();
        topup.setPayItemId("1");

        Mockito.when(checks.isS3pAvailable()).thenReturn(true);
        Mockito.when(collectionApi.topupGet(1)).thenReturn(Collections.singletonList(topup));

        Quotestd offer = new Quotestd();
        offer.setQuoteId("1");
        Mockito.when(collectionApi.quotestdPost(Mockito.any())).thenReturn(offer);

        Collectionstd payment = new Collectionstd();
        payment.setPtn("ptn1");
        Mockito.when(collectionApi.collectstdPost(Mockito.any())).thenReturn(payment);

        Mockito.when(requestRepo.save(Mockito.any())).thenReturn(request);
        Mockito.when(requestRepo.findBySourcePTN("ptn1")).thenReturn(request);

        String requestId = airtimeService.executeTopup(airtimeCommand, "ptn1");
        assertNotNull(requestId);
    }

    @Test
    void executeTopup_s3pUnavailable() {
        Mockito.when(checks.isS3pAvailable()).thenReturn(false);
        CustomException exception = assertThrows(CustomException.class, () -> airtimeService.executeTopup(null, null));
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, exception.getHttpStatus());
    }

    @Test
    void executeTopup_s3pError() throws ApiException {
        Mockito.when(checks.isS3pAvailable()).thenReturn(true);
        AirtimePaymentCmd airtimeCommand = new AirtimePaymentCmd("orange", "698223844", 200.0f);
        Request request = new Request();
        request.configWithPaymentCommand(airtimeCommand);
        request.setRequestId(UUID.randomUUID());

        Service service = new Service();
        service.setType(Service.TypeEnum.TOPUP);
        service.setTitle("Orange");
        service.setServiceid(1);
        Constants.SERVICES.add(service);
        Mockito.when(collectionApi.topupGet(1)).thenThrow(ApiException.class);
        CustomException exception = assertThrows(CustomException.class, () -> airtimeService.executeTopup(airtimeCommand, null));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getHttpStatus());
    }
}