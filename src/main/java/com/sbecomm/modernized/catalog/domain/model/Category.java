package com.sbecomm.modernized.catalog.domain.model;

import lombok.Getter;
import lombok.Setter;

@Getter
public class Category {
    private final String id;
    @Setter
    private String name;
    @Setter
    private String description;

    public Category(String id, String name, String description) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("Category ID cannot be blank");
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public void update(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
