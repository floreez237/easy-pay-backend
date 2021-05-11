package com.maviance.easypay.controllers;

import com.maviance.easypay.commands.CashOutCommand;
import com.maviance.easypay.model.CardDetails;
import com.maviance.easypay.services.interfaces.CashOutService;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.callback.CallbackHandler;
import java.util.Map;

@RestController
@RequestMapping(value = "/v1/cashout")
public class CashOutController {
    private final CashOutService cashOutService;

    public CashOutController(CashOutService cashOutService) {
        this.cashOutService = cashOutService;
    }

    @PostMapping
//    @CrossOrigin
    public String cashOut(@RequestParam Map<String, String> cashOutCommandMap) {
        CardDetails sourceCardDetails = null;
        if (cashOutCommandMap.containsKey("cvc")) {
        sourceCardDetails= new CardDetails(cashOutCommandMap);
        }
        CashOutCommand cashOutCommand = new CashOutCommand(cashOutCommandMap,sourceCardDetails);
        return cashOutService.cashOut(cashOutCommand);
    }

    @GetMapping("/success/{cashOutPtn}")
    public Boolean cashOut(@PathVariable String cashOutPtn) {
        return cashOutService.isCashOutSuccessful(cashOutPtn);
    }
}
