package com.formflow.interfaces.rest.controller;

import com.formflow.application.order.command.CreateOrderCommand;
import com.formflow.application.order.dto.CreateOrderRequest;
import com.formflow.application.order.dto.OrderDTO;
import com.formflow.application.order.service.OrderApplicationService;
import com.formflow.domain.order.model.Order;
import com.formflow.domain.order.repository.OrderRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderApplicationService orderApplicationService;
    private final OrderRepository orderRepository;

    public OrderController(OrderApplicationService orderApplicationService, OrderRepository orderRepository) {
        this.orderApplicationService = orderApplicationService;
        this.orderRepository = orderRepository;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        try {
            CreateOrderCommand command = new CreateOrderCommand(
                request.getTitle(),
                request.getDescription(),
                request.getCategory(),
                request.getPriority(),
                request.getCreatorId(),
                request.getGroupId(),
                request.getTeamId(),
                request.getDueDate()
            );

            Long orderId = orderApplicationService.createOrder(command);

            Map<String, Object> response = new HashMap<>();
            response.put("id", orderId);
            response.put("message", "Order created successfully");

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to create order");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getOrder(@PathVariable Long id) {
        Optional<Order> orderOpt = orderRepository.findById(id);
        
        if (orderOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Order order = orderOpt.get();
        OrderDTO dto = convertToDTO(order);
        
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        List<OrderDTO> dtos = orders.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/my-orders")
    public ResponseEntity<List<OrderDTO>> getMyOrders(@RequestParam Long creatorId) {
        List<Order> orders = orderRepository.findByCreatorId(creatorId);
        List<OrderDTO> dtos = orders.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/my-assignments")
    public ResponseEntity<List<OrderDTO>> getMyAssignments(@RequestParam Long assigneeId) {
        List<Order> orders = orderRepository.findByAssigneeId(assigneeId);
        List<OrderDTO> dtos = orders.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(dtos);
    }

    private OrderDTO convertToDTO(Order order) {
        return new OrderDTO(
            order.getId(),
            order.getTitle(),
            order.getDescription(),
            order.getCategory() != null ? order.getCategory().getValue() : null,
            order.getPriority() != null ? order.getPriority().getValue() : null,
            order.getStatus() != null ? order.getStatus().getValue() : null,
            order.getCreatorId(),
            order.getAssigneeId(),
            order.getGroupId(),
            order.getTeamId(),
            order.getDueDate(),
            order.getCreatedAt(),
            order.getUpdatedAt(),
            order.getCompletedAt()
        );
    }
}