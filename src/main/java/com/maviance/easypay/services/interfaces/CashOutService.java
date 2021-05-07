package com.maviance.easypay.services.interfaces;

import com.maviance.easypay.commands.CashOutCommand;

public interface CashOutService {
    String cashOut(CashOutCommand cashOutCommand);

    Boolean isCashOutSuccessful(String cashOutPtn);
}
