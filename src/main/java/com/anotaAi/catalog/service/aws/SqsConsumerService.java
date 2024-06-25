package com.anotaAi.catalog.service.aws;

import com.anotaAi.catalog.service.CatalogService;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SqsConsumerService {

    private final CatalogService catalogService;

    private static final Logger logger = LoggerFactory.getLogger(CatalogService.class);


    @Value("${cloud.aws.sqs.url}")
    private String sqsUrl;

    @SqsListener(value = "catalog-queue")
    public void receiveMessage(String messageBody) {
        try {
            catalogService.generateCatalogForOwner(messageBody);
        } catch (Exception e) {
            logger.error("Error processing message for ownerId: " + messageBody, e);
            throw e;
        }
    }

}
