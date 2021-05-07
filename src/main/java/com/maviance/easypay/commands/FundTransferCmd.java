package com.maviance.easypay.commands;

import com.maviance.easypay.model.CardDetails;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.Value;

@EqualsAndHashCode(callSuper = true)
@Getter
@ToString(callSuper = true)
public class FundTransferCmd extends BaseCmd {
    private final CardDetails destinationDetails;

    public FundTransferCmd(String source, String sourceServiceNumber, String destination, String destinationServiceNumber, Float amount, CardDetails sourceCardDetails, CardDetails destinationDetails) {
        super(source, sourceServiceNumber, destination, destinationServiceNumber, amount, sourceCardDetails);
        this.destinationDetails = destinationDetails;
    }


}
