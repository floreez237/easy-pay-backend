package com.maviance.easypay.controllers;

import com.maviance.easypay.services.interfaces.CashInService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/v1/cashin")
public class CashInController {
    private final CashInService cashInService;

    public CashInController(CashInService cashInService) {
        this.cashInService = cashInService;
    }

    @PostMapping("/{cashOutPtn}")
    public String reimburse(@PathVariable String cashOutPtn) {
        return cashInService.reimburse(cashOutPtn);
    }
}
