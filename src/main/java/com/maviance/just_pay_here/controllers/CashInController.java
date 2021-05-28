package com.maviance.just_pay_here.controllers;

import com.maviance.just_pay_here.commands.CashInCommand;
import com.maviance.just_pay_here.services.interfaces.CashInService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

@Api(value = "Cash-In")
@RestController
@RequestMapping(value = "/v1/cashin")
public class CashInController {
    private final CashInService cashInService;

    public CashInController(CashInService cashInService) {
        this.cashInService = cashInService;
    }

    @ApiOperation(value = "Reimburse an S3P Cash Out", response = String.class,tags = "Cash In")
    @PostMapping("/reimburse/{cashOutPtn}")
    public String reimburse(@PathVariable String cashOutPtn) {
        return cashInService.s3pReimburse(cashOutPtn);
    }

    @ApiOperation(value = "Initiate an S3P Cash In", response = String.class,tags = "Cash In")
    @PostMapping("/{cashOutPtn}")
    public String fundTransferCashIn(@RequestBody CashInCommand cashInCommand, @PathVariable String cashOutPtn) {
        return cashInService.fundTransferCashIn(cashInCommand, cashOutPtn);
    }
}
