package com.formflow.application.order.command;

import lombok.Data;

@Data
public class AssignOrderToGroupCommand {
    private final Long orderId;
    private final Long groupId;
    private final Long requesterId;

    public AssignOrderToGroupCommand(Long orderId, Long groupId, Long requesterId) {
        this.orderId = orderId;
        this.groupId = groupId;
        this.requesterId = requesterId;
    }
}