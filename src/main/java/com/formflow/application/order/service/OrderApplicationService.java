package com.formflow.application.order.service;

import com.formflow.application.order.command.AssignOrderToGroupCommand;
import com.formflow.application.order.command.CreateOrderCommand;
import com.formflow.domain.order.model.Category;
import com.formflow.domain.order.model.Order;
import com.formflow.domain.order.model.Priority;
import com.formflow.domain.order.repository.OrderRepository;
import org.springframework.stereotype.Service;

@Service
public class OrderApplicationService {
    private final OrderRepository orderRepository;

    public OrderApplicationService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Long createOrder(CreateOrderCommand command) {
        Category category = Category.of(command.getCategory());
        Priority priority = Priority.of(command.getPriority());
        
        Order order = Order.create(
            command.getTitle(),
            command.getDescription(),
            category,
            priority,
            command.getCreatorId()
        );

        if (command.getGroupId() != null) {
            order.assignToGroup(command.getGroupId());
        }

        if (command.getTeamId() != null) {
            order.assignToTeam(command.getTeamId());
        }

        if (command.getDueDate() != null) {
            order.setDueDate(command.getDueDate());
        }

        Order savedOrder = orderRepository.save(order);
        return savedOrder.getId();
    }

    public void assignOrderToGroup(AssignOrderToGroupCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("Command cannot be null");
        }
        if (command.getOrderId() == null) {
            throw new IllegalArgumentException("Order ID cannot be null");
        }

        Order order = orderRepository.findById(command.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + command.getOrderId()));

        order.assignToGroup(command.getGroupId());
        orderRepository.save(order);
    }
}