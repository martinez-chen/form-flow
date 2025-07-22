package com.formflow.domain.order.model;

import lombok.Getter;
import java.util.Objects;

@Getter
public class Group {
    private final Long id;
    private final String name;
    private final String description;

    private Group(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public static Group of(Long id, String name, String description) {
        if (id == null) {
            throw new IllegalArgumentException("Group ID cannot be null");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Group name cannot be null or empty");
        }
        return new Group(id, name.trim(), description);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Group group = (Group) o;
        return Objects.equals(id, group.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Group{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}