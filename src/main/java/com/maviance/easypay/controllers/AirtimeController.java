package com.maviance.easypay.controllers;

import com.maviance.easypay.commands.AirtimeCmd;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/airtime")
public class AirtimeController {



    @PostMapping("/topup")
    public String topup(@RequestBody AirtimeCmd airtimeCmd) {
        return null;
    }
}
