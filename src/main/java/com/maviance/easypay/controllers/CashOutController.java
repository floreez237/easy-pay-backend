package com.maviance.easypay.controllers;

import com.maviance.easypay.commands.CashOutCommand;
import com.maviance.easypay.commands.FlutterWaveValidationCmd;
import com.maviance.easypay.model.CardPaymentPinResponse;
import com.maviance.easypay.model.GeneralCardPaymentResponse;
import com.maviance.easypay.services.interfaces.CashOutService;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/v1/cashout")
public class CashOutController {
    private final CashOutService cashOutService;

    public CashOutController(CashOutService cashOutService) {
        this.cashOutService = cashOutService;
    }

    @ApiOperation(value = "Initiate an S3P Cash Out", response = String.class)
    @PostMapping
    public String cashOut(@RequestBody CashOutCommand cashOutCommand) {
        return cashOutService.s3pCashOut(cashOutCommand);
    }

    @ApiOperation(value = "Check If Cash Out is Completed", response = Boolean.class)
    @GetMapping("/success/{cashOutPtn}")
    public Boolean checkS3pCashOutStatus(@PathVariable String cashOutPtn) {
        return cashOutService.isS3PCashOutSuccessful(cashOutPtn);
    }

    @ApiOperation(value = "Initiate a FlutterWave Card Payment", response = GeneralCardPaymentResponse.class)
    @PostMapping("/card/pay")
    public GeneralCardPaymentResponse initiatePayment(@RequestBody CashOutCommand cashOutCommand) {
        return cashOutService.initiateCardCashOut(cashOutCommand);
    }

    @ApiOperation(value = "Initiate a FlutterWave Card Payment with PIN", response = CardPaymentPinResponse.class)
    @PostMapping("/card/pay/pin")
    public CardPaymentPinResponse authenticateCashOutPinPayment(@RequestBody CashOutCommand cashOutCommand) {
        return cashOutService.authenticateCashOutWithPin(cashOutCommand);
    }

    @ApiOperation(value = "Validate FlutterWave Card Payment", response = String.class)
    @PostMapping("/card/validate")
    public String validateCardPayment(@RequestBody FlutterWaveValidationCmd flutterWaveValidationCmd) {
        return cashOutService.validateCardPayment(flutterWaveValidationCmd);
    }

    @ApiOperation(value = "Check If FlutterWave Card Payment is Successful", response = Boolean.class)
    @GetMapping("/card/success/{transactionId}")
    public Boolean isCardTransactionSuccessful(@PathVariable String transactionId) {
        return cashOutService.isCardTransactionSuccessful(transactionId);
    }


}
