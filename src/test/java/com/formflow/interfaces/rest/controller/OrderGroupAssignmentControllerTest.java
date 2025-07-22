package com.formflow.interfaces.rest.controller;

import com.formflow.application.order.command.AssignOrderToGroupCommand;
import com.formflow.application.order.service.OrderApplicationService;
import com.formflow.domain.order.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
class OrderGroupAssignmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderApplicationService orderApplicationService;

    @MockBean
    private OrderRepository orderRepository;

    @Test
    @WithMockUser
    void shouldAssignOrderToGroup() throws Exception {
        Long orderId = 1L;
        String requestBody = """
                {
                    "groupId": 100,
                    "requesterId": 50
                }
                """;

        mockMvc.perform(put("/api/v1/orders/{id}/assign-to-group", orderId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Order assigned to group successfully"));

        verify(orderApplicationService).assignOrderToGroup(any(AssignOrderToGroupCommand.class));
    }

    @Test
    @WithMockUser
    void shouldReturnBadRequestWhenGroupIdIsNull() throws Exception {
        Long orderId = 1L;
        String requestBody = """
                {
                    "groupId": null,
                    "requesterId": 50
                }
                """;

        mockMvc.perform(put("/api/v1/orders/{id}/assign-to-group", orderId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verify(orderApplicationService, never()).assignOrderToGroup(any());
    }

    @Test
    @WithMockUser
    void shouldReturnBadRequestWhenRequesterIdIsNull() throws Exception {
        Long orderId = 1L;
        String requestBody = """
                {
                    "groupId": 100,
                    "requesterId": null
                }
                """;

        mockMvc.perform(put("/api/v1/orders/{id}/assign-to-group", orderId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verify(orderApplicationService, never()).assignOrderToGroup(any());
    }

    @Test
    @WithMockUser
    void shouldReturnNotFoundWhenOrderDoesNotExist() throws Exception {
        Long orderId = 999L;
        String requestBody = """
                {
                    "groupId": 100,
                    "requesterId": 50
                }
                """;

        doThrow(new IllegalArgumentException("Order not found: " + orderId))
                .when(orderApplicationService).assignOrderToGroup(any(AssignOrderToGroupCommand.class));

        mockMvc.perform(put("/api/v1/orders/{id}/assign-to-group", orderId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Order not found: " + orderId));

        verify(orderApplicationService).assignOrderToGroup(any(AssignOrderToGroupCommand.class));
    }

    @Test
    @WithMockUser
    void shouldReturnConflictWhenOrderCannotBeAssigned() throws Exception {
        Long orderId = 1L;
        String requestBody = """
                {
                    "groupId": 100,
                    "requesterId": 50
                }
                """;

        doThrow(new IllegalStateException("Cannot assign completed order to group"))
                .when(orderApplicationService).assignOrderToGroup(any(AssignOrderToGroupCommand.class));

        mockMvc.perform(put("/api/v1/orders/{id}/assign-to-group", orderId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Cannot assign completed order to group"));

        verify(orderApplicationService).assignOrderToGroup(any(AssignOrderToGroupCommand.class));
    }
}