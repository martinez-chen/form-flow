package com.formflow.domain.order.repository;

import com.formflow.domain.order.model.Order;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {
    Order save(Order order);
    Optional<Order> findById(Long id);
    List<Order> findByCreatorId(Long creatorId);
    List<Order> findByAssigneeId(Long assigneeId);
    List<Order> findByGroupId(Long groupId);
    List<Order> findByTeamId(Long teamId);
    List<Order> findAll();
    void deleteById(Long id);
    boolean existsById(Long id);
}