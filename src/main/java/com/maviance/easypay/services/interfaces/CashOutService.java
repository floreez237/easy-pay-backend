package com.maviance.easypay.services.interfaces;

import com.maviance.easypay.commands.CashOutCommand;
import com.maviance.easypay.commands.FlutterWaveValidationCmd;
import com.maviance.easypay.model.CardPaymentPinResponse;
import com.maviance.easypay.model.GeneralCardPaymentResponse;

public interface CashOutService {
    String s3pCashOut(CashOutCommand cashOutCommand);

    Boolean isS3PCashOutSuccessful(String cashOutPtn);

    GeneralCardPaymentResponse initiateCardCashOut(CashOutCommand cashOutCommand);

    CardPaymentPinResponse authenticateCashOutWithPin(CashOutCommand cashOutCommand);

    String validateCardPayment(FlutterWaveValidationCmd flutterWaveValidationCmd);

    Boolean isCardTransactionSuccessful(String txId);
}
