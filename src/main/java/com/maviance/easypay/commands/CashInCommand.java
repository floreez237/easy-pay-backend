package com.maviance.easypay.commands;

import com.maviance.easypay.model.CardDetails;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@Getter
@ToString(callSuper = true)
@NoArgsConstructor
public class CashInCommand extends BasePaymentCmd {
    private CardDetails destinationCardDetails;


    public CashInCommand(String destination, String destinationServiceNumber, Float amount, CardDetails destinationCardDetails) {
        super(destination, destinationServiceNumber, amount);
        this.destinationCardDetails = destinationCardDetails;
    }
}
