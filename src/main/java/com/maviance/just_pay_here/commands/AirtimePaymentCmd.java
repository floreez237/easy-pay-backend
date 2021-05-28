package com.maviance.just_pay_here.commands;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class AirtimePaymentCmd extends BasePaymentCmd {

    public AirtimePaymentCmd(String destination, String destinationServiceNumber, Float amount) {
        super(destination, destinationServiceNumber, amount);
    }
}
