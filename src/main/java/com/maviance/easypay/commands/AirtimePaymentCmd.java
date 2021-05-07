package com.maviance.easypay.commands;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
public class AirtimePaymentCmd extends BasePaymentCmd {

    public AirtimePaymentCmd(String destination, String destinationServiceNumber, Float amount) {
        super(destination, destinationServiceNumber, amount);
    }
}
