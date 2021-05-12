package com.maviance.easypay.commands;

import lombok.*;

@AllArgsConstructor
@Getter
@ToString
@NoArgsConstructor
@Setter
public abstract class BasePaymentCmd {
    private  String destination;
    private  String destinationServiceNumber;
    private  Float amount;
}
