package com.anotaAi.catalog.repository;

import com.anotaAi.catalog.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProductRepository extends MongoRepository<Product, String> {
    List<Product> findByCategoryId(String id);

    List<Product> findProductsByOwnerId(String ownerId);
}
