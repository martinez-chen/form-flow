package com.formflow.domain.order.model;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class OrderGroupAssignmentTest {

    @Test
    void shouldAssignOrderToGroup() {
        Order order = Order.create("Test Order", "Description", Category.of("TASK"), Priority.high(), 1L);
        Long groupId = 100L;
        
        order.assignToGroup(groupId);
        
        assertThat(order.getGroupId()).isEqualTo(groupId);
        assertThat(order.getUpdatedAt()).isNotNull();
    }

    @Test
    void shouldAllowGroupAssignmentForPendingOrder() {
        Order order = Order.create("Test Order", "Description", Category.of("TASK"), Priority.high(), 1L);
        
        assertThat(order.getStatus().isPending()).isTrue();
        
        order.assignToGroup(100L);
        
        assertThat(order.getGroupId()).isEqualTo(100L);
        assertThat(order.getStatus().isPending()).isTrue();
    }

    @Test
    void shouldAllowGroupAssignmentForAssignedOrder() {
        Order order = Order.create("Test Order", "Description", Category.of("TASK"), Priority.high(), 1L);
        order.assignTo(50L);
        
        assertThat(order.getStatus().isAssigned()).isTrue();
        
        order.assignToGroup(100L);
        
        assertThat(order.getGroupId()).isEqualTo(100L);
        assertThat(order.getStatus().isAssigned()).isTrue();
    }

    @Test
    void shouldNotAllowGroupAssignmentForCompletedOrder() {
        Order order = Order.create("Test Order", "Description", Category.of("TASK"), Priority.high(), 1L);
        order.complete();
        
        assertThat(order.getStatus().isCompleted()).isTrue();
        
        assertThatThrownBy(() -> order.assignToGroup(100L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Cannot assign completed order to group");
    }

    @Test
    void shouldNotAllowGroupAssignmentForCancelledOrder() {
        Order order = Order.create("Test Order", "Description", Category.of("TASK"), Priority.high(), 1L);
        order.cancel();
        
        assertThat(order.getStatus().isCancelled()).isTrue();
        
        assertThatThrownBy(() -> order.assignToGroup(100L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Cannot assign cancelled order to group");
    }

    @Test
    void shouldReassignOrderToDifferentGroup() {
        Order order = Order.create("Test Order", "Description", Category.of("TASK"), Priority.high(), 1L);
        order.assignToGroup(100L);
        
        assertThat(order.getGroupId()).isEqualTo(100L);
        
        order.assignToGroup(200L);
        
        assertThat(order.getGroupId()).isEqualTo(200L);
    }

    @Test
    void shouldThrowExceptionWhenGroupIdIsNull() {
        Order order = Order.create("Test Order", "Description", Category.of("TASK"), Priority.high(), 1L);
        
        assertThatThrownBy(() -> order.assignToGroup(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Group ID cannot be null");
    }
}