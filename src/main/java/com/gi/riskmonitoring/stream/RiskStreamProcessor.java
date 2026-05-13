package com.gi.riskmonitoring.stream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gi.riskmonitoring.model.SuspiciousBetEvent;
import com.gi.riskmonitoring.model.TicketPurchaseEvent;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class RiskStreamProcessor {

    private final ObjectMapper mapper = new ObjectMapper();

    @Bean
    public KStream<String, TicketPurchaseEvent>  process(StreamsBuilder builder) {

        KStream<String, TicketPurchaseEvent> stream =
                builder.stream("ticket-topic",
                                Consumed.with(Serdes.String(), Serdes.String()))
                        .mapValues(value -> {
                            try {
                                return mapper.readValue(value, TicketPurchaseEvent.class);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }

                        });

        System.out.println("Inside RiskStreamProcessor =1");
        TimeWindows window = TimeWindows.ofSizeWithNoGrace(Duration.ofMinutes(1));
        //TimeWindows window = TimeWindows.ofSizeAndGrace(Duration.ofMinutes(1), Duration.ofSeconds(10));// grace period
        stream
                .groupByKey()
                .windowedBy(window)
                .count()
                .toStream()
                .filter((windowedKey, count) -> count == 6)
                .peek((windowedKey, count) ->
                        System.out.println("COUNT for " + windowedKey.key() + " = " + count))
                .map((windowedKey, count) -> {
                    try {
                        SuspiciousBetEvent alert = new SuspiciousBetEvent(
                                windowedKey.key(),
                                "High transaction velocity",
                                "HIGH"
                        );
                        return KeyValue.pair(
                                windowedKey.key(),
                                mapper.writeValueAsString(alert)
                        );

                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .to("suspicious-topic",
                        Produced.with(Serdes.String(), Serdes.String())
                );

        stream
                .groupByKey()
                .windowedBy(window)
                .aggregate(
                        () -> 0.0,
                        (key, event, total) -> total + event.getAmount(),
                        Materialized.with(Serdes.String(), Serdes.Double())
                )
                .toStream()
                .filter((windowedKey, totalAmount) -> totalAmount >= 20000)
                .map((windowedKey, totalAmount) -> {
                    SuspiciousBetEvent alert = new SuspiciousBetEvent(
                            windowedKey.key(),
                            "High total amount in 1 minute",
                            "CRITICAL"
                    );
                    try {
                        return KeyValue.pair(
                                windowedKey.key(),
                                mapper.writeValueAsString(alert)
                        );
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .to("suspicious-topic", Produced.with(Serdes.String(), Serdes.String()));
        System.out.println("Inside RiskStreamProcessor =end");
        return stream;
    }
}