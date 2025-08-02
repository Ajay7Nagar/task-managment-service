package com.talentica.taskmanagement.service;

import com.talentica.taskmanagement.dto.request.TaskCreateRequest;
import com.talentica.taskmanagement.dto.request.TaskStatusTransitionRequest;
import com.talentica.taskmanagement.dto.request.TaskUpdateRequest;
import com.talentica.taskmanagement.dto.response.TaskResponse;
import com.talentica.taskmanagement.entity.User;
import com.talentica.taskmanagement.enums.TaskStatus;
import com.talentica.taskmanagement.enums.TaskType;

import java.util.List;

public interface TaskService {

    TaskResponse createTask(TaskCreateRequest request, User reporter);

    TaskResponse getTaskById(Long id);

    List<TaskResponse> getAllTasks();

    List<TaskResponse> getTasksByAssignee(Long assigneeId);

    List<TaskResponse> getTasksByReporter(Long reporterId);

    List<TaskResponse> getTasksByStatus(TaskStatus status);

    List<TaskResponse> getTasksByType(TaskType taskType);

    List<TaskResponse> getSubtasks(Long parentTaskId);

    TaskResponse updateTask(Long id, TaskUpdateRequest request, User currentUser);

    TaskResponse transitionTaskStatus(Long id, TaskStatusTransitionRequest request, User currentUser);

    TaskResponse assignTask(Long taskId, Long assigneeId, User currentUser);

    void deleteTask(Long id, User currentUser);

    List<TaskResponse> searchTasks(String searchTerm);

    List<TaskResponse> getTasksForUser(User user);

    boolean canUserCreateTaskType(User user, TaskType taskType, Long parentTaskId);

    boolean canUserEditTask(User user, Long taskId);

    boolean canUserTransitionTask(User user, Long taskId);

    List<TaskStatus> getAvailableTransitions(Long taskId);
}