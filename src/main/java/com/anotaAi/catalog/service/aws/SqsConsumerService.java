package com.anotaAi.catalog.service.aws;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.anotaAi.catalog.service.CatalogService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SqsConsumerService {

    private final AmazonSQS amazonSQSClient;
    private final CatalogService catalogService;

    @Value("${cloud.aws.sqs.url}")
    private String sqsUrl;

    @PostConstruct
    private void init() {
        Runnable sqsListener = () -> {
            while (true) {
                ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(sqsUrl)
                        .withMaxNumberOfMessages(10);
                List<Message> messages = amazonSQSClient.receiveMessage(receiveMessageRequest).getMessages();

                for (Message message : messages) {
                    String ownerId = message.getBody();
                    catalogService.generateCatalogForOwner(ownerId);
                }
            }
        };
        Thread listenerThread = new Thread(sqsListener);
        listenerThread.start();
    }

}
