package com.maviance.easypay.controllers;

import com.maviance.easypay.commands.CashInCommand;
import com.maviance.easypay.services.interfaces.CashInService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/v1/cashin")
public class CashInController {
    private final CashInService cashInService;

    public CashInController(CashInService cashInService) {
        this.cashInService = cashInService;
    }

    @PostMapping("/reimburse/{cashOutPtn}")
    public String reimburse(@PathVariable String cashOutPtn) {
        return cashInService.reimburse(cashOutPtn);
    }

    @PostMapping("/{cashOutPtn}")
    public String fundTransferCashIn(@RequestBody CashInCommand cashInCommand, @PathVariable String cashOutPtn) {
        return cashInService.fundTransferCashIn(cashInCommand,cashOutPtn);
    }
}
