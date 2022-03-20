package com.kafka.libraryeventsconsumer.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kafka.libraryeventsconsumer.entity.LibraryEvent;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LibraryEventsService {

    private static final Logger logger = LoggerFactory.getLogger(LibraryEventsService.class);

    @Autowired
    private ObjectMapper objectMapper;

    public void processLibraryEvent(ConsumerRecord<String, String> consumerRecord) throws JsonProcessingException {

        final LibraryEvent libraryEvent;
        try {
            logger.info("Processing library event");
            libraryEvent = objectMapper.readValue(consumerRecord.value(), LibraryEvent.class);

            //Persist record or do further processing
        } catch (Exception ex) {
            throw new RuntimeException("Exception while processing message");
        }
        
        validateLibraryEvent(libraryEvent);
    }

    public void validateLibraryEvent(final LibraryEvent libraryEvent) {

        if (libraryEvent.getLibraryEventId() == null || libraryEvent.getLibraryEventId().isBlank()) {
            throw new IllegalArgumentException("Library event id is missing");
        }
    }
}
