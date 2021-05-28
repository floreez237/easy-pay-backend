package com.maviance.just_pay_here.services.interfaces;

import com.maviance.just_pay_here.commands.BillFetchCommand;
import com.maviance.just_pay_here.commands.BillPaymentCmd;
import com.maviance.just_pay_here.model.BillInfo;

import java.util.List;

public interface BillPaymentService {
    List<BillInfo> getAllOpenBills(BillFetchCommand billFetchCommand);

    String executeBillPayment(BillPaymentCmd billPaymentCmd, String sourcePTN);

}
