package com.kafka.libraryeventsconsumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.EnableKafka;

import javax.sql.DataSource;

@SpringBootApplication
@EnableKafka
public class LibraryEventsConsumerApplication {

	public static void main(String[] args) {
		SpringApplication.run(LibraryEventsConsumerApplication.class, args);
	}
}
