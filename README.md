**Application**
->KafkaConsumerPOC application will consume Library events from Kafka and send events for further processing to LibraryEventsService.
->Retry Mechanism is added to config in case consumer is unable to process a particular message due to specific exceptions specified in config.

**Integration Test**
@EmbeddedKafka -> Using EmbeddedKafka for integration test. 
TestConatiner -> Using TestConatiner to integrate Kafka broker and consumer will read events from this broker. Upon completion of IT container will automcatically stop.

//TODO
-> Increase Parallelism by Partitioning Topic.
-> Use Protobuf plugin for sending events.
