package com.talentica.taskmanagement.service.impl;


import jakarta.persistence.Id;
import com.talentica.taskmanagement.dto.request.TaskCreateRequest;
import com.talentica.taskmanagement.dto.request.TaskStatusTransitionRequest;
import com.talentica.taskmanagement.dto.request.TaskUpdateRequest;
import com.talentica.taskmanagement.dto.response.TaskResponse;
import com.talentica.taskmanagement.dto.response.UserResponse;
import com.talentica.taskmanagement.entity.Task;
import com.talentica.taskmanagement.entity.User;
import com.talentica.taskmanagement.enums.TaskStatus;
import com.talentica.taskmanagement.enums.TaskType;
import com.talentica.taskmanagement.enums.UserRole;
import com.talentica.taskmanagement.exception.InvalidWorkflowTransitionException;
import com.talentica.taskmanagement.exception.ResourceNotFoundException;
import com.talentica.taskmanagement.exception.UnauthorizedException;
import com.talentica.taskmanagement.repository.TaskRepository;
import com.talentica.taskmanagement.service.TaskService;
import com.talentica.taskmanagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserService userService;

    @Override
    public TaskResponse createTask(TaskCreateRequest request, User reporter) {
        // Validate if user can create this task type
        if (!canUserCreateTaskType(reporter, request.getTaskType(), request.getParentTaskId())) {
            throw new UnauthorizedException("User is not authorized to create " + request.getTaskType().getDisplayName());
        }

        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setTaskType(request.getTaskType());
        task.setReporter(reporter);
        task.setStoryPoints(request.getStoryPoints());
        task.setEstimatedHours(request.getEstimatedHours());
        task.setDueDate(request.getDueDate());

        // Handle parent task for subtasks
        if (request.getParentTaskId() != null) {
            Task parentTask = findTaskEntityById(request.getParentTaskId());
            task.setParentTask(parentTask);
        }

        // Handle assignee
        if (request.getAssigneeId() != null) {
            User assignee = userService.findUserEntityById(request.getAssigneeId());
            task.setAssignee(assignee);
        }

        Task savedTask = taskRepository.save(task);
        return mapToTaskResponse(savedTask);
    }

    @Override
    @Transactional(readOnly = true)
    public TaskResponse getTaskById(Long id) {
        Task task = findTaskEntityById(id);
        return mapToTaskResponse(task);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> getAllTasks() {
        return taskRepository.findAll()
                .stream()
                .map(this::mapToTaskResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> getTasksByAssignee(Long assigneeId) {
        User assignee = userService.findUserEntityById(assigneeId);
        return taskRepository.findByAssignee(assignee)
                .stream()
                .map(this::mapToTaskResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> getTasksByReporter(Long reporterId) {
        User reporter = userService.findUserEntityById(reporterId);
        return taskRepository.findByReporter(reporter)
                .stream()
                .map(this::mapToTaskResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> getTasksByStatus(TaskStatus status) {
        return taskRepository.findByTaskStatus(status)
                .stream()
                .map(this::mapToTaskResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> getTasksByType(TaskType taskType) {
        return taskRepository.findByTaskType(taskType)
                .stream()
                .map(this::mapToTaskResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> getSubtasks(Long parentTaskId) {
        Task parentTask = findTaskEntityById(parentTaskId);
        return taskRepository.findByParentTask(parentTask)
                .stream()
                .map(this::mapToTaskResponse)
                .collect(Collectors.toList());
    }

    @Override
    public TaskResponse updateTask(Long id, TaskUpdateRequest request, User currentUser) {
        Task task = findTaskEntityById(id);

        if (!canUserEditTask(currentUser, id)) {
            throw new UnauthorizedException("User is not authorized to edit this task");
        }

        if (request.getTitle() != null) {
            task.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            task.setDescription(request.getDescription());
        }
        if (request.getStoryPoints() != null) {
            task.setStoryPoints(request.getStoryPoints());
        }
        if (request.getEstimatedHours() != null) {
            task.setEstimatedHours(request.getEstimatedHours());
        }
        if (request.getActualHours() != null) {
            task.setActualHours(request.getActualHours());
        }
        if (request.getDueDate() != null) {
            task.setDueDate(request.getDueDate());
        }
        if (request.getAssigneeId() != null) {
            User assignee = userService.findUserEntityById(request.getAssigneeId());
            task.setAssignee(assignee);
        }

        Task updatedTask = taskRepository.save(task);
        return mapToTaskResponse(updatedTask);
    }

    @Override
    public TaskResponse transitionTaskStatus(Long id, TaskStatusTransitionRequest request, User currentUser) {
        Task task = findTaskEntityById(id);

        if (!canUserTransitionTask(currentUser, id)) {
            throw new UnauthorizedException("User is not authorized to transition this task");
        }

        if (!task.canTransitionTo(request.getTargetStatus())) {
            throw new InvalidWorkflowTransitionException(
                    String.format("Cannot transition task from %s to %s", 
                            task.getTaskStatus().getDisplayName(), 
                            request.getTargetStatus().getDisplayName())
            );
        }

        task.transitionTo(request.getTargetStatus());
        Task updatedTask = taskRepository.save(task);
        return mapToTaskResponse(updatedTask);
    }

    @Override
    public TaskResponse assignTask(Long taskId, Long assigneeId, User currentUser) {
        Task task = findTaskEntityById(taskId);
        User assignee = userService.findUserEntityById(assigneeId);

        // Only admin, manager, or task reporter can assign tasks
        if (currentUser.getRole() != UserRole.ADMIN && 
            currentUser.getRole() != UserRole.MANAGER && 
            !task.getReporter().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("User is not authorized to assign this task");
        }

        task.setAssignee(assignee);
        Task updatedTask = taskRepository.save(task);
        return mapToTaskResponse(updatedTask);
    }

    @Override
    public void deleteTask(Long id, User currentUser) {
        Task task = findTaskEntityById(id);

        // Only admin, manager, or task reporter can delete tasks
        if (currentUser.getRole() != UserRole.ADMIN && 
            currentUser.getRole() != UserRole.MANAGER && 
            !task.getReporter().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("User is not authorized to delete this task");
        }

        taskRepository.delete(task);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> searchTasks(String searchTerm) {
        return taskRepository.searchParentTasks(searchTerm)
                .stream()
                .map(this::mapToTaskResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> getTasksForUser(User user) {
        return taskRepository.findTasksByUser(user)
                .stream()
                .map(this::mapToTaskResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canUserCreateTaskType(User user, TaskType taskType, Long parentTaskId) {
        // Check basic role permissions
        if (!user.getRole().canCreate(taskType)) {
            return false;
        }

        // For subtasks, check if user can create under the parent task
        if (taskType == TaskType.SUBTASK && parentTaskId != null) {
            Task parentTask = findTaskEntityById(parentTaskId);
            // Developer/Tester can only create subtasks under their assigned stories
            if ((user.getRole() == UserRole.DEVELOPER || user.getRole() == UserRole.TESTER)) {
                return parentTask.getAssignee() != null && 
                       parentTask.getAssignee().getId().equals(user.getId()) &&
                       parentTask.getTaskType() == TaskType.STORY;
            }
        }

        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canUserEditTask(User user, Long taskId) {
        Task task = findTaskEntityById(taskId);

        // Admin can edit any task
        if (user.getRole() == UserRole.ADMIN) {
            return true;
        }

        // Manager can edit any task
        if (user.getRole() == UserRole.MANAGER) {
            return true;
        }

        // Reporter can edit their own tasks
        if (task.getReporter().getId().equals(user.getId())) {
            return true;
        }

        // Assignee can edit assigned tasks
        if (task.getAssignee() != null && task.getAssignee().getId().equals(user.getId())) {
            return true;
        }

        return false;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canUserTransitionTask(User user, Long taskId) {
        Task task = findTaskEntityById(taskId);

        // Admin can transition any task
        if (user.getRole() == UserRole.ADMIN) {
            return true;
        }

        // Assignee can transition assigned tasks
        if (task.getAssignee() != null && task.getAssignee().getId().equals(user.getId())) {
            return true;
        }

        // Manager can transition any task
        if (user.getRole() == UserRole.MANAGER) {
            return true;
        }

        return false;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskStatus> getAvailableTransitions(Long taskId) {
        Task task = findTaskEntityById(taskId);
        return task.getTaskStatus().getAllowedTransitions();
    }

    private Task findTaskEntityById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
    }

    private TaskResponse mapToTaskResponse(Task task) {
        TaskResponse response = new TaskResponse();
        response.setId(task.getId());
        response.setTitle(task.getTitle());
        response.setDescription(task.getDescription());
        response.setTaskType(task.getTaskType());
        response.setTaskStatus(task.getTaskStatus());
        response.setStoryPoints(task.getStoryPoints());
        response.setEstimatedHours(task.getEstimatedHours());
        response.setActualHours(task.getActualHours());
        response.setCreatedAt(task.getCreatedAt());
        response.setUpdatedAt(task.getUpdatedAt());
        response.setDueDate(task.getDueDate());
        response.setCompletedAt(task.getCompletedAt());

        if (task.getAssignee() != null) {
            response.setAssignee(mapToUserResponse(task.getAssignee()));
        }

        if (task.getReporter() != null) {
            response.setReporter(mapToUserResponse(task.getReporter()));
        }

        if (task.getParentTask() != null) {
            TaskResponse parentResponse = new TaskResponse();
            parentResponse.setId(task.getParentTask().getId());
            parentResponse.setTitle(task.getParentTask().getTitle());
            parentResponse.setTaskType(task.getParentTask().getTaskType());
            response.setParentTask(parentResponse);
        }

        if (task.getSubtasks() != null && !task.getSubtasks().isEmpty()) {
            List<TaskResponse> subtaskResponses = task.getSubtasks().stream()
                    .map(this::mapToTaskResponse)
                    .collect(Collectors.toList());
            response.setSubtasks(subtaskResponses);
        }

        return response;
    }

    private UserResponse mapToUserResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole(),
                user.getIsActive(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}