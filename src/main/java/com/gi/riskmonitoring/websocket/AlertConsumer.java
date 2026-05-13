package com.gi.riskmonitoring.websocket;


import com.gi.riskmonitoring.model.AlertEntity;
import com.gi.riskmonitoring.model.SuspiciousBetEvent;
import com.gi.riskmonitoring.repository.AlertRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
public class AlertConsumer {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    @Autowired
    private AlertRepository alertRepository;

    @KafkaListener(topics = "suspicious-topic", groupId = "risk-alert-group")
    public void consumer(String message) throws Exception {

        try {
            System.out.println("🚨 ALERT RECEIVED FROM KAFKA: " + message);
            SuspiciousBetEvent event =
                    new ObjectMapper().readValue(message, SuspiciousBetEvent.class);


            AlertEntity alertEntity=new AlertEntity(null,event.getUserId(),event.getReason(),event.getRiskLevel(), System.currentTimeMillis());
            alertRepository.save(alertEntity);
            simpMessagingTemplate.convertAndSend("/topic/admin-alerts", message);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
