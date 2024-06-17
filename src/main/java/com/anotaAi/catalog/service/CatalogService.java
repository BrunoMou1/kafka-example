package com.anotaAi.catalog.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.anotaAi.catalog.model.Product;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CatalogService {

    private final ProductService productService;

    private static final Logger logger = LoggerFactory.getLogger(CatalogService.class);

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.bucketS3.name}")
    private String bucketName;

    public void generateCatalogForOwner(String ownerId) {
        List<Product> products = productService.getProductsByOwnerId(ownerId);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonCatalog = objectMapper.writeValueAsString(products);
            byte[] bytes = jsonCatalog.getBytes(StandardCharsets.UTF_8);
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(bytes.length);

            amazonS3.putObject(bucketName, ownerId + "/catalog.json", byteArrayInputStream, metadata);
        } catch (Exception e) {
            logger.error("Failed to generate catalog for owner: {}", ownerId, e);
        }
    }
}
