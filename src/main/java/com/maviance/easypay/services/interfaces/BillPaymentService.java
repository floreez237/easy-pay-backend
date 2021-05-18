package com.maviance.easypay.services.interfaces;

import com.maviance.easypay.commands.BillFetchCommand;
import com.maviance.easypay.commands.BillPaymentCmd;
import com.maviance.easypay.model.BillInfo;

import java.util.List;

public interface BillPaymentService {
    List<BillInfo> getAllOpenBills(BillFetchCommand billFetchCommand);

    String executeBillPayment(BillPaymentCmd billPaymentCmd, String sourcePTN);

}
