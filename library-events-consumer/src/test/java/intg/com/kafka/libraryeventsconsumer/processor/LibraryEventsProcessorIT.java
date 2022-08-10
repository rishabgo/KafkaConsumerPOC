package com.kafka.libraryeventsconsumer.processor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kafka.libraryeventsconsumer.entity.Book;
import com.kafka.libraryeventsconsumer.entity.LibraryEvent;
import com.kafka.libraryeventsconsumer.entity.LibraryEventType;
import com.kafka.libraryeventsconsumer.service.LibraryEventsService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.Ignore;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://${kafka.brokers}"})
@DirtiesContext
public class LibraryEventsProcessorIT {

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Value("${kafka.topic.library}")
    private String topic;

    @SpyBean
    private LibraryEventsProcessor libraryEventsProcessor;

    @SpyBean
    private LibraryEventsService libraryEventsService;

    private Producer<String, String> producer;

    @SpyBean
    private ObjectMapper objectMapper;

    @Captor
    ArgumentCaptor<ConsumerRecord<String, String>> argumentCaptor;

    @BeforeAll
    void setUp() {
        Map<String, Object> configs = KafkaTestUtils.producerProps(embeddedKafkaBroker);
        producer = new DefaultKafkaProducerFactory<>(configs, new StringSerializer(), new StringSerializer()).createProducer();
    }

    @AfterAll
    void shutdown() {
        producer.close();
    }

    @Test
    public void testLibraryEventProcessor() throws JsonProcessingException, InterruptedException, ExecutionException {

        System.out.println("Processor bean " + libraryEventsProcessor.toString());
        //given
        LibraryEvent libraryEvent = LibraryEvent.builder()
                .libraryEventId(UUID.randomUUID().toString())
                .eventType(LibraryEventType.NEW)
                .book(Book.builder().bookId("1").bookName("Java").bookAuthor("Joshua bloch").build())
                .build();

        //convert to JSON for sending to kafka
        //additional optimization can be done(encrypt and compress the message)
        String data = objectMapper.writeValueAsString(libraryEvent);
        System.out.println(data);

        //when
        RecordMetadata recordMetadata = producer.send(new ProducerRecord<>(topic, data)).get();
        System.out.println(recordMetadata.topic());

        CountDownLatch latch = new CountDownLatch(1);
        latch.await(3, TimeUnit.SECONDS);

        //then
        System.out.println("verifying data in kafka");
        verify(libraryEventsProcessor, times(1)).onMessage(argumentCaptor.capture());
        System.out.println(argumentCaptor.getValue().value());

    }
}
