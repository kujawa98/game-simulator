package com.kucu.gamesimulator.configuration;

import com.kucu.gamesimulator.configuration.properties.KafkaTopicsProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.integration.kafka.dsl.Kafka;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;

@Configuration
@EnableConfigurationProperties(KafkaTopicsProperties.class)
public class KafkaConfiguration {

    @Autowired
    private KafkaTopicsProperties kafkaIntegrationProperties;

    // Kafka Configuration
    // ****************************************************************************************************************

    @Bean
    public ProducerFactory<String, String> producerFactory(final KafkaProperties kafkaProperties) {
        return new DefaultKafkaProducerFactory<>(kafkaProperties.buildProducerProperties());
    }

    @Bean
    public ConsumerFactory<String, String> consumerFactory(final KafkaProperties kafkaProperties) {
        return new DefaultKafkaConsumerFactory<>(kafkaProperties.buildConsumerProperties());
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate(final ProducerFactory<String, String> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public KafkaMessageListenerContainer<String, String> messageListenerContainer(final ConsumerFactory<String, String> consumerFactory) {
        ContainerProperties containerProps = new ContainerProperties(kafkaIntegrationProperties.getTest());
        return new KafkaMessageListenerContainer<>(consumerFactory, containerProps);
    }

    @Bean
    public IntegrationFlow kafkaInboundFlow(final KafkaMessageListenerContainer<String, String> container) {
        return IntegrationFlow
                .from(Kafka.messageDrivenChannelAdapter(container))
                .log(LoggingHandler.Level.INFO, Message::getPayload) //todo tutaj router
                .nullChannel();
    }

    @Bean
    public MessageChannel toKafka() {
        return MessageChannels.publishSubscribe().getObject();
    }

    @Bean
    public IntegrationFlow kafkaOutboundFlow(final KafkaTemplate<String, String> template) {
        return IntegrationFlow
                .from(toKafka())
                .handle(Kafka.outboundChannelAdapter(template)
                        .topic(kafkaIntegrationProperties.getTest()))
                .get();
    }
}
