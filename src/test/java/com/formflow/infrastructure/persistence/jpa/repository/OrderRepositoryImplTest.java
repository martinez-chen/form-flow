package com.formflow.infrastructure.persistence.jpa.repository;

import com.formflow.domain.order.model.*;
import com.formflow.infrastructure.persistence.jpa.entity.OrderEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderRepositoryImplTest {

    @Mock
    private JpaOrderRepository jpaOrderRepository;
    
    private OrderRepositoryImpl orderRepository;

    @BeforeEach
    void setUp() {
        orderRepository = new OrderRepositoryImpl(jpaOrderRepository);
    }

    @Test
    void shouldSaveOrder() {
        Order order = Order.create(
            "Fix login issue",
            "Users cannot login",
            Category.of("IT_SUPPORT"),
            Priority.high(),
            1L
        );
        
        OrderEntity savedEntity = createOrderEntity();
        savedEntity.setId(100L);
        
        when(jpaOrderRepository.save(any(OrderEntity.class))).thenReturn(savedEntity);
        
        Order result = orderRepository.save(order);
        
        assertThat(result).isNotNull();
        verify(jpaOrderRepository).save(any(OrderEntity.class));
    }

    @Test
    void shouldFindOrderById() {
        OrderEntity entity = createOrderEntity();
        entity.setId(1L);
        
        when(jpaOrderRepository.findById(1L)).thenReturn(Optional.of(entity));
        
        Optional<Order> result = orderRepository.findById(1L);
        
        assertThat(result).isPresent();
        assertThat(result.get().getTitle()).isEqualTo("Fix login issue");
        assertThat(result.get().getCategory()).isEqualTo(Category.of("IT_SUPPORT"));
        assertThat(result.get().getPriority()).isEqualTo(Priority.high());
    }

    @Test
    void shouldReturnEmptyWhenOrderNotFound() {
        when(jpaOrderRepository.findById(999L)).thenReturn(Optional.empty());
        
        Optional<Order> result = orderRepository.findById(999L);
        
        assertThat(result).isEmpty();
    }

    @Test
    void shouldFindOrdersByCreatorId() {
        OrderEntity entity = createOrderEntity();
        when(jpaOrderRepository.findByCreatorId(1L)).thenReturn(List.of(entity));
        
        List<Order> result = orderRepository.findByCreatorId(1L);
        
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCreatorId()).isEqualTo(1L);
    }

    @Test
    void shouldFindOrdersByAssigneeId() {
        OrderEntity entity = createOrderEntity();
        entity.setAssigneeId(2L);
        when(jpaOrderRepository.findByAssigneeId(2L)).thenReturn(List.of(entity));
        
        List<Order> result = orderRepository.findByAssigneeId(2L);
        
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getAssigneeId()).isEqualTo(2L);
    }

    @Test
    void shouldFindOrdersByGroupId() {
        OrderEntity entity = createOrderEntity();
        entity.setGroupId(10L);
        when(jpaOrderRepository.findByGroupId(10L)).thenReturn(List.of(entity));
        
        List<Order> result = orderRepository.findByGroupId(10L);
        
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getGroupId()).isEqualTo(10L);
    }

    @Test
    void shouldFindOrdersByTeamId() {
        OrderEntity entity = createOrderEntity();
        entity.setTeamId(20L);
        when(jpaOrderRepository.findByTeamId(20L)).thenReturn(List.of(entity));
        
        List<Order> result = orderRepository.findByTeamId(20L);
        
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTeamId()).isEqualTo(20L);
    }

    @Test
    void shouldDeleteOrderById() {
        orderRepository.deleteById(1L);
        
        verify(jpaOrderRepository).deleteById(1L);
    }

    @Test
    void shouldCheckIfOrderExists() {
        when(jpaOrderRepository.existsById(1L)).thenReturn(true);
        
        boolean result = orderRepository.existsById(1L);
        
        assertThat(result).isTrue();
    }

    private OrderEntity createOrderEntity() {
        OrderEntity entity = new OrderEntity();
        entity.setTitle("Fix login issue");
        entity.setDescription("Users cannot login");
        entity.setCategory("IT_SUPPORT");
        entity.setPriority("HIGH");
        entity.setStatus("PENDING");
        entity.setCreatorId(1L);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        return entity;
    }
}