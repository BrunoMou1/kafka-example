package com.anotaAi.catalog.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.bson.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CatalogService {

    private static final Logger logger = LoggerFactory.getLogger(CatalogService.class);

    private final AmazonS3 amazonS3;

    private final MongoTemplate mongoTemplate;

    @Value("${cloud.aws.bucketS3.name}")
    private String bucketName;

    public void generateCatalogForOwner(String ownerId) {
        List<Map<String, Object>> catalog = this.getCatalogByOwnerId(ownerId);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonCatalog = objectMapper.writeValueAsString(catalog);
            byte[] bytes = jsonCatalog.getBytes(StandardCharsets.UTF_8);
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(bytes.length);

            amazonS3.putObject(bucketName, ownerId + "/catalog.json", byteArrayInputStream, metadata);
        } catch (Exception e) {
            logger.error("Failed to generate catalog for owner: {}", ownerId, e);
        }
    }

    public List<Map<String, Object>> getCatalogByOwnerId(String ownerId) {
        List<Map<String, Object>> catalog = new ArrayList<>();

        List<Document> categories = mongoTemplate.find(
                Query.query(Criteria.where("ownerId").is(ownerId)),
                Document.class,
                "categories"
        );

        for (Document category : categories) {
            Map<String, Object> categoryMap = new LinkedHashMap<>();
            categoryMap.put("id", category.getObjectId("_id").toHexString());
            categoryMap.put("title", category.getString("title"));
            categoryMap.put("description", category.getString("description"));

            List<Document> products = mongoTemplate.find(
                    Query.query(Criteria.where("ownerId").is(ownerId).and("categoryId").is(category.getObjectId("_id").toHexString())),
                    Document.class,
                    "products"
            );

            List<Map<String, Object>> productsList = new ArrayList<>();
            for (Document product : products) {
                Map<String, Object> productMap = new LinkedHashMap<>();
                productMap.put("id", product.getObjectId("_id").toHexString());
                productMap.put("title", product.getString("title"));
                productMap.put("description", product.getString("description"));
                productMap.put("price", product.getDouble("price"));
                productMap.put("categoryId", product.getString("categoryId"));
                productsList.add(productMap);
            }

            categoryMap.put("products", productsList);
            catalog.add(categoryMap);
        }

        return catalog;
    }

}
