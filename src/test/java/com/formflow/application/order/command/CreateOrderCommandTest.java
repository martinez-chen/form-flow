package com.formflow.application.order.command;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

class CreateOrderCommandTest {

    @Test
    void shouldCreateCommandWithValidData() {
        String title = "Fix login issue";
        String description = "Users cannot login";
        String category = "IT_SUPPORT";
        String priority = "HIGH";
        Long creatorId = 1L;
        Long groupId = 10L;
        LocalDateTime dueDate = LocalDateTime.now().plusDays(7);
        
        CreateOrderCommand command = new CreateOrderCommand(title, description, category, priority, creatorId, groupId, null, dueDate);
        
        assertThat(command.getTitle()).isEqualTo(title);
        assertThat(command.getDescription()).isEqualTo(description);
        assertThat(command.getCategory()).isEqualTo(category);
        assertThat(command.getPriority()).isEqualTo(priority);
        assertThat(command.getCreatorId()).isEqualTo(creatorId);
        assertThat(command.getGroupId()).isEqualTo(groupId);
        assertThat(command.getTeamId()).isNull();
        assertThat(command.getDueDate()).isEqualTo(dueDate);
    }

    @Test
    void shouldCreateCommandWithMinimalData() {
        String title = "Fix issue";
        String category = "IT";
        String priority = "LOW";
        Long creatorId = 1L;
        
        CreateOrderCommand command = new CreateOrderCommand(title, null, category, priority, creatorId, null, null, null);
        
        assertThat(command.getTitle()).isEqualTo(title);
        assertThat(command.getDescription()).isNull();
        assertThat(command.getCategory()).isEqualTo(category);
        assertThat(command.getPriority()).isEqualTo(priority);
        assertThat(command.getCreatorId()).isEqualTo(creatorId);
        assertThat(command.getGroupId()).isNull();
        assertThat(command.getTeamId()).isNull();
        assertThat(command.getDueDate()).isNull();
    }

    @Test
    void shouldCreateCommandWithTeamAssignment() {
        CreateOrderCommand command = new CreateOrderCommand("title", "description", "IT", "MEDIUM", 1L, null, 20L, null);
        
        assertThat(command.getTeamId()).isEqualTo(20L);
        assertThat(command.getGroupId()).isNull();
    }
}