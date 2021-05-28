package com.maviance.just_pay_here.controllers;

import com.maviance.just_pay_here.commands.AirtimePaymentCmd;
import com.maviance.just_pay_here.services.interfaces.AirtimeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

@Api(value = "Airtime Topup")
@RestController()
@RequestMapping(value = "/v1/airtime")
public class AirtimeController {
    private final AirtimeService airtimeService;

    public AirtimeController(AirtimeService airtimeService) {
        this.airtimeService = airtimeService;
    }

    @ApiOperation(value = "Initiate an Airtime Topup", response = String.class,tags = "Airtime Topup")
    @PostMapping("/topup/{sourcePTN}")
    public String topup(@RequestBody AirtimePaymentCmd airtimePaymentCmd, @PathVariable String sourcePTN) {
        return airtimeService.executeTopup(airtimePaymentCmd, sourcePTN);
    }
}
