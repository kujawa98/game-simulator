package com.kucu.gamesimulator.listener;

import org.springframework.kafka.annotation.KafkaListener;

public class KafkaMessageListener {
    @KafkaListener(topics = "test", groupId = "game-sim")
    public void listen(String message) {
        System.out.println("Received: " + message);
    }
}
