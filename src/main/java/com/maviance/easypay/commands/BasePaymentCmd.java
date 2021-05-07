package com.maviance.easypay.commands;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public abstract class BasePaymentCmd {
    private final String destination;
    private final String destinationServiceNumber;
    private final Float amount;
}
