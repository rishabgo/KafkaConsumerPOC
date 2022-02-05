package com.kafka.libraryeventsconsumer.processor;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LibraryEventsProcessor {

    @KafkaListener(topics = "${kafka.topic.library}", containerFactory = "kafkaListenerLibraryEventContainerFactory")
    public void onMessage(ConsumerRecord<String, String> consumerRecord) {
        log.info("Processing event " + consumerRecord.toString());
    }
}
