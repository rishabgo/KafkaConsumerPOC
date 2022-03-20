package com.kafka.libraryeventsconsumer.processor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kafka.libraryeventsconsumer.service.LibraryEventsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LibraryEventsProcessor {

    private LibraryEventsService libraryEventsService;

    public LibraryEventsProcessor(final LibraryEventsService libraryEventsService) {
        this.libraryEventsService = libraryEventsService;
    }

    @KafkaListener(topics = "${kafka.topic.library}", containerFactory = "kafkaListenerLibraryEventContainerFactory")
    public void onMessage(ConsumerRecord<String, String> consumerRecord) throws JsonProcessingException {
        log.info("Processing event " + consumerRecord.toString());

        libraryEventsService.processLibraryEvent(consumerRecord);
    }
}
