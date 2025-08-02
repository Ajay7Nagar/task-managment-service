package com.talentica.taskmanagement.dto.response;

import com.talentica.taskmanagement.enums.TaskStatus;
import com.talentica.taskmanagement.enums.TaskType;

import java.time.LocalDateTime;
import java.util.List;

public class TaskResponse {

    private Long id;
    private String title;
    private String description;
    private TaskType taskType;
    private TaskStatus taskStatus;
    private Integer storyPoints;
    private Double estimatedHours;
    private Double actualHours;
    private UserResponse assignee;
    private UserResponse reporter;
    private TaskResponse parentTask;
    private List<TaskResponse> subtasks;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime dueDate;
    private LocalDateTime completedAt;

    public TaskResponse() {}

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    public Integer getStoryPoints() {
        return storyPoints;
    }

    public void setStoryPoints(Integer storyPoints) {
        this.storyPoints = storyPoints;
    }

    public Double getEstimatedHours() {
        return estimatedHours;
    }

    public void setEstimatedHours(Double estimatedHours) {
        this.estimatedHours = estimatedHours;
    }

    public Double getActualHours() {
        return actualHours;
    }

    public void setActualHours(Double actualHours) {
        this.actualHours = actualHours;
    }

    public UserResponse getAssignee() {
        return assignee;
    }

    public void setAssignee(UserResponse assignee) {
        this.assignee = assignee;
    }

    public UserResponse getReporter() {
        return reporter;
    }

    public void setReporter(UserResponse reporter) {
        this.reporter = reporter;
    }

    public TaskResponse getParentTask() {
        return parentTask;
    }

    public void setParentTask(TaskResponse parentTask) {
        this.parentTask = parentTask;
    }

    public List<TaskResponse> getSubtasks() {
        return subtasks;
    }

    public void setSubtasks(List<TaskResponse> subtasks) {
        this.subtasks = subtasks;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }
}