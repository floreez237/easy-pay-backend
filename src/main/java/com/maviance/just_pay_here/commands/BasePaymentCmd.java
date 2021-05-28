package com.maviance.just_pay_here.commands;

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
