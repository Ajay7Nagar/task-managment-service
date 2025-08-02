package com.talentica.taskmanagement.enums;

public enum UserRole {
    ADMIN("Admin"),
    MANAGER("Manager"),
    DEVELOPER("Developer"),
    TESTER("Tester");

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    // Define what task types each role can create
    public boolean canCreate(TaskType taskType) {
        switch (this) {
            case ADMIN:
                return true; // Admin can create everything
            case MANAGER:
                return taskType == TaskType.EPIC || taskType == TaskType.STORY || 
                       taskType == TaskType.SPIKE || taskType == TaskType.TASK;
            case DEVELOPER:
            case TESTER:
                return taskType == TaskType.SUBTASK; // Can only create subtasks under assigned stories
            default:
                return false;
        }
    }
}