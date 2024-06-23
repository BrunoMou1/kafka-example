package com.anotaAi.catalog.service;

import com.anotaAi.catalog.dtos.ProductDto;
import com.anotaAi.catalog.exception.CategoryNotFoundException;
import com.anotaAi.catalog.exception.ProductNotFoundException;
import com.anotaAi.catalog.model.Product;
import com.anotaAi.catalog.repository.ProductRepository;
import com.anotaAi.catalog.service.kafka.KafkaPublishService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    private final KafkaPublishService kafkaPublishService;

    public Product create(ProductDto productDto) {
        categoryService.findCategoryById(productDto.categoryId())
                .orElseThrow(() -> new CategoryNotFoundException(String.format("category with id=%s was not found", productDto.categoryId())));

        Product newProduct = new Product(productDto);
        productRepository.save(newProduct);
        kafkaPublishService.sendMessage(productDto.ownerId(), "catalog-emit");
        return newProduct;
    }

    public Product update(String id, ProductDto productDto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(String.format("product with id=%s was not found", id)));

        if(!productDto.title().isEmpty()) product.setTitle(productDto.title());
        if(!productDto.description().isEmpty()) product.setDescription(productDto.description());
        if(productDto.price() != null) product.setPrice(productDto.price());
        if(productDto.categoryId() != null) product.setCategoryId(productDto.categoryId());

        productRepository.save(product);
        kafkaPublishService.sendMessage(productDto.ownerId(), "catalog-emit");
        return product;
    }

    public void delete(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(String.format("product with id=%s was not found", id)));
        productRepository.deleteById(id);
        kafkaPublishService.sendMessage(product.getOwnerId(), "catalog-emit");
    }
}
