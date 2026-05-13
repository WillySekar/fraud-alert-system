package com.gi.riskmonitoring.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class SuspiciousBetEvent {

    private String userId;
    private String reason;
    private String riskLevel;
    //private double transactionCount;

}
