package com.maviance.easypay.controllers;

import com.maviance.easypay.commands.CashOutCommand;
import com.maviance.easypay.services.interfaces.CashOutService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/cashin")
public class CashOutController {
    private final CashOutService cashOutService;

    public CashOutController(CashOutService cashOutService) {
        this.cashOutService = cashOutService;
    }

    @PostMapping
    public String cashIn(@RequestBody CashOutCommand cashOutCommand) {
        return cashOutService.cashOut(cashOutCommand);
    }

    @PostMapping("/{cashOutPtn}")
    public Boolean cashIn(@PathVariable String cashOutPtn) {
        return cashOutService.isCashOutSuccessful(cashOutPtn);
    }
}
