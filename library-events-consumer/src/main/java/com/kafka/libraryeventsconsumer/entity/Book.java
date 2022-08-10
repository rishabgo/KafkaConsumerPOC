package com.kafka.libraryeventsconsumer.entity;

import lombok.*;

@Data
@Builder
public class Book {

    private String bookId;
    private String bookName;
    private String bookAuthor;
}
