package com.maviance.just_pay_here.services.interfaces;

import com.maviance.just_pay_here.commands.CashInCommand;

public interface CashInService {
    public String s3pReimburse(String cashOutPTN);

    String fundTransferCashIn(CashInCommand cashInCommand, String sourcePTN);
}
