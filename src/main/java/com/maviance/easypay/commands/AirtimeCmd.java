package com.maviance.easypay.commands;

import com.maviance.easypay.model.CardDetails;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
public class AirtimeCmd extends BaseCmd{
    public AirtimeCmd(String source, String sourceServiceNumber, String destination, String destinationServiceNumber, Float amount, CardDetails sourceCardDetails) {
        super(source, sourceServiceNumber, destination, destinationServiceNumber, amount, sourceCardDetails);
    }
}
