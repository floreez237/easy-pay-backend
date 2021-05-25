package com.maviance.easypay.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GeneralCardPaymentResponse {
    private String transactionId;
    private String mode;
    private String redirect;
}
