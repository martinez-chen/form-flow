package com.formflow.application.order.service;

import com.formflow.application.order.command.CreateOrderCommand;
import com.formflow.domain.order.model.*;
import com.formflow.domain.order.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderApplicationServiceTest {

    @Mock
    private OrderRepository orderRepository;
    
    private OrderApplicationService orderApplicationService;

    @BeforeEach
    void setUp() {
        orderApplicationService = new OrderApplicationService(orderRepository);
    }

    @Test
    void shouldCreateOrderWithValidCommand() {
        CreateOrderCommand command = new CreateOrderCommand(
            "Fix login issue",
            "Users cannot login to system",
            "IT_SUPPORT",
            "HIGH",
            1L,
            null,
            null,
            null
        );
        
        Order savedOrder = Order.create("Fix login issue", "Users cannot login to system", 
            Category.of("IT_SUPPORT"), Priority.high(), 1L);
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        
        Long result = orderApplicationService.createOrder(command);
        
        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(orderCaptor.capture());
        
        Order capturedOrder = orderCaptor.getValue();
        assertThat(capturedOrder.getTitle()).isEqualTo("Fix login issue");
        assertThat(capturedOrder.getDescription()).isEqualTo("Users cannot login to system");
        assertThat(capturedOrder.getCategory()).isEqualTo(Category.of("IT_SUPPORT"));
        assertThat(capturedOrder.getPriority()).isEqualTo(Priority.high());
        assertThat(capturedOrder.getCreatorId()).isEqualTo(1L);
        assertThat(capturedOrder.getStatus()).isEqualTo(Status.pending());
    }

    @Test
    void shouldCreateOrderWithGroupAssignment() {
        CreateOrderCommand command = new CreateOrderCommand(
            "Bug report",
            "System bug found",
            "DEVELOPMENT",
            "MEDIUM",
            2L,
            10L,
            null,
            null
        );
        
        Order savedOrder = Order.create("Bug report", "System bug found",
            Category.of("DEVELOPMENT"), Priority.medium(), 2L);
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        
        orderApplicationService.createOrder(command);
        
        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(orderCaptor.capture());
        
        Order capturedOrder = orderCaptor.getValue();
        assertThat(capturedOrder.getGroupId()).isEqualTo(10L);
    }

    @Test
    void shouldCreateOrderWithTeamAssignment() {
        CreateOrderCommand command = new CreateOrderCommand(
            "Feature request",
            "New feature needed",
            "ENHANCEMENT",
            "LOW",
            3L,
            null,
            20L,
            null
        );
        
        Order savedOrder = Order.create("Feature request", "New feature needed",
            Category.of("ENHANCEMENT"), Priority.low(), 3L);
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        
        orderApplicationService.createOrder(command);
        
        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(orderCaptor.capture());
        
        Order capturedOrder = orderCaptor.getValue();
        assertThat(capturedOrder.getTeamId()).isEqualTo(20L);
    }

    @Test
    void shouldCreateOrderWithDueDate() {
        LocalDateTime dueDate = LocalDateTime.now().plusDays(7);
        CreateOrderCommand command = new CreateOrderCommand(
            "Urgent fix",
            "Critical issue",
            "CRITICAL",
            "URGENT",
            1L,
            null,
            null,
            dueDate
        );
        
        Order savedOrder = Order.create("Urgent fix", "Critical issue",
            Category.of("CRITICAL"), Priority.urgent(), 1L);
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        
        orderApplicationService.createOrder(command);
        
        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(orderCaptor.capture());
        
        Order capturedOrder = orderCaptor.getValue();
        assertThat(capturedOrder.getDueDate()).isEqualTo(dueDate);
    }

    @Test
    void shouldThrowExceptionWhenRepositoryFails() {
        CreateOrderCommand command = new CreateOrderCommand(
            "Test order",
            "Test description",
            "TEST",
            "LOW",
            1L,
            null,
            null,
            null
        );
        
        when(orderRepository.save(any(Order.class))).thenThrow(new RuntimeException("Database error"));
        
        assertThatThrownBy(() -> orderApplicationService.createOrder(command))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Database error");
    }
}