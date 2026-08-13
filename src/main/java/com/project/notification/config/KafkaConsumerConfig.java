//package com.project.notification.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.kafka.listener.DefaultErrorHandler;
//import org.springframework.util.backoff.FixedBackOff;
//@Configuration
//public class KafkaConsumerConfig {
//    @Bean
//    public DefaultErrorHandler errorHandler() {
//
//        FixedBackOff backOff =
//                new FixedBackOff(2000L, 2L);
//
//        return new DefaultErrorHandler(backOff);
//    }
//}
