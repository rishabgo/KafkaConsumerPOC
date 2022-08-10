package com.kafka.libraryeventsconsumer.entity;

import lombok.*;

@Data
@Builder
public class LibraryEvent {
    private String libraryEventId;
    private Book book;
    private LibraryEventType eventType;
}
