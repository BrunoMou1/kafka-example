package com.anotaAi.catalog.controller;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/search")
public class SearchController {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.bucketS3.name}")
    private String bucketName;

    @GetMapping("/{ownerId}")
    public ResponseEntity<String> getProductJson(@PathVariable String ownerId) {
        try {
            S3Object s3Object = amazonS3.getObject(bucketName, ownerId + "/catalog.json");
            BufferedReader reader = new BufferedReader(new InputStreamReader(s3Object.getObjectContent()));
            StringBuilder jsonBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }
            return ResponseEntity.ok(jsonBuilder.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
