package com.maviance.just_pay_here.commands;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Getter
@NoArgsConstructor
public class BillPaymentCmd extends BasePaymentCmd {
    private String billPayItemId;

    public BillPaymentCmd(String destination, String destinationServiceNumber, Float amount, String billPayItemId) {
        super(destination, destinationServiceNumber, amount);
        this.billPayItemId = billPayItemId;
    }
}
