package com.anotaAi.catalog.controller;

import com.anotaAi.catalog.dtos.ProductDto;
import com.anotaAi.catalog.model.Product;
import com.anotaAi.catalog.service.ProductService;
import com.anotaAi.catalog.service.kafka.KafkaPublishService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final KafkaPublishService kafkaPublishService;

    @PostMapping
    public Product createProduct(@RequestBody ProductDto productDto) {
        return productService.create(productDto);
    }

    @PutMapping("/{id}")
    public Product updateProduct(@PathVariable String id, @RequestBody ProductDto productDto) {
        return productService.update(id, productDto);
    }

    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable String id) {
        productService.delete(id);
    }


}
