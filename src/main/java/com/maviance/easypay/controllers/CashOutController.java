package com.maviance.easypay.controllers;

import com.maviance.easypay.commands.CashOutCommand;
import com.maviance.easypay.commands.FlutterWaveValidationCmd;
import com.maviance.easypay.model.CardPaymentPinResponse;
import com.maviance.easypay.model.GeneralCardPaymentResponse;
import com.maviance.easypay.services.interfaces.CashOutService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/v1/cashout")
public class CashOutController {
    private final CashOutService cashOutService;

    public CashOutController(CashOutService cashOutService) {
        this.cashOutService = cashOutService;
    }

    @PostMapping
    public String cashOut(@RequestBody CashOutCommand cashOutCommand) {
        return cashOutService.s3pCashOut(cashOutCommand);
    }

    @GetMapping("/success/{cashOutPtn}")
    public Boolean checkS3pCashOutStatus(@PathVariable String cashOutPtn) {
        return cashOutService.isS3PCashOutSuccessful(cashOutPtn);
    }

    @PostMapping("/card/pay")
    public GeneralCardPaymentResponse initiatePayment(@RequestBody CashOutCommand cashOutCommand) {
        return cashOutService.initiateCardCashOut(cashOutCommand);
    }

    @PostMapping("/card/pin")
    public CardPaymentPinResponse authenticateCashOutPinPayment(@RequestBody CashOutCommand cashOutCommand) {
        return cashOutService.authenticateCashOutWithPin(cashOutCommand);
    }

    @PostMapping("/card/validate")
    public String validateCardPayment(@RequestBody FlutterWaveValidationCmd flutterWaveValidationCmd) {
        return cashOutService.validateCardPayment(flutterWaveValidationCmd);
    }

    @GetMapping("/card/success/{transactionId}")
    public Boolean isCardTransactionSuccessful(@PathVariable String transactionId) {
        return cashOutService.isCardTransactionSuccessful(transactionId);
    }


}
