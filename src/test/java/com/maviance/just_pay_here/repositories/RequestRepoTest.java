package com.maviance.just_pay_here.repositories;

import com.maviance.just_pay_here.model.Request;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class RequestRepoTest {

    @Autowired
    private RequestRepo requestRepo;

    @BeforeEach
    void setUp() {
        Request request1 = new Request();
        request1.setSourcePTN("abc");
        request1.setStatus(Request.Status.SUCCESS);

        Request request2 = new Request();
        request2.setSourcePTN("ab");
        request2.setStatus(Request.Status.SUCCESS);
        requestRepo.saveAll(Arrays.asList(request1, request2));
    }

    @Test
    void findBySourcePTN() {
        Request request = requestRepo.findBySourcePTN("abc");
        assertNotNull(request);
        assertEquals(Request.Status.SUCCESS,request.getStatus());
    }
}