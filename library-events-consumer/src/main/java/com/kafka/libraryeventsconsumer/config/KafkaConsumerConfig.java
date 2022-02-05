package com.kafka.libraryeventsconsumer.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

    @Value("${kafka.brokers}")
    private String kafkaBootStrapServers;

    @Value("${kafka.consumer.group}")
    private String consumerGroup;

    private Map<String, Object> kafkaCommonProperties() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootStrapServers);
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        config.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroup);
        return config;
    }

    @Bean(name = "kafkaListenerLibraryEventContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerLibraryEventContainerFactory() {
        ConcurrentKafkaListenerContainerFactory factory = new ConcurrentKafkaListenerContainerFactory();
        factory.setConsumerFactory(createConsumerFactory(new StringDeserializer(), new StringDeserializer()));
        return factory;
    }

    private <K, V> ConsumerFactory<K, V> createConsumerFactory(final Deserializer<K> keyDeserializer, final Deserializer<V> valueDeserializer) {
        return new DefaultKafkaConsumerFactory(kafkaCommonProperties(),
                new ErrorHandlingDeserializer(keyDeserializer),
                new ErrorHandlingDeserializer(valueDeserializer));
    }

}
