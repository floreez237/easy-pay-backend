package com.maviance.easypay.commands;

import com.maviance.easypay.model.CardDetails;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.smartcardio.Card;
import java.util.Map;

@AllArgsConstructor
@Getter
public class CashOutCommand {
    private final String source;
    private final String sourceServiceNumber;
    private final CardDetails sourceCardDetails;
    private final Float amountWithFee;

    public CashOutCommand(Map<String, String> cashOutCommandMap, CardDetails sourceCardDetails) {
        this(cashOutCommandMap.get("source"),cashOutCommandMap.get("sourceServiceNumber"),
                sourceCardDetails,Float.valueOf(cashOutCommandMap.get("amountWithFee")));
    }
}
