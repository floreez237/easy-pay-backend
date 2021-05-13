package com.maviance.easypay.commands;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BillFetchCommand {
    private String provider;
    private String serviceNumber;
}
