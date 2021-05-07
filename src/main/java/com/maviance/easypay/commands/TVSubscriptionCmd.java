package com.maviance.easypay.commands;

import com.maviance.easypay.model.CardDetails;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Getter
public class TVSubscriptionCmd extends BaseCmd {
    private final String planId;

    public TVSubscriptionCmd(String source, String sourceServiceNumber, String destination, String destinationServiceNumber,
                             Float amount, CardDetails sourceCardDetails, String planId) {
        super(source, sourceServiceNumber, destination, destinationServiceNumber, amount, sourceCardDetails);
        this.planId = planId;
    }
}
