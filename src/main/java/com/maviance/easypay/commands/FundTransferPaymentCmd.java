package com.maviance.easypay.commands;

import com.maviance.easypay.model.CardDetails;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@Getter
@ToString(callSuper = true)
public class FundTransferPaymentCmd extends BasePaymentCmd {
    private final CardDetails destinationDetails;


    public FundTransferPaymentCmd(String destination, String destinationServiceNumber, Float amount, CardDetails destinationDetails) {
        super(destination, destinationServiceNumber, amount);
        this.destinationDetails = destinationDetails;
    }
}
