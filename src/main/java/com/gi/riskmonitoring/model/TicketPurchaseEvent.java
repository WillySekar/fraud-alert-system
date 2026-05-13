package com.gi.riskmonitoring.model;


import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class TicketPurchaseEvent {

    private String userId;
    private double amount;
    private long timestamp;
}
