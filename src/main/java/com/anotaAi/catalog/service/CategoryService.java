package com.anotaAi.catalog.service;

import com.anotaAi.catalog.dtos.CategoryDto;
import com.anotaAi.catalog.exception.CategoryNotFoundException;
import com.anotaAi.catalog.model.Category;
import com.anotaAi.catalog.repository.CategoryRepository;
import com.anotaAi.catalog.service.kafka.KafkaPublishService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final KafkaPublishService kafkaPublishService;

        public Category create(CategoryDto categoryDto) {
            Category newCategory = new Category(categoryDto);
            categoryRepository.save(newCategory);
            kafkaPublishService.sendMessage(categoryDto.ownerId(), "catalog-emit");
            return newCategory;
        }

    public Optional<Category> findCategoryById(String categoryID) {
        return categoryRepository.findById(categoryID);
    }

    public Category update(String id, CategoryDto categoryDTO){
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(String.format("category with id=%s was not found", id)));

        if(!categoryDTO.title().isEmpty()) category.setTitle(categoryDTO.title());
        if(!categoryDTO.description().isEmpty()) category.setDescription(categoryDTO.description());

        categoryRepository.save(category);
        kafkaPublishService.sendMessage(categoryDTO.ownerId(), "catalog-emit");
        return category;
    }

    public void delete(String id){
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(String.format("category with id=%s was not found", id)));

        categoryRepository.delete(category);
        kafkaPublishService.sendMessage(category.getOwnerId(), "catalog-emit");
    }
}
