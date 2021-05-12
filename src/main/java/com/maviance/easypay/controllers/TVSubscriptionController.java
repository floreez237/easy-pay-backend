package com.maviance.easypay.controllers;

import com.maviance.easypay.commands.TVSubscriptionPaymentCmd;
import com.maviance.easypay.model.SubscriptionOffer;
import com.maviance.easypay.services.interfaces.TVSubscriptionService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/tv")
public class TVSubscriptionController {

    private final TVSubscriptionService tvSubscriptionService;

    public TVSubscriptionController(TVSubscriptionService tvSubscriptionService) {
        this.tvSubscriptionService = tvSubscriptionService;
    }

    @GetMapping("/{provider}")
    public List<SubscriptionOffer> getAllOffers(@PathVariable String provider) {
        return tvSubscriptionService.getAllOffers(provider);
    }

    @PostMapping(value = "/pay/{cashOutPtn}",consumes = MediaType.APPLICATION_JSON_VALUE)
    public String payTvOffer(@RequestBody TVSubscriptionPaymentCmd tvSubscriptionPaymentCmd, @PathVariable String cashOutPtn) {
        return tvSubscriptionService.payTvOffer(tvSubscriptionPaymentCmd, cashOutPtn);
    }
}
