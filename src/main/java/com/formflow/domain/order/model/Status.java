package com.formflow.domain.order.model;

import lombok.Getter;
import java.util.Objects;
import java.util.Set;

@Getter
public class Status {
    private static final Set<String> VALID_VALUES = Set.of("PENDING", "ASSIGNED", "IN_PROGRESS", "COMPLETED", "CANCELLED");
    
    private final String value;

    private Status(String value) {
        this.value = value;
    }

    public static Status of(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Status value cannot be null or empty");
        }
        
        String upperValue = value.trim().toUpperCase();
        if (!VALID_VALUES.contains(upperValue)) {
            throw new IllegalArgumentException("Invalid status: " + value + ". Valid values are: PENDING, ASSIGNED, IN_PROGRESS, COMPLETED, CANCELLED");
        }
        
        return new Status(upperValue);
    }

    public static Status pending() {
        return new Status("PENDING");
    }

    public static Status assigned() {
        return new Status("ASSIGNED");
    }

    public static Status inProgress() {
        return new Status("IN_PROGRESS");
    }

    public static Status completed() {
        return new Status("COMPLETED");
    }

    public static Status cancelled() {
        return new Status("CANCELLED");
    }


    public boolean isPending() {
        return "PENDING".equals(value);
    }

    public boolean isAssigned() {
        return "ASSIGNED".equals(value);
    }

    public boolean isInProgress() {
        return "IN_PROGRESS".equals(value);
    }

    public boolean isCompleted() {
        return "COMPLETED".equals(value);
    }

    public boolean isCancelled() {
        return "CANCELLED".equals(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Status status = (Status) o;
        return Objects.equals(value, status.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "Status{" +
                "value='" + value + '\'' +
                '}';
    }
}