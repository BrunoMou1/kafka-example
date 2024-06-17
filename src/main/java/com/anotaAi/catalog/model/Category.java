package com.anotaAi.catalog.model;

import com.anotaAi.catalog.dtos.CategoryDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "categories")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Category {
    @Id
    private String id;
    private String title;
    private String description;
    private String ownerId;

    public Category(CategoryDto categoryDto){
        this.title = categoryDto.title();
        this.description = categoryDto.description();
        this.ownerId = categoryDto.ownerId();
    }
}
