package com.kucu.gamesimulator.listener;

import org.springframework.kafka.annotation.KafkaListener;

public class KafkaMessageListener {
    private static final String GROUP_ID = "game-sim";
    private static final String TEST_TOPIC = "test";


    @KafkaListener(topics = TEST_TOPIC, groupId = GROUP_ID)
    public void listen(String message) {
        System.out.println("Received: " + message);
    }
}
