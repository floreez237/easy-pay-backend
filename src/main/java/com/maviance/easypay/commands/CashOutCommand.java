package com.maviance.easypay.commands;

import com.maviance.easypay.model.CardDetails;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Getter
@NoArgsConstructor
public class CashOutCommand {
    private String source;
    private String sourceServiceNumber;
    private CardDetails sourceCardDetails;
    private Float amountWithFee;

}
