package com.formflow.infrastructure.persistence.jpa.repository;

import com.formflow.domain.order.model.*;
import com.formflow.domain.order.repository.OrderRepository;
import com.formflow.infrastructure.persistence.jpa.entity.OrderEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class OrderRepositoryImpl implements OrderRepository {
    
    private final JpaOrderRepository jpaOrderRepository;

    public OrderRepositoryImpl(JpaOrderRepository jpaOrderRepository) {
        this.jpaOrderRepository = jpaOrderRepository;
    }

    @Override
    public Order save(Order order) {
        OrderEntity entity = toEntity(order);
        OrderEntity savedEntity = jpaOrderRepository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public Optional<Order> findById(Long id) {
        return jpaOrderRepository.findById(id)
                .map(this::toDomain);
    }

    @Override
    public List<Order> findByCreatorId(Long creatorId) {
        return jpaOrderRepository.findByCreatorId(creatorId)
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Order> findByAssigneeId(Long assigneeId) {
        return jpaOrderRepository.findByAssigneeId(assigneeId)
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Order> findByGroupId(Long groupId) {
        return jpaOrderRepository.findByGroupId(groupId)
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Order> findByTeamId(Long teamId) {
        return jpaOrderRepository.findByTeamId(teamId)
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Order> findAll() {
        return jpaOrderRepository.findAll()
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        jpaOrderRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return jpaOrderRepository.existsById(id);
    }

    private OrderEntity toEntity(Order order) {
        OrderEntity entity = new OrderEntity();
        entity.setId(order.getId());
        entity.setTitle(order.getTitle());
        entity.setDescription(order.getDescription());
        entity.setCategory(order.getCategory().getValue());
        entity.setPriority(order.getPriority().getValue());
        entity.setStatus(order.getStatus().getValue());
        entity.setCreatorId(order.getCreatorId());
        entity.setAssigneeId(order.getAssigneeId());
        entity.setGroupId(order.getGroupId());
        entity.setTeamId(order.getTeamId());
        entity.setDueDate(order.getDueDate());
        entity.setCreatedAt(order.getCreatedAt());
        entity.setUpdatedAt(order.getUpdatedAt());
        entity.setCompletedAt(order.getCompletedAt());
        return entity;
    }

    private Order toDomain(OrderEntity entity) {
        // Using reflection to access private fields since Order doesn't have setters
        try {
            Order order = Order.create(
                entity.getTitle(),
                entity.getDescription(),
                Category.of(entity.getCategory()),
                Priority.of(entity.getPriority()),
                entity.getCreatorId()
            );
            
            // Set ID using reflection
            java.lang.reflect.Field idField = Order.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(order, entity.getId());
            
            // Set status
            if (!"PENDING".equals(entity.getStatus())) {
                java.lang.reflect.Field statusField = Order.class.getDeclaredField("status");
                statusField.setAccessible(true);
                statusField.set(order, Status.of(entity.getStatus()));
            }
            
            // Set other fields
            if (entity.getAssigneeId() != null) {
                java.lang.reflect.Field assigneeField = Order.class.getDeclaredField("assigneeId");
                assigneeField.setAccessible(true);
                assigneeField.set(order, entity.getAssigneeId());
            }
            
            if (entity.getGroupId() != null) {
                java.lang.reflect.Field groupField = Order.class.getDeclaredField("groupId");
                groupField.setAccessible(true);
                groupField.set(order, entity.getGroupId());
            }
            
            if (entity.getTeamId() != null) {
                java.lang.reflect.Field teamField = Order.class.getDeclaredField("teamId");
                teamField.setAccessible(true);
                teamField.set(order, entity.getTeamId());
            }
            
            // Set timestamps
            java.lang.reflect.Field createdAtField = Order.class.getDeclaredField("createdAt");
            createdAtField.setAccessible(true);
            createdAtField.set(order, entity.getCreatedAt());
            
            java.lang.reflect.Field updatedAtField = Order.class.getDeclaredField("updatedAt");
            updatedAtField.setAccessible(true);
            updatedAtField.set(order, entity.getUpdatedAt());
            
            if (entity.getDueDate() != null) {
                java.lang.reflect.Field dueDateField = Order.class.getDeclaredField("dueDate");
                dueDateField.setAccessible(true);
                dueDateField.set(order, entity.getDueDate());
            }
            
            if (entity.getCompletedAt() != null) {
                java.lang.reflect.Field completedAtField = Order.class.getDeclaredField("completedAt");
                completedAtField.setAccessible(true);
                completedAtField.set(order, entity.getCompletedAt());
            }
            
            return order;
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert entity to domain object", e);
        }
    }
}