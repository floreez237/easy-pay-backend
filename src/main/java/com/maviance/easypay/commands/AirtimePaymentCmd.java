package com.maviance.easypay.commands;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Map;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
public class AirtimePaymentCmd extends BasePaymentCmd {

    public AirtimePaymentCmd(String destination, String destinationServiceNumber, Float amount) {
        super(destination, destinationServiceNumber, amount);
    }

    public AirtimePaymentCmd(Map<String, String> airtimeRequestParams) {
        super(airtimeRequestParams.get("destination"),airtimeRequestParams.get("destinationServiceNumber"),
                Float.valueOf(airtimeRequestParams.get("amount")));

    }
}
