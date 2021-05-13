package com.maviance.easypay.services.implementations;

import com.maviance.easypay.commands.BillFetchCommand;
import com.maviance.easypay.model.BillInfo;
import com.maviance.easypay.services.interfaces.BillPaymentService;

import java.util.List;

public class BillPaymentServiceImpl implements BillPaymentService {
    @Override
    public List<BillInfo> getAllOpenBills(BillFetchCommand billFetchCommand) {

    }
}
