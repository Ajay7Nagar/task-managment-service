package com.talentica.taskmanagement.enums;

public enum TaskType {
    EPIC("Epic"),
    STORY("Story"),
    TASK("Task"),
    SUBTASK("Subtask"),
    SPIKE("Spike");

    private final String displayName;

    TaskType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}