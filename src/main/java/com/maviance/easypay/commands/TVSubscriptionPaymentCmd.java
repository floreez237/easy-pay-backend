package com.maviance.easypay.commands;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Getter
public class TVSubscriptionPaymentCmd extends BasePaymentCmd {
    private final String planId;


    public TVSubscriptionPaymentCmd(String destination, String destinationServiceNumber, Float amount, String planId) {
        super(destination, destinationServiceNumber, amount);
        this.planId = planId;
    }
}
