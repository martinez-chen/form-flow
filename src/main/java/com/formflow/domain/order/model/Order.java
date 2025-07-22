package com.formflow.domain.order.model;

import lombok.Getter;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
public class Order {
    private Long id;
    private String title;
    private String description;
    private Category category;
    private Priority priority;
    private Status status;
    private Long creatorId;
    private Long assigneeId;
    private Long groupId;
    private Long teamId;
    private LocalDateTime dueDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime completedAt;

    private Order(String title, String description, Category category, Priority priority, Long creatorId) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.priority = priority;
        this.creatorId = creatorId;
        this.status = Status.pending();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public static Order create(String title, String description, Category category, Priority priority, Long creatorId) {
        validateCreateOrderParams(title, category, priority, creatorId);
        return new Order(title, description, category, priority, creatorId);
    }

    private static void validateCreateOrderParams(String title, Category category, Priority priority, Long creatorId) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be null or empty");
        }
        if (category == null) {
            throw new IllegalArgumentException("Category cannot be null");
        }
        if (priority == null) {
            throw new IllegalArgumentException("Priority cannot be null");
        }
        if (creatorId == null) {
            throw new IllegalArgumentException("Creator ID cannot be null");
        }
    }

    public void assignTo(Long assigneeId) {
        if (assigneeId == null) {
            throw new IllegalArgumentException("Assignee ID cannot be null");
        }
        this.assigneeId = assigneeId;
        this.status = Status.assigned();
        this.updatedAt = LocalDateTime.now();
    }

    public void startProgress() {
        this.status = Status.inProgress();
        this.updatedAt = LocalDateTime.now();
    }

    public void complete() {
        this.status = Status.completed();
        this.completedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void cancel() {
        this.status = Status.cancelled();
        this.updatedAt = LocalDateTime.now();
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
        this.updatedAt = LocalDateTime.now();
    }

    public void assignToGroup(Long groupId) {
        if (groupId == null) {
            throw new IllegalArgumentException("Group ID cannot be null");
        }
        if (this.status.isCompleted()) {
            throw new IllegalStateException("Cannot assign completed order to group");
        }
        if (this.status.isCancelled()) {
            throw new IllegalStateException("Cannot assign cancelled order to group");
        }
        this.groupId = groupId;
        this.updatedAt = LocalDateTime.now();
    }

    public void assignToTeam(Long teamId) {
        this.teamId = teamId;
        this.updatedAt = LocalDateTime.now();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(id, order.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", status=" + status +
                ", priority=" + priority +
                ", createdAt=" + createdAt +
                '}';
    }
}