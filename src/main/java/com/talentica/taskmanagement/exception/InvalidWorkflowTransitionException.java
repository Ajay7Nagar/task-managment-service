package com.talentica.taskmanagement.exception;

public class InvalidWorkflowTransitionException extends RuntimeException {

    public InvalidWorkflowTransitionException(String message) {
        super(message);
    }

    public InvalidWorkflowTransitionException(String message, Throwable cause) {
        super(message, cause);
    }
}