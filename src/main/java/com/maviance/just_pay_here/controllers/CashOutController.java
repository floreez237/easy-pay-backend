package com.maviance.just_pay_here.controllers;

import com.maviance.just_pay_here.commands.CashOutCommand;
import com.maviance.just_pay_here.commands.FlutterWaveValidationCmd;
import com.maviance.just_pay_here.model.CardPaymentPinResponse;
import com.maviance.just_pay_here.model.GeneralCardPaymentResponse;
import com.maviance.just_pay_here.services.interfaces.CashOutService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

@Api(value = "Cash-Out")
@RestController
@RequestMapping(value = "/v1/cashout")
public class CashOutController {
    private final CashOutService cashOutService;

    public CashOutController(CashOutService cashOutService) {
        this.cashOutService = cashOutService;
    }

    @ApiOperation(value = "Initiate an S3P Cash Out", response = String.class,tags = "Cash Out")
    @PostMapping
    public String cashOut(@RequestBody CashOutCommand cashOutCommand) {
        return cashOutService.s3pCashOut(cashOutCommand);
    }

    @ApiOperation(value = "Check If Cash Out is Completed", response = Boolean.class,tags = "Cash Out")
    @GetMapping("/success/{cashOutPtn}")
    public Boolean checkS3pCashOutStatus(@PathVariable String cashOutPtn) {
        return cashOutService.isS3PCashOutSuccessful(cashOutPtn);
    }

    @ApiOperation(value = "Initiate a FlutterWave Card Payment", response = GeneralCardPaymentResponse.class,tags = "Cash Out")
    @PostMapping("/card/pay")
    public GeneralCardPaymentResponse initiatePayment(@RequestBody CashOutCommand cashOutCommand) {
        return cashOutService.initiateCardCashOut(cashOutCommand);
    }

    @ApiOperation(value = "Initiate a FlutterWave Card Payment with PIN", response = CardPaymentPinResponse.class,tags = "Cash Out")
    @PostMapping("/card/pay/pin")
    public CardPaymentPinResponse authenticateCashOutPinPayment(@RequestBody CashOutCommand cashOutCommand) {
        return cashOutService.authenticateCashOutWithPin(cashOutCommand);
    }

    @ApiOperation(value = "Validate FlutterWave Card Payment", response = String.class,tags = "Cash Out")
    @PostMapping("/card/validate")
    public String validateCardPayment(@RequestBody FlutterWaveValidationCmd flutterWaveValidationCmd) {
        return cashOutService.validateCardPayment(flutterWaveValidationCmd);
    }

    @ApiOperation(value = "Check If FlutterWave Card Payment is Successful", response = Boolean.class,tags = "Cash Out")
    @GetMapping("/card/success/{transactionId}")
    public Boolean isCardTransactionSuccessful(@PathVariable String transactionId) {
        return cashOutService.isCardTransactionSuccessful(transactionId);
    }


}
