package com.maviance.easypay.commands;

import com.maviance.easypay.model.CardDetails;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CashOutCommand {
    private final String source;
    private final String sourceServiceNumber;
    private final CardDetails sourceCardDetails;
    private final Float amountWithFee;
}
