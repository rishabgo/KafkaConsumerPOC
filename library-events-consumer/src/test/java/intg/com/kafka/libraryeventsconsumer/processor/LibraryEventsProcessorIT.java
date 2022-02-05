package com.kafka.libraryeventsconsumer.processor;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kafka.libraryeventsconsumer.config.KafkaConsumerConfig;
import com.kafka.libraryeventsconsumer.entity.Book;
import com.kafka.libraryeventsconsumer.entity.LibraryEvent;
import com.kafka.libraryeventsconsumer.entity.LibraryEventType;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
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


@SpringBootTest(classes = {KafkaConsumerConfig.class})
@EmbeddedKafka(partitions = 1)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext
public class LibraryEventsProcessorIT {

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Value("${kafka.topic.library}")
    private String topic;

    /*@Autowired
    KafkaListenerEndpointRegistry endpointRegistry;*/

    @SpyBean
    private LibraryEventsProcessor libraryEventsProcessor;

    private Producer<String, String> producer;

    private ObjectMapper objectMapper = new ObjectMapper().setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    ;

    @Captor
    ArgumentCaptor<ConsumerRecord> argumentCaptor;

    @BeforeAll
    void setUp() {

       /* for (MessageListenerContainer messageListenerContainer : endpointRegistry.getListenerContainers()){
            ContainerTestUtils.waitForAssignment(messageListenerContainer, embeddedKafkaBroker.getPartitionsPerTopic());
        }*/

        Map<String, Object> configs = KafkaTestUtils.producerProps(embeddedKafkaBroker);
        producer = new DefaultKafkaProducerFactory<>(configs, new StringSerializer(), new StringSerializer()).createProducer();
    }

    @AfterAll
    void shutdown() {
        producer.close();
    }

    @Test
    public void testLibraryEventProcessor() throws JsonProcessingException, ExecutionException, InterruptedException {

        System.out.println("Processor bean " + libraryEventsProcessor.toString());
        //given
        LibraryEvent libraryEvent = LibraryEvent.builder().libraryEventId(UUID.randomUUID().toString()).eventType(LibraryEventType.NEW).book(
                Book.builder().bookId("1").bookName("Java").bookAuthor("Joshua blonch").build()
        ).build();

        String data = objectMapper.writeValueAsString(libraryEvent);

        System.out.println("Sending message to broker " + embeddedKafkaBroker.getBrokersAsString());

        //when
        producer.send(new ProducerRecord<>(topic, data));
        producer.flush();

        CountDownLatch latch = new CountDownLatch(1);
        latch.await(3, TimeUnit.SECONDS);

        //then
        verify(libraryEventsProcessor, times(1)).onMessage(argumentCaptor.capture());
        System.out.println(argumentCaptor.getValue().value());

    }
}
