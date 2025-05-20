package com.kucu.gamesimulator.configuration.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "application.kafka.topics")
@Getter
@Setter
public class KafkaTopicsProperties {
    String test;
}
