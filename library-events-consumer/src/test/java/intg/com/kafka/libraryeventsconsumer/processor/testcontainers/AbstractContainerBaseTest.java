package com.kafka.libraryeventsconsumer.processor.testcontainers;

import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

public class AbstractContainerBaseTest {

    @Container
    static final KafkaContainer KAFKA_CONTAINER;

    static {
        KAFKA_CONTAINER = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:6.2.1"));

        KAFKA_CONTAINER.start();
        System.setProperty("KAFKA_BROKERS", KAFKA_CONTAINER.getBootstrapServers());
    }
}