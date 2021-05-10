package com.maviance.easypay.controllers;

import com.maviance.easypay.commands.AirtimePaymentCmd;
import com.maviance.easypay.services.interfaces.AirtimeService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(value = "/v1/airtime")
public class AirtimeController {
    private final AirtimeService airtimeService;

    public AirtimeController(AirtimeService airtimeService) {
        this.airtimeService = airtimeService;
    }


    @PostMapping("/topup/{sourcePTN}")
    public String topup(@RequestParam Map<String,String> airtimeRequestParams, @PathVariable String sourcePTN) {
        AirtimePaymentCmd airtimeCmd = new AirtimePaymentCmd(airtimeRequestParams);
        return airtimeService.executeTopup(airtimeCmd, sourcePTN);
    }
}
