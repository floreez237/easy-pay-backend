package com.maviance.easypay.commands;

import com.maviance.easypay.model.CardDetails;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Getter
public class BillPaymentCmd extends BaseCmd {
    private final String contractNumber;

    public BillPaymentCmd(String source, String sourceServiceNumber, String destination, String destinationServiceNumber,
                          Float amount, CardDetails sourceCardDetails, String contractNumber) {
        super(source, sourceServiceNumber, destination, destinationServiceNumber, amount, sourceCardDetails);
        this.contractNumber = contractNumber;
    }
}
