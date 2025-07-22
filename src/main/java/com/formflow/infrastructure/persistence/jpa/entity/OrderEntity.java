package com.formflow.infrastructure.persistence.jpa.entity;

import lombok.Data;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "orders", indexes = {
    @Index(name = "idx_order_creator", columnList = "creator_id"),
    @Index(name = "idx_order_assignee", columnList = "assignee_id"),
    @Index(name = "idx_order_group", columnList = "group_id"),
    @Index(name = "idx_order_team", columnList = "team_id"),
    @Index(name = "idx_order_status", columnList = "status"),
    @Index(name = "idx_order_priority", columnList = "priority"),
    @Index(name = "idx_order_category", columnList = "category"),
    @Index(name = "idx_order_created_at", columnList = "created_at")
})
@Data
public class OrderEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 200)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(nullable = false, length = 50)
    private String category;
    
    @Column(nullable = false, length = 20)
    private String priority;
    
    @Column(nullable = false, length = 20)
    private String status;
    
    @Column(name = "creator_id", nullable = false)
    private Long creatorId;
    
    @Column(name = "assignee_id")
    private Long assigneeId;
    
    @Column(name = "group_id")
    private Long groupId;
    
    @Column(name = "team_id")
    private Long teamId;
    
    @Column(name = "due_date")
    private LocalDateTime dueDate;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @Column(name = "completed_at")
    private LocalDateTime completedAt;
    
    @PrePersist
    protected void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }
    
    @PreUpdate
    protected void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

}