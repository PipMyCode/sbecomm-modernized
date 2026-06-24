package com.sbecomm.modernized.catalog.domain.model;

import com.sbecomm.modernized.catalog.domain.exception.InsufficientStockException;
import lombok.Getter;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class Product {
    private final ProductId id;
    private String name;
    private String description;
    private BigDecimal price;
    private int stockQuantity;
    private Category category;

    public void updateDetails(String name, String description, BigDecimal price, Category category) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
    }

    public void addStock(int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be positive");
        this.stockQuantity += quantity;
    }

    public void reduceStock(int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be positive");
        if (this.stockQuantity < quantity) {
            throw new InsufficientStockException("Not enough stock for product: " + id.value());
        }
        this.stockQuantity -= quantity;
    }
}
