package com.maviance.just_pay_here.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardPaymentPinResponse {
    private String flutterWaveRef;
    private String message;
    private String transactionId;
    private Boolean isSuccessful;
    private String redirect;
}
