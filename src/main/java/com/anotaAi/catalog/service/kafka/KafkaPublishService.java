package com.anotaAi.catalog.service.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaPublishService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendMessage(String message, String topicName) {
        log.info("Sending : {}", message);
        CompletableFuture<SendResult<String, Object>> send = kafkaTemplate.send(topicName, message);
        send.whenComplete((result, throwable) -> {
           if (throwable == null) {
               log.info("sent message: " + message + " with offset " + result.getRecordMetadata().offset());
           } else {
               log.info("unable to send message: " + message + " due to " + throwable);
           }
        });
    }
}
