package com.maviance.easypay.controllers;

import com.maviance.easypay.commands.TVSubscriptionPaymentCmd;
import com.maviance.easypay.model.SubscriptionOffer;
import com.maviance.easypay.services.interfaces.TVSubscriptionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(value = "TV Subscription")
@RestController
@RequestMapping("/v1/tv")
public class TVSubscriptionController {

    private final TVSubscriptionService tvSubscriptionService;

    public TVSubscriptionController(TVSubscriptionService tvSubscriptionService) {
        this.tvSubscriptionService = tvSubscriptionService;
    }

    @ApiOperation(value = "Get a List of all the TV Subscriptions Available", response = SubscriptionOffer.class,responseContainer = "List")
    @GetMapping("/{provider}")
    public List<SubscriptionOffer> getAllOffers(@PathVariable String provider) {
        return tvSubscriptionService.getAllOffers(provider);
    }

    @ApiOperation(value = "Initiate a TV Subscription Payment", response = String.class)
    @PostMapping(value = "/pay/{cashOutPtn}",consumes = MediaType.APPLICATION_JSON_VALUE)
    public String payTvOffer(@RequestBody TVSubscriptionPaymentCmd tvSubscriptionPaymentCmd, @PathVariable String cashOutPtn) {
        return tvSubscriptionService.payTvOffer(tvSubscriptionPaymentCmd, cashOutPtn);
    }
}
