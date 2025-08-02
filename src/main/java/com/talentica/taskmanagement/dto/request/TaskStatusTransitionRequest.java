package com.talentica.taskmanagement.dto.request;

import com.talentica.taskmanagement.enums.TaskStatus;

import jakarta.validation.constraints.NotNull;

public class TaskStatusTransitionRequest {

    @NotNull(message = "Target status is required")
    private TaskStatus targetStatus;

    private String comment;

    public TaskStatusTransitionRequest() {}

    public TaskStatusTransitionRequest(TaskStatus targetStatus, String comment) {
        this.targetStatus = targetStatus;
        this.comment = comment;
    }

    public TaskStatus getTargetStatus() {
        return targetStatus;
    }

    public void setTargetStatus(TaskStatus targetStatus) {
        this.targetStatus = targetStatus;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}