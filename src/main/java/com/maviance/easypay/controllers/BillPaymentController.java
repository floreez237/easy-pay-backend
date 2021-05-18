package com.maviance.easypay.controllers;

import com.maviance.easypay.commands.BillFetchCommand;
import com.maviance.easypay.commands.BillPaymentCmd;
import com.maviance.easypay.model.BillInfo;
import com.maviance.easypay.services.interfaces.BillPaymentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/bills")
public class BillPaymentController {

    private final BillPaymentService billPaymentService;

    public BillPaymentController(BillPaymentService billPaymentService) {
        this.billPaymentService = billPaymentService;
    }

    @PostMapping("/fetch")
    public List<BillInfo> fetchAllOpenBills(@RequestBody BillFetchCommand billFetchCommand) {
        return billPaymentService.getAllOpenBills(billFetchCommand);
    }

    @PostMapping("/pay/{sourcePtn}")
    public String payBill(@RequestBody BillPaymentCmd billPaymentCmd, @PathVariable String sourcePtn) {
        return billPaymentService.executeBillPayment(billPaymentCmd, sourcePtn);
    }
}
