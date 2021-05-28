package com.maviance.just_pay_here.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Embeddable;
import javax.persistence.Transient;


@Embeddable
@Data
@NoArgsConstructor
@Slf4j
public class CardDetails {
    private String cardholderName;
    private String cardholderEmail;
    @Transient
    private String encryptedPayload;
}
