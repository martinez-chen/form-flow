package com.formflow.interfaces.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.formflow.application.order.command.CreateOrderCommand;
import com.formflow.application.order.dto.CreateOrderRequest;
import com.formflow.application.order.service.OrderApplicationService;
import com.formflow.domain.order.model.Order;
import com.formflow.domain.order.model.Category;
import com.formflow.domain.order.model.Priority;
import com.formflow.domain.order.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
@WithMockUser
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderApplicationService orderApplicationService;

    @MockBean
    private OrderRepository orderRepository;

    @Test
    void shouldCreateOrderSuccessfully() throws Exception {
        CreateOrderRequest request = new CreateOrderRequest(
            "Fix login issue",
            "Users cannot login to system",
            "IT_SUPPORT",
            "HIGH",
            1L,
            null,
            null,
            null
        );

        when(orderApplicationService.createOrder(any(CreateOrderCommand.class))).thenReturn(100L);

        mockMvc.perform(post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.message").value("Order created successfully"));
    }

    @Test
    void shouldReturnBadRequestForInvalidOrderData() throws Exception {
        CreateOrderRequest request = new CreateOrderRequest(
            null, // null title
            "Description",
            "IT_SUPPORT",
            "HIGH",
            1L,
            null,
            null,
            null
        );

        mockMvc.perform(post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldGetOrderByIdSuccessfully() throws Exception {
        Order order = Order.create(
            "Fix login issue",
            "Users cannot login",
            Category.of("IT_SUPPORT"),
            Priority.high(),
            1L
        );
        
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        mockMvc.perform(get("/api/v1/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Fix login issue"))
                .andExpect(jsonPath("$.category").value("IT_SUPPORT"))
                .andExpect(jsonPath("$.priority").value("HIGH"))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.creatorId").value(1));
    }

    @Test
    void shouldReturnNotFoundForNonExistentOrder() throws Exception {
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/orders/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldGetAllOrdersSuccessfully() throws Exception {
        Order order1 = Order.create("Issue 1", "Description 1", Category.of("IT"), Priority.high(), 1L);
        Order order2 = Order.create("Issue 2", "Description 2", Category.of("HR"), Priority.medium(), 2L);
        
        when(orderRepository.findAll()).thenReturn(List.of(order1, order2));

        mockMvc.perform(get("/api/v1/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("Issue 1"))
                .andExpect(jsonPath("$[1].title").value("Issue 2"));
    }

    @Test
    void shouldCreateOrderWithGroupAssignment() throws Exception {
        CreateOrderRequest request = new CreateOrderRequest(
            "Bug report",
            "System bug found",
            "DEVELOPMENT",
            "MEDIUM",
            2L,
            10L, // groupId
            null,
            null
        );

        when(orderApplicationService.createOrder(any(CreateOrderCommand.class))).thenReturn(101L);

        mockMvc.perform(post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(101));
    }

    @Test
    void shouldCreateOrderWithTeamAssignment() throws Exception {
        CreateOrderRequest request = new CreateOrderRequest(
            "Feature request",
            "New feature needed",
            "ENHANCEMENT",
            "LOW",
            3L,
            null,
            20L, // teamId
            null
        );

        when(orderApplicationService.createOrder(any(CreateOrderCommand.class))).thenReturn(102L);

        mockMvc.perform(post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(102));
    }

    @Test
    void shouldCreateOrderWithDueDate() throws Exception {
        LocalDateTime dueDate = LocalDateTime.now().plusDays(7);
        CreateOrderRequest request = new CreateOrderRequest(
            "Urgent fix",
            "Critical issue",
            "CRITICAL",
            "URGENT",
            1L,
            null,
            null,
            dueDate
        );

        when(orderApplicationService.createOrder(any(CreateOrderCommand.class))).thenReturn(103L);

        mockMvc.perform(post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(103));
    }
}