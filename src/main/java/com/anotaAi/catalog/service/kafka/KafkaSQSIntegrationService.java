package com.anotaAi.catalog.service.kafka;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaSQSIntegrationService {

    private final AmazonSQS amazonSQSClient;

    @Value("${cloud.aws.sqs.url}")
    private String sqsUrl;

    private static final Logger logger = LoggerFactory.getLogger(KafkaSQSIntegrationService.class);

    @KafkaListener(topics = "catalog-emit", groupId = "catalog-emit-id")
    public void listen(String message) {
        SendMessageRequest sendMessageRequest = new SendMessageRequest()
                .withQueueUrl(sqsUrl)
                .withMessageBody(message);

        try {
            SendMessageResult sendMessageResult = amazonSQSClient.sendMessage(sendMessageRequest);
            String messageId = sendMessageResult.getMessageId();
            if (messageId != null && !messageId.isEmpty()) {
                logger.info("Message sent successfully to SQS. Message ID: {}", messageId);
            } else {
                logger.error("Message was not sent to SQS. No Message ID returned.");
            }
        } catch (Exception e) {
            logger.error("Failed to send message to SQS. Error: {}", e.getMessage(), e);
        }
    }

}
