package com.kucu.gamesimulator.configuration;

import com.kucu.gamesimulator.listener.KafkaMessageListener;
import com.kucu.gamesimulator.producer.KafkaMessageProducer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
public class GameSimulatorConfiguration {

    @Bean
    public KafkaMessageListener kafkaMessageListener() {
        return new KafkaMessageListener();
    }

    @Bean
    public KafkaMessageProducer kafkaMessageProducer(final KafkaTemplate<String, String> kafkaTemplate) {
        return new KafkaMessageProducer(kafkaTemplate);
    }
}
