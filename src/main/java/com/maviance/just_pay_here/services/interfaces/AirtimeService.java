package com.maviance.just_pay_here.services.interfaces;

import com.maviance.just_pay_here.commands.AirtimePaymentCmd;

public interface AirtimeService {
    String executeTopup(AirtimePaymentCmd airtimeCmd, String sourcePTN);
}
