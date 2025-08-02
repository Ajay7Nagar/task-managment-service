package com.talentica.taskmanagement.enums;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public enum TaskStatus {
    DRAFT("Draft", 0),
    TODO("To Do", 1),
    IN_PROGRESS("In Progress", 2),
    QA("QA", 3),
    READY_TO_DEPLOY("Ready to Deploy", 4),
    DONE("Done", 5);

    private final String displayName;
    private final int order;

    TaskStatus(String displayName, int order) {
        this.displayName = displayName;
        this.order = order;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getOrder() {
        return order;
    }

    // Define allowed transitions
    private static final Map<TaskStatus, List<TaskStatus>> ALLOWED_TRANSITIONS = new HashMap<>();

    static {
        ALLOWED_TRANSITIONS.put(DRAFT, Arrays.asList(TODO));
        ALLOWED_TRANSITIONS.put(TODO, Arrays.asList(IN_PROGRESS));
        ALLOWED_TRANSITIONS.put(IN_PROGRESS, Arrays.asList(QA, TODO)); // Can go back to TODO
        ALLOWED_TRANSITIONS.put(QA, Arrays.asList(READY_TO_DEPLOY, IN_PROGRESS)); // Can go back to IN_PROGRESS
        ALLOWED_TRANSITIONS.put(READY_TO_DEPLOY, Arrays.asList(DONE, QA)); // Can go back to QA
        ALLOWED_TRANSITIONS.put(DONE, Arrays.asList()); // Final state
    }

    public List<TaskStatus> getAllowedTransitions() {
        return ALLOWED_TRANSITIONS.get(this);
    }

    public boolean canTransitionTo(TaskStatus targetStatus) {
        return getAllowedTransitions().contains(targetStatus);
    }
}