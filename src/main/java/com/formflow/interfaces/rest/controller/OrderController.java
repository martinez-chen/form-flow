package com.formflow.interfaces.rest.controller;

import com.formflow.application.order.command.AssignOrderToGroupCommand;
import com.formflow.application.order.command.CreateOrderCommand;
import com.formflow.application.order.dto.AssignOrderToGroupRequest;
import com.formflow.application.order.dto.CreateOrderRequest;
import com.formflow.application.order.dto.OrderDTO;
import com.formflow.application.order.service.OrderApplicationService;
import com.formflow.domain.order.model.Order;
import com.formflow.domain.order.repository.OrderRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Tag(name = "工單管理", description = "工單管理相關API，提供工單的創建、查詢、更新等功能")
@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderApplicationService orderApplicationService;
    private final OrderRepository orderRepository;

    public OrderController(OrderApplicationService orderApplicationService, OrderRepository orderRepository) {
        this.orderApplicationService = orderApplicationService;
        this.orderRepository = orderRepository;
    }

    @Operation(summary = "創建工單", description = "創建新的工單，支援指派給群組或團隊")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "工單創建成功", 
                    content = @Content(schema = @Schema(implementation = Map.class))),
        @ApiResponse(responseCode = "400", description = "請求參數無效"),
        @ApiResponse(responseCode = "500", description = "服務器內部錯誤")
    })
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

    @Operation(summary = "根據ID獲取工單", description = "根據工單ID獲取工單詳細信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功獲取工單信息",
                    content = @Content(schema = @Schema(implementation = OrderDTO.class))),
        @ApiResponse(responseCode = "404", description = "工單不存在")
    })
    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getOrder(@Parameter(description = "工單ID") @PathVariable Long id) {
        Optional<Order> orderOpt = orderRepository.findById(id);
        
        if (orderOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Order order = orderOpt.get();
        OrderDTO dto = convertToDTO(order);
        
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "獲取所有工單", description = "獲取系統中所有工單的列表")
    @ApiResponse(responseCode = "200", description = "成功獲取工單列表",
                content = @Content(schema = @Schema(implementation = OrderDTO.class)))
    @GetMapping
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        List<OrderDTO> dtos = orders.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(dtos);
    }

    @Operation(summary = "獲取我創建的工單", description = "根據創建者ID獲取該用戶創建的所有工單")
    @GetMapping("/my-orders")
    public ResponseEntity<List<OrderDTO>> getMyOrders(@Parameter(description = "創建者ID") @RequestParam Long creatorId) {
        List<Order> orders = orderRepository.findByCreatorId(creatorId);
        List<OrderDTO> dtos = orders.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(dtos);
    }

    @Operation(summary = "獲取指派給我的工單", description = "根據被指派者ID獲取指派給該用戶的所有工單")
    @GetMapping("/my-assignments")
    public ResponseEntity<List<OrderDTO>> getMyAssignments(@Parameter(description = "被指派者ID") @RequestParam Long assigneeId) {
        List<Order> orders = orderRepository.findByAssigneeId(assigneeId);
        List<OrderDTO> dtos = orders.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(dtos);
    }

    @Operation(summary = "指派工單給群組", description = "將工單指派給指定的群組")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "工單成功指派給群組"),
        @ApiResponse(responseCode = "400", description = "請求參數無效"),
        @ApiResponse(responseCode = "404", description = "工單不存在"),
        @ApiResponse(responseCode = "409", description = "工單狀態不允許指派")
    })
    @PutMapping("/{id}/assign-to-group")
    public ResponseEntity<Map<String, Object>> assignOrderToGroup(
            @Parameter(description = "工單ID") @PathVariable Long id,
            @Valid @RequestBody AssignOrderToGroupRequest request) {
        try {
            AssignOrderToGroupCommand command = new AssignOrderToGroupCommand(
                id,
                request.getGroupId(),
                request.getRequesterId()
            );

            orderApplicationService.assignOrderToGroup(command);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Order assigned to group successfully");
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("not found")) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", e.getMessage());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            } else {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", e.getMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }
        } catch (IllegalStateException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to assign order to group");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
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