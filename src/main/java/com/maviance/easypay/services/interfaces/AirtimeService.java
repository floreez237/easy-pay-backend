package com.maviance.easypay.services.interfaces;

import com.maviance.easypay.commands.AirtimeCmd;

public interface AirtimeService {
    String executeTopup(AirtimeCmd airtimeCmd);
}
