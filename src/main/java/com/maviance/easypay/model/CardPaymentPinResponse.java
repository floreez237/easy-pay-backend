package com.maviance.easypay.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardPaymentPinResponse {
    private String flutterWaveRef;
    private String message;
    private String id;
    private Boolean isSuccessful;
}
