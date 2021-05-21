package com.maviance.easypay.services.interfaces;

import com.maviance.easypay.commands.CashOutCommand;

public interface CashOutService {
    String s3pCashOut(CashOutCommand cashOutCommand);

    Boolean isCashOutSuccessful(String cashOutPtn);

    String initiateCardCashOut(String encryptedPayload);
}
