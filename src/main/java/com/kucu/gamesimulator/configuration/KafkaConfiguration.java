package com.kucu.gamesimulator.configuration;

import com.kucu.gamesimulator.configuration.properties.KafkaIntegrationProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.integration.kafka.dsl.Kafka;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;

@Configuration
@EnableConfigurationProperties(KafkaIntegrationProperties.class)
public class KafkaConfiguration {

    @Autowired
    private KafkaIntegrationProperties kafkaIntegrationProperties;

    @Bean
    public ProducerFactory<String, String> producerFactory(KafkaProperties kafkaProperties) {
        return new DefaultKafkaProducerFactory<>(kafkaProperties.buildProducerProperties());
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate(ProducerFactory<String, String> pf) {
        return new KafkaTemplate<>(pf);
    }

    @Bean
    public ConsumerFactory<String, String> consumerFactory(KafkaProperties kafkaProperties) {
        return new DefaultKafkaConsumerFactory<>(kafkaProperties.buildConsumerProperties());
    }

    @Bean
    public KafkaMessageListenerContainer<String, String> messageListenerContainer(ConsumerFactory<String, String> cf) {
        ContainerProperties containerProps = new ContainerProperties(kafkaIntegrationProperties.getTest());
        return new KafkaMessageListenerContainer<>(cf, containerProps);
    }

    @Bean
    public IntegrationFlow kafkaInboundFlow(KafkaMessageListenerContainer<String, String> container) {
        return IntegrationFlow
                .from(Kafka.messageDrivenChannelAdapter(container))
                .log(LoggingHandler.Level.INFO, Message::getPayload)
                .get();
    }

    @Bean
    public MessageChannel toKafka() {
        return new DirectChannel();
    }

    @Bean
    public IntegrationFlow kafkaOutboundFlow(KafkaTemplate<String, String> template) {
        return IntegrationFlow
                .from(toKafka())
                .handle(Kafka.outboundChannelAdapter(template)
                        .topic(kafkaIntegrationProperties.getTest()))
                .get();
    }
}
