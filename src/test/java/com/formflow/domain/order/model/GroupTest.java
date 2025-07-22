package com.formflow.domain.order.model;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class GroupTest {

    @Test
    void shouldCreateGroupWithValidParameters() {
        Long groupId = 1L;
        String name = "Development Team";
        String description = "Software development group";
        
        Group group = Group.of(groupId, name, description);
        
        assertThat(group.getId()).isEqualTo(groupId);
        assertThat(group.getName()).isEqualTo(name);
        assertThat(group.getDescription()).isEqualTo(description);
    }

    @Test
    void shouldThrowExceptionWhenGroupIdIsNull() {
        assertThatThrownBy(() -> Group.of(null, "Team Name", "Description"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Group ID cannot be null");
    }

    @Test
    void shouldThrowExceptionWhenGroupNameIsNull() {
        assertThatThrownBy(() -> Group.of(1L, null, "Description"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Group name cannot be null or empty");
    }

    @Test
    void shouldThrowExceptionWhenGroupNameIsEmpty() {
        assertThatThrownBy(() -> Group.of(1L, "", "Description"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Group name cannot be null or empty");
    }

    @Test
    void shouldThrowExceptionWhenGroupNameIsBlank() {
        assertThatThrownBy(() -> Group.of(1L, "   ", "Description"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Group name cannot be null or empty");
    }

    @Test
    void shouldTrimGroupName() {
        Group group = Group.of(1L, "  Team Name  ", "Description");
        assertThat(group.getName()).isEqualTo("Team Name");
    }

    @Test
    void shouldAllowNullDescription() {
        Group group = Group.of(1L, "Team Name", null);
        assertThat(group.getDescription()).isNull();
    }

    @Test
    void shouldBeEqualWhenSameId() {
        Group group1 = Group.of(1L, "Team A", "Description A");
        Group group2 = Group.of(1L, "Team B", "Description B");
        
        assertThat(group1).isEqualTo(group2);
        assertThat(group1.hashCode()).isEqualTo(group2.hashCode());
    }

    @Test
    void shouldNotBeEqualWhenDifferentId() {
        Group group1 = Group.of(1L, "Team Name", "Description");
        Group group2 = Group.of(2L, "Team Name", "Description");
        
        assertThat(group1).isNotEqualTo(group2);
    }
}