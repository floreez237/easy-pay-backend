package com.maviance.easypay.controllers;

import com.maviance.easypay.commands.CashInCommand;
import com.maviance.easypay.services.interfaces.CashInService;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/v1/cashin")
public class CashInController {
    private final CashInService cashInService;

    public CashInController(CashInService cashInService) {
        this.cashInService = cashInService;
    }

    @ApiOperation(value = "Reimburse an S3P Cash Out", response = String.class)
    @PostMapping("/reimburse/{cashOutPtn}")
    public String reimburse(@PathVariable String cashOutPtn) {
        return cashInService.s3pReimburse(cashOutPtn);
    }

    @ApiOperation(value = "Initiate an S3P Cash In", response = String.class)
    @PostMapping("/{cashOutPtn}")
    public String fundTransferCashIn(@RequestBody CashInCommand cashInCommand, @PathVariable String cashOutPtn) {
        return cashInService.fundTransferCashIn(cashInCommand, cashOutPtn);
    }
}
