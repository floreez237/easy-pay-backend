package com.maviance.easypay.commands;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Getter
public class BillPaymentCmd extends BasePaymentCmd {
    private final String contractNumber;

    public BillPaymentCmd(String destination, String destinationServiceNumber, Float amount, String contractNumber) {
        super(destination, destinationServiceNumber, amount);
        this.contractNumber = contractNumber;
    }
}