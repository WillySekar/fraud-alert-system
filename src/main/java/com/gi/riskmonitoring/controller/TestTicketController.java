package com.gi.riskmonitoring.controller;

import com.gi.riskmonitoring.model.TicketPurchaseEvent;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tools.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("api/test")
@Tag(name = "Testing API")
public class TestTicketController {


    private final ObjectMapper mapper = new ObjectMapper();
    private static final String TOPIC = "ticket-topic";
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @PostMapping("/send")
    @Operation(summary ="Send transaction to Kafka" )
    public String send(@RequestParam String userId, @RequestParam double amount) {
        TicketPurchaseEvent event = new TicketPurchaseEvent();
        event.setUserId(userId);
        event.setAmount(amount);
        event.setTimestamp(System.currentTimeMillis());

        kafkaTemplate.send(TOPIC, userId, mapper.writeValueAsString(event));
        return "Ticket Event Sent";
    }
}
