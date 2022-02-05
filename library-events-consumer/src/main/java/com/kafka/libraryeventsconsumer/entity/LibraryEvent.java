package com.kafka.libraryeventsconsumer.entity;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class LibraryEvent {
    private String libraryEventId;
    private Book book;
    @Setter
    private LibraryEventType eventType;
}
