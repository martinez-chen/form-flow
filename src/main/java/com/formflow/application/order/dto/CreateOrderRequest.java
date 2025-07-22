package com.formflow.application.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {
    
    @NotBlank(message = "Title cannot be blank")
    @Size(max = 200, message = "Title must not exceed 200 characters")
    private String title;
    
    @Size(max = 5000, message = "Description must not exceed 5000 characters")
    private String description;
    
    @NotBlank(message = "Category cannot be blank")
    @Size(max = 50, message = "Category must not exceed 50 characters")
    private String category;
    
    @NotBlank(message = "Priority cannot be blank")
    private String priority;
    
    @NotNull(message = "Creator ID cannot be null")
    private Long creatorId;
    
    private Long groupId;
    
    private Long teamId;
    
    private LocalDateTime dueDate;

}