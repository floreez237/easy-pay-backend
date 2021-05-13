package com.maviance.easypay.services.interfaces;

import com.maviance.easypay.commands.CashInCommand;

public interface CashInService {
    public String reimburse(String cashOutPTN);

    String fundTransferCashIn(CashInCommand cashInCommand, String sourcePTN);
}
