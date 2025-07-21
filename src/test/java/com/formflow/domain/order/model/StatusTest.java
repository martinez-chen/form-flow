package com.formflow.domain.order.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class StatusTest {

    @Test
    void shouldCreateStatusWithValidValue() {
        Status status = Status.of("PENDING");
        
        assertThat(status.getValue()).isEqualTo("PENDING");
    }

    @Test
    void shouldThrowExceptionForNullValue() {
        assertThatThrownBy(() -> Status.of(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Status value cannot be null or empty");
    }

    @Test
    void shouldThrowExceptionForEmptyValue() {
        assertThatThrownBy(() -> Status.of(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Status value cannot be null or empty");
    }

    @Test
    void shouldThrowExceptionForInvalidValue() {
        assertThatThrownBy(() -> Status.of("INVALID"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid status: INVALID. Valid values are: PENDING, ASSIGNED, IN_PROGRESS, COMPLETED, CANCELLED");
    }

    @Test
    void shouldCreatePendingStatus() {
        Status status = Status.pending();
        
        assertThat(status.getValue()).isEqualTo("PENDING");
    }

    @Test
    void shouldCreateAssignedStatus() {
        Status status = Status.assigned();
        
        assertThat(status.getValue()).isEqualTo("ASSIGNED");
    }

    @Test
    void shouldCreateInProgressStatus() {
        Status status = Status.inProgress();
        
        assertThat(status.getValue()).isEqualTo("IN_PROGRESS");
    }

    @Test
    void shouldCreateCompletedStatus() {
        Status status = Status.completed();
        
        assertThat(status.getValue()).isEqualTo("COMPLETED");
    }

    @Test
    void shouldCreateCancelledStatus() {
        Status status = Status.cancelled();
        
        assertThat(status.getValue()).isEqualTo("CANCELLED");
    }

    @Test
    void shouldBeEqualWhenSameValue() {
        Status status1 = Status.of("PENDING");
        Status status2 = Status.of("PENDING");
        
        assertThat(status1).isEqualTo(status2);
        assertThat(status1.hashCode()).isEqualTo(status2.hashCode());
    }

    @Test
    void shouldNotBeEqualWhenDifferentValue() {
        Status status1 = Status.of("PENDING");
        Status status2 = Status.of("COMPLETED");
        
        assertThat(status1).isNotEqualTo(status2);
    }

    @Test
    void shouldCheckIfPending() {
        Status status = Status.pending();
        
        assertThat(status.isPending()).isTrue();
        assertThat(status.isAssigned()).isFalse();
        assertThat(status.isCompleted()).isFalse();
    }

    @Test
    void shouldCheckIfAssigned() {
        Status status = Status.assigned();
        
        assertThat(status.isAssigned()).isTrue();
        assertThat(status.isPending()).isFalse();
        assertThat(status.isCompleted()).isFalse();
    }

    @Test
    void shouldCheckIfCompleted() {
        Status status = Status.completed();
        
        assertThat(status.isCompleted()).isTrue();
        assertThat(status.isPending()).isFalse();
        assertThat(status.isAssigned()).isFalse();
    }
}