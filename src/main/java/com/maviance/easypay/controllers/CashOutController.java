package com.maviance.easypay.controllers;

import com.maviance.easypay.commands.CashOutCommand;
import com.maviance.easypay.model.CardDetails;
import com.maviance.easypay.services.interfaces.CashOutService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(value = "/v1/cashout")
public class CashOutController {
    private final CashOutService cashOutService;

    public CashOutController(CashOutService cashOutService) {
        this.cashOutService = cashOutService;
    }

    @PostMapping
    public String cashOut(@RequestBody CashOutCommand cashOutCommand) {
        return cashOutService.cashOut(cashOutCommand);
    }

    @GetMapping("/success/{cashOutPtn}")
    public Boolean cashOut(@PathVariable String cashOutPtn) {
        return cashOutService.isCashOutSuccessful(cashOutPtn);
    }
}
