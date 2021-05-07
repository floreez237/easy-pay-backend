package com.maviance.easypay.controllers;

import com.maviance.easypay.commands.AirtimePaymentCmd;
import com.maviance.easypay.services.interfaces.AirtimeService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/airtime")
public class AirtimeController {
    private final AirtimeService airtimeService;

    public AirtimeController(AirtimeService airtimeService) {
        this.airtimeService = airtimeService;
    }


    @PostMapping("/topup/{sourcePTN}")
    public String topup(@RequestBody AirtimePaymentCmd airtimeCmd, @PathVariable String sourcePTN) {
        return airtimeService.executeTopup(airtimeCmd, sourcePTN);
    }
}