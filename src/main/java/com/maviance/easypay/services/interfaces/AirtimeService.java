package com.maviance.easypay.services.interfaces;

import com.maviance.easypay.commands.AirtimePaymentCmd;

public interface AirtimeService {
    String executeTopup(AirtimePaymentCmd airtimeCmd, String sourcePTN);
}
