package com.formflow.application.order.command;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class AssignOrderToGroupCommandTest {

    @Test
    void shouldCreateCommandWithValidParameters() {
        Long orderId = 1L;
        Long groupId = 100L;
        Long requesterId = 50L;
        
        AssignOrderToGroupCommand command = new AssignOrderToGroupCommand(orderId, groupId, requesterId);
        
        assertThat(command.getOrderId()).isEqualTo(orderId);
        assertThat(command.getGroupId()).isEqualTo(groupId);
        assertThat(command.getRequesterId()).isEqualTo(requesterId);
    }

    @Test
    void shouldAllowNullValues() {
        AssignOrderToGroupCommand command = new AssignOrderToGroupCommand(null, null, null);
        
        assertThat(command.getOrderId()).isNull();
        assertThat(command.getGroupId()).isNull();
        assertThat(command.getRequesterId()).isNull();
    }
}