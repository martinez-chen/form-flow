package com.formflow.infrastructure.persistence.jpa.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

class OrderEntityTest {

    @Test
    void shouldCreateOrderEntityWithAllFields() {
        OrderEntity entity = new OrderEntity();
        entity.setId(1L);
        entity.setTitle("Fix login issue");
        entity.setDescription("Users cannot login");
        entity.setCategory("IT_SUPPORT");
        entity.setPriority("HIGH");
        entity.setStatus("PENDING");
        entity.setCreatorId(10L);
        entity.setAssigneeId(20L);
        entity.setGroupId(5L);
        entity.setTeamId(15L);
        
        LocalDateTime now = LocalDateTime.now();
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
        entity.setDueDate(now.plusDays(7));
        entity.setCompletedAt(now.plusHours(2));
        
        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getTitle()).isEqualTo("Fix login issue");
        assertThat(entity.getDescription()).isEqualTo("Users cannot login");
        assertThat(entity.getCategory()).isEqualTo("IT_SUPPORT");
        assertThat(entity.getPriority()).isEqualTo("HIGH");
        assertThat(entity.getStatus()).isEqualTo("PENDING");
        assertThat(entity.getCreatorId()).isEqualTo(10L);
        assertThat(entity.getAssigneeId()).isEqualTo(20L);
        assertThat(entity.getGroupId()).isEqualTo(5L);
        assertThat(entity.getTeamId()).isEqualTo(15L);
        assertThat(entity.getCreatedAt()).isEqualTo(now);
        assertThat(entity.getUpdatedAt()).isEqualTo(now);
        assertThat(entity.getDueDate()).isEqualTo(now.plusDays(7));
        assertThat(entity.getCompletedAt()).isEqualTo(now.plusHours(2));
    }

    @Test
    void shouldAllowNullableFields() {
        OrderEntity entity = new OrderEntity();
        entity.setTitle("Test");
        entity.setCategory("TEST");
        entity.setPriority("LOW");
        entity.setStatus("PENDING");
        entity.setCreatorId(1L);
        
        assertThat(entity.getDescription()).isNull();
        assertThat(entity.getAssigneeId()).isNull();
        assertThat(entity.getGroupId()).isNull();
        assertThat(entity.getTeamId()).isNull();
        assertThat(entity.getDueDate()).isNull();
        assertThat(entity.getCompletedAt()).isNull();
    }

    @Test
    void shouldPrePersistSetTimestamps() {
        OrderEntity entity = new OrderEntity();
        entity.setTitle("Test");
        entity.setCategory("TEST");
        entity.setPriority("LOW");
        entity.setStatus("PENDING");
        entity.setCreatorId(1L);
        
        entity.prePersist();
        
        assertThat(entity.getCreatedAt()).isNotNull();
        assertThat(entity.getUpdatedAt()).isNotNull();
        assertThat(entity.getCreatedAt()).isEqualTo(entity.getUpdatedAt());
    }

    @Test
    void shouldPreUpdateSetTimestamp() {
        OrderEntity entity = new OrderEntity();
        entity.setTitle("Test");
        entity.setCategory("TEST");
        entity.setPriority("LOW");
        entity.setStatus("PENDING");
        entity.setCreatorId(1L);
        
        LocalDateTime originalTime = LocalDateTime.now().minusHours(1);
        entity.setCreatedAt(originalTime);
        entity.setUpdatedAt(originalTime);
        
        entity.preUpdate();
        
        assertThat(entity.getCreatedAt()).isEqualTo(originalTime);
        assertThat(entity.getUpdatedAt()).isAfter(originalTime);
    }
}