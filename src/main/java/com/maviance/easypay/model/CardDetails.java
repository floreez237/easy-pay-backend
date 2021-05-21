package com.maviance.easypay.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Embeddable;


@Embeddable
@Data
@NoArgsConstructor
@Slf4j
public class CardDetails {
    private Integer cvc;
    private String cardholderName;
    private int expiryMonth;
    private int expiryYear;
    private String encryptedPayload;

}
