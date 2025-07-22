package com.formflow.application.order.service;

import com.formflow.application.order.command.AssignOrderToGroupCommand;
import com.formflow.domain.order.model.*;
import com.formflow.domain.order.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderGroupAssignmentServiceTest {

    @Mock
    private OrderRepository orderRepository;

    private OrderApplicationService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderApplicationService(orderRepository);
    }

    @Test
    void shouldAssignOrderToGroup() {
        Long orderId = 1L;
        Long groupId = 100L;
        Long requesterId = 50L;
        Order order = Order.create("Test Order", "Description", Category.of("TASK"), Priority.high(), 1L);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);

        AssignOrderToGroupCommand command = new AssignOrderToGroupCommand(orderId, groupId, requesterId);
        
        orderService.assignOrderToGroup(command);

        assertThat(order.getGroupId()).isEqualTo(groupId);
        verify(orderRepository).findById(orderId);
        verify(orderRepository).save(order);
    }

    @Test
    void shouldThrowExceptionWhenOrderNotFound() {
        Long orderId = 999L;
        Long groupId = 100L;
        Long requesterId = 50L;

        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        AssignOrderToGroupCommand command = new AssignOrderToGroupCommand(orderId, groupId, requesterId);

        assertThatThrownBy(() -> orderService.assignOrderToGroup(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Order not found: " + orderId);

        verify(orderRepository).findById(orderId);
        verify(orderRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenCommandIsNull() {
        assertThatThrownBy(() -> orderService.assignOrderToGroup(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Command cannot be null");

        verify(orderRepository, never()).findById(any());
        verify(orderRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenOrderIdIsNull() {
        AssignOrderToGroupCommand command = new AssignOrderToGroupCommand(null, 100L, 50L);

        assertThatThrownBy(() -> orderService.assignOrderToGroup(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Order ID cannot be null");

        verify(orderRepository, never()).findById(any());
        verify(orderRepository, never()).save(any());
    }
}