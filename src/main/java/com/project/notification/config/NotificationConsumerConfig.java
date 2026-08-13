//package com.project.notification.config;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.kafka.listener.ConsumerRecordRecoverer;
//import org.springframework.kafka.retrytopic.DltStrategy;
//import org.springframework.kafka.retrytopic.RetryTopicConfiguration;
//import org.springframework.kafka.retrytopic.RetryTopicConfigurationBuilder;
//
//import java.util.Arrays;
//
//@Configuration
//public class NotificationConsumerConfig {
//    private static final Logger log = LoggerFactory.getLogger(NotificationConsumerConfig.class);
//
//    @Bean
//    public RetryTopicConfiguration notificationRetryConfig(){
//        return RetryTopicConfigurationBuilder.newInstance()
//                .maxAttempts(3)                                    // Blueprint: Try 3 times total
//                .exponentialBackoff(1000, 2.0, 10000)             // Blueprint: Delay config
//                .retryTopicSuffix("-retry")                        // Blueprint: Topic naming
//                .dltStrategy(DltStrategy.FAIL_ON_ERROR)            // Blueprint: DLT strategy
//                .dltTopicSuffix("-dlt")                            // Blueprint: DLT topic name
//                .includeTopics(Arrays.asList("notification-topic")) // Blueprint: Apply to this topic
//                .create();
//    }
//    @Bean
//    public ConsumerRecordRecoverer deadLetterRecoverer() {
//        return (consumerRecord, exception) ->
//                log.error("Dead Letter: Failed to process notification after all retries. " +
//                        "Message: {}. Exception: {}", consumerRecord.value(), exception.getMessage());
//    }
//}
