package com.maviance.easypay.commands;

import com.maviance.easypay.model.CardDetails;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public abstract class BaseCmd {
    private final String source;
    private final String sourceServiceNumber;
    private final String destination;
    private final String destinationServiceNumber;
    private final Float amount;
    private final CardDetails sourceCardDetails;

}
