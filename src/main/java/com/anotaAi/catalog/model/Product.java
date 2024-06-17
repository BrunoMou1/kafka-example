package com.anotaAi.catalog.model;

import com.anotaAi.catalog.dtos.ProductDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "products")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    @Id
    private String title;
    private String description;
    private Double price;
    private String categoryId;
    private String ownerId;

    public Product(ProductDto productDto){
        this.title = productDto.title();
        this.description = productDto.description();
        this.price = productDto.price();
        this.categoryId = productDto.categoryId();
        this.ownerId = productDto.ownerId();
    }
}
