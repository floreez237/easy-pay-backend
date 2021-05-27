package com.maviance.easypay.controllers;

import com.maviance.easypay.commands.BillFetchCommand;
import com.maviance.easypay.commands.BillPaymentCmd;
import com.maviance.easypay.model.BillInfo;
import com.maviance.easypay.services.interfaces.BillPaymentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(value = "Bill Payment")
@RestController
@RequestMapping("/v1/bills")
public class BillPaymentController {

    private final BillPaymentService billPaymentService;

    public BillPaymentController(BillPaymentService billPaymentService) {
        this.billPaymentService = billPaymentService;
    }
    @ApiOperation(value = "Fetch All Open Bills",response = BillInfo.class,responseContainer = "List")
    @PostMapping("/fetch")
    public List<BillInfo> fetchAllOpenBills(@RequestBody BillFetchCommand billFetchCommand) {
        return billPaymentService.getAllOpenBills(billFetchCommand);
    }

    @ApiOperation(value = "Initiate a Bill Payment",response = String.class)
    @PostMapping("/pay/{sourcePtn}")
    public String payBill(@RequestBody BillPaymentCmd billPaymentCmd, @PathVariable String sourcePtn) {
        return billPaymentService.executeBillPayment(billPaymentCmd, sourcePtn);
    }
}
