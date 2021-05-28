package com.maviance.just_pay_here.commands;

import com.maviance.just_pay_here.model.CardDetails;
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
