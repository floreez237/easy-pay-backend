package com.maviance.just_pay_here.commands;

import lombok.*;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Getter
@Setter
@NoArgsConstructor
public class TVSubscriptionPaymentCmd extends BasePaymentCmd {
    private String planId;
    private String notificationPhoneNumber;
    public TVSubscriptionPaymentCmd(String destination, String destinationServiceNumber, Float amount,
                                    String planId,String notificationPhoneNumber) {
        super(destination, destinationServiceNumber, amount);
        this.planId = planId;
        this.notificationPhoneNumber = notificationPhoneNumber;
    }

}
