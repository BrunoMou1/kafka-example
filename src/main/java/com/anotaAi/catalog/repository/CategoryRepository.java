package com.anotaAi.catalog.repository;

import com.anotaAi.catalog.model.Category;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CategoryRepository extends MongoRepository<Category, String> {
    List<Category> findByOwnerId(String ownerId);
}
