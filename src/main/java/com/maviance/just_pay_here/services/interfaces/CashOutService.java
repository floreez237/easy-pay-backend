package com.maviance.just_pay_here.services.interfaces;

import com.maviance.just_pay_here.commands.CashOutCommand;
import com.maviance.just_pay_here.commands.FlutterWaveValidationCmd;
import com.maviance.just_pay_here.model.CardPaymentPinResponse;
import com.maviance.just_pay_here.model.GeneralCardPaymentResponse;

public interface CashOutService {
    String s3pCashOut(CashOutCommand cashOutCommand);

    Boolean isS3PCashOutSuccessful(String cashOutPtn);

    GeneralCardPaymentResponse initiateCardCashOut(CashOutCommand cashOutCommand);

    CardPaymentPinResponse authenticateCashOutWithPin(CashOutCommand cashOutCommand);

    String validateCardPayment(FlutterWaveValidationCmd flutterWaveValidationCmd);

    Boolean isCardTransactionSuccessful(String txId);
}
