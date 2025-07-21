package com.formflow.application.order.command;

import java.time.LocalDateTime;

public class CreateOrderCommand {
    private final String title;
    private final String description;
    private final String category;
    private final String priority;
    private final Long creatorId;
    private final Long groupId;
    private final Long teamId;
    private final LocalDateTime dueDate;

    public CreateOrderCommand(String title, String description, String category, String priority, 
                             Long creatorId, Long groupId, Long teamId, LocalDateTime dueDate) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.priority = priority;
        this.creatorId = creatorId;
        this.groupId = groupId;
        this.teamId = teamId;
        this.dueDate = dueDate;
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getCategory() { return category; }
    public String getPriority() { return priority; }
    public Long getCreatorId() { return creatorId; }
    public Long getGroupId() { return groupId; }
    public Long getTeamId() { return teamId; }
    public LocalDateTime getDueDate() { return dueDate; }
}