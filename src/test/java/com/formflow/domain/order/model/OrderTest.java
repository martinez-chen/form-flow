package com.formflow.domain.order.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

class OrderTest {

    @Test
    void shouldCreateOrderWithValidData() {
        String title = "Fix login issue";
        String description = "Users cannot login to the system";
        Category category = Category.of("IT_SUPPORT");
        Priority priority = Priority.high();
        Long creatorId = 1L;
        
        Order order = Order.create(title, description, category, priority, creatorId);
        
        assertThat(order.getTitle()).isEqualTo(title);
        assertThat(order.getDescription()).isEqualTo(description);
        assertThat(order.getCategory()).isEqualTo(category);
        assertThat(order.getPriority()).isEqualTo(priority);
        assertThat(order.getCreatorId()).isEqualTo(creatorId);
        assertThat(order.getStatus()).isEqualTo(Status.pending());
        assertThat(order.getCreatedAt()).isBefore(LocalDateTime.now().plusSeconds(1));
        assertThat(order.getUpdatedAt()).isBefore(LocalDateTime.now().plusSeconds(1));
        assertThat(order.getId()).isNull();
        assertThat(order.getAssigneeId()).isNull();
        assertThat(order.getGroupId()).isNull();
        assertThat(order.getTeamId()).isNull();
        assertThat(order.getDueDate()).isNull();
        assertThat(order.getCompletedAt()).isNull();
    }

    @Test
    void shouldThrowExceptionForNullTitle() {
        assertThatThrownBy(() -> Order.create(null, "description", Category.of("IT"), Priority.low(), 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Title cannot be null or empty");
    }

    @Test
    void shouldThrowExceptionForEmptyTitle() {
        assertThatThrownBy(() -> Order.create("", "description", Category.of("IT"), Priority.low(), 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Title cannot be null or empty");
    }

    @Test
    void shouldThrowExceptionForNullCategory() {
        assertThatThrownBy(() -> Order.create("title", "description", null, Priority.low(), 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Category cannot be null");
    }

    @Test
    void shouldThrowExceptionForNullPriority() {
        assertThatThrownBy(() -> Order.create("title", "description", Category.of("IT"), null, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Priority cannot be null");
    }

    @Test
    void shouldThrowExceptionForNullCreatorId() {
        assertThatThrownBy(() -> Order.create("title", "description", Category.of("IT"), Priority.low(), null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Creator ID cannot be null");
    }

    @Test
    void shouldAllowNullDescription() {
        Order order = Order.create("title", null, Category.of("IT"), Priority.low(), 1L);
        
        assertThat(order.getDescription()).isNull();
    }

    @Test
    void shouldAssignToUser() {
        Order order = Order.create("title", "description", Category.of("IT"), Priority.low(), 1L);
        Long assigneeId = 2L;
        
        order.assignTo(assigneeId);
        
        assertThat(order.getAssigneeId()).isEqualTo(assigneeId);
        assertThat(order.getStatus()).isEqualTo(Status.assigned());
        assertThat(order.getUpdatedAt()).isAfter(order.getCreatedAt());
    }

    @Test
    void shouldThrowExceptionWhenAssigningToNullUser() {
        Order order = Order.create("title", "description", Category.of("IT"), Priority.low(), 1L);
        
        assertThatThrownBy(() -> order.assignTo(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Assignee ID cannot be null");
    }

    @Test
    void shouldStartProgress() {
        Order order = Order.create("title", "description", Category.of("IT"), Priority.low(), 1L);
        order.assignTo(2L);
        
        order.startProgress();
        
        assertThat(order.getStatus()).isEqualTo(Status.inProgress());
        assertThat(order.getUpdatedAt()).isAfter(order.getCreatedAt());
    }

    @Test
    void shouldCompleteOrder() {
        Order order = Order.create("title", "description", Category.of("IT"), Priority.low(), 1L);
        order.assignTo(2L);
        order.startProgress();
        
        order.complete();
        
        assertThat(order.getStatus()).isEqualTo(Status.completed());
        assertThat(order.getCompletedAt()).isNotNull();
        assertThat(order.getCompletedAt()).isBefore(LocalDateTime.now().plusSeconds(1));
        assertThat(order.getUpdatedAt()).isAfter(order.getCreatedAt());
    }

    @Test
    void shouldCancelOrder() {
        Order order = Order.create("title", "description", Category.of("IT"), Priority.low(), 1L);
        
        order.cancel();
        
        assertThat(order.getStatus()).isEqualTo(Status.cancelled());
        assertThat(order.getUpdatedAt()).isAfter(order.getCreatedAt());
    }

    @Test
    void shouldSetDueDate() {
        Order order = Order.create("title", "description", Category.of("IT"), Priority.low(), 1L);
        LocalDateTime dueDate = LocalDateTime.now().plusDays(7);
        
        order.setDueDate(dueDate);
        
        assertThat(order.getDueDate()).isEqualTo(dueDate);
        assertThat(order.getUpdatedAt()).isAfter(order.getCreatedAt());
    }

    @Test
    void shouldAssignToGroup() {
        Order order = Order.create("title", "description", Category.of("IT"), Priority.low(), 1L);
        Long groupId = 10L;
        
        order.assignToGroup(groupId);
        
        assertThat(order.getGroupId()).isEqualTo(groupId);
        assertThat(order.getUpdatedAt()).isAfter(order.getCreatedAt());
    }

    @Test
    void shouldAssignToTeam() {
        Order order = Order.create("title", "description", Category.of("IT"), Priority.low(), 1L);
        Long teamId = 20L;
        
        order.assignToTeam(teamId);
        
        assertThat(order.getTeamId()).isEqualTo(teamId);
        assertThat(order.getUpdatedAt()).isAfter(order.getCreatedAt());
    }
}