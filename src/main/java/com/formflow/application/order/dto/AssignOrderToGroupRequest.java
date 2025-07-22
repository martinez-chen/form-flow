package com.formflow.application.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignOrderToGroupRequest {
    
    @NotNull(message = "Group ID cannot be null")
    private Long groupId;
    
    @NotNull(message = "Requester ID cannot be null")
    private Long requesterId;
}