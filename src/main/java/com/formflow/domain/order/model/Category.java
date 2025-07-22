package com.formflow.domain.order.model;

import lombok.Getter;
import java.util.Objects;

@Getter
public class Category {
    private final String value;

    private Category(String value) {
        this.value = value;
    }

    public static Category of(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Category value cannot be null or empty");
        }
        
        String normalizedValue = normalizeValue(value);
        return new Category(normalizedValue);
    }

    private static String normalizeValue(String value) {
        return value.trim()
                .toUpperCase()
                .replaceAll("\\s+", "_");
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return Objects.equals(value, category.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "Category{" +
                "value='" + value + '\'' +
                '}';
    }
}