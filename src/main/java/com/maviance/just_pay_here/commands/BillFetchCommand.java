package com.maviance.just_pay_here.commands;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BillFetchCommand {
    private String provider;
    private String contractNumber;
}
