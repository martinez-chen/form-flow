package com.formflow.domain.order.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class PriorityTest {

    @Test
    void shouldCreatePriorityWithValidValue() {
        Priority priority = Priority.of("HIGH");
        
        assertThat(priority.getValue()).isEqualTo("HIGH");
    }

    @Test
    void shouldThrowExceptionForNullValue() {
        assertThatThrownBy(() -> Priority.of(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Priority value cannot be null or empty");
    }

    @Test
    void shouldThrowExceptionForEmptyValue() {
        assertThatThrownBy(() -> Priority.of(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Priority value cannot be null or empty");
    }

    @Test
    void shouldThrowExceptionForInvalidValue() {
        assertThatThrownBy(() -> Priority.of("INVALID"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid priority: INVALID. Valid values are: LOW, MEDIUM, HIGH, URGENT");
    }

    @Test
    void shouldCreateLowPriority() {
        Priority priority = Priority.low();
        
        assertThat(priority.getValue()).isEqualTo("LOW");
    }

    @Test
    void shouldCreateMediumPriority() {
        Priority priority = Priority.medium();
        
        assertThat(priority.getValue()).isEqualTo("MEDIUM");
    }

    @Test
    void shouldCreateHighPriority() {
        Priority priority = Priority.high();
        
        assertThat(priority.getValue()).isEqualTo("HIGH");
    }

    @Test
    void shouldCreateUrgentPriority() {
        Priority priority = Priority.urgent();
        
        assertThat(priority.getValue()).isEqualTo("URGENT");
    }

    @Test
    void shouldBeEqualWhenSameValue() {
        Priority priority1 = Priority.of("HIGH");
        Priority priority2 = Priority.of("HIGH");
        
        assertThat(priority1).isEqualTo(priority2);
        assertThat(priority1.hashCode()).isEqualTo(priority2.hashCode());
    }

    @Test
    void shouldNotBeEqualWhenDifferentValue() {
        Priority priority1 = Priority.of("HIGH");
        Priority priority2 = Priority.of("LOW");
        
        assertThat(priority1).isNotEqualTo(priority2);
    }
}