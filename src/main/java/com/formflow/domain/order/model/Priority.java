package com.formflow.domain.order.model;

import java.util.Objects;
import java.util.Set;

public class Priority {
    private static final Set<String> VALID_VALUES = Set.of("LOW", "MEDIUM", "HIGH", "URGENT");
    
    private final String value;

    private Priority(String value) {
        this.value = value;
    }

    public static Priority of(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Priority value cannot be null or empty");
        }
        
        String upperValue = value.trim().toUpperCase();
        if (!VALID_VALUES.contains(upperValue)) {
            throw new IllegalArgumentException("Invalid priority: " + value + ". Valid values are: LOW, MEDIUM, HIGH, URGENT");
        }
        
        return new Priority(upperValue);
    }

    public static Priority low() {
        return new Priority("LOW");
    }

    public static Priority medium() {
        return new Priority("MEDIUM");
    }

    public static Priority high() {
        return new Priority("HIGH");
    }

    public static Priority urgent() {
        return new Priority("URGENT");
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Priority priority = (Priority) o;
        return Objects.equals(value, priority.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "Priority{" +
                "value='" + value + '\'' +
                '}';
    }
}