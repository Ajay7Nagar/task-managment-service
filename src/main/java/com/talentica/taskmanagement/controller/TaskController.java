package com.talentica.taskmanagement.controller;

import com.talentica.taskmanagement.dto.request.TaskCreateRequest;
import com.talentica.taskmanagement.dto.request.TaskStatusTransitionRequest;
import com.talentica.taskmanagement.dto.request.TaskUpdateRequest;
import com.talentica.taskmanagement.dto.response.TaskResponse;
import com.talentica.taskmanagement.entity.User;
import com.talentica.taskmanagement.enums.TaskStatus;
import com.talentica.taskmanagement.enums.TaskType;
import com.talentica.taskmanagement.service.TaskService;
import com.talentica.taskmanagement.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@Api(tags = "Task Management", description = "Task management endpoints")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserService userService;

    @PostMapping
    @ApiOperation(value = "Create task", notes = "Create a new task")
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody TaskCreateRequest request,
                                                   Authentication authentication) {
        User reporter = userService.findUserEntityByUsername(authentication.getName());
        TaskResponse taskResponse = taskService.createTask(request, reporter);
        return new ResponseEntity<>(taskResponse, HttpStatus.CREATED);
    }

    @GetMapping
    @ApiOperation(value = "Get all tasks", notes = "Get list of all tasks")
    public ResponseEntity<List<TaskResponse>> getAllTasks() {
        List<TaskResponse> tasks = taskService.getAllTasks();
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Get task by ID", notes = "Get task details by ID")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable Long id) {
        TaskResponse task = taskService.getTaskById(id);
        return ResponseEntity.ok(task);
    }

    @GetMapping("/my")
    @ApiOperation(value = "Get my tasks", notes = "Get tasks for current user")
    public ResponseEntity<List<TaskResponse>> getMyTasks(Authentication authentication) {
        User user = userService.findUserEntityByUsername(authentication.getName());
        List<TaskResponse> tasks = taskService.getTasksForUser(user);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/assignee/{assigneeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or @taskService.canUserEditTask(authentication.principal, #assigneeId)")
    @ApiOperation(value = "Get tasks by assignee", notes = "Get tasks assigned to a specific user")
    public ResponseEntity<List<TaskResponse>> getTasksByAssignee(@PathVariable Long assigneeId) {
        List<TaskResponse> tasks = taskService.getTasksByAssignee(assigneeId);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/reporter/{reporterId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or authentication.principal.id == #reporterId")
    @ApiOperation(value = "Get tasks by reporter", notes = "Get tasks reported by a specific user")
    public ResponseEntity<List<TaskResponse>> getTasksByReporter(@PathVariable Long reporterId) {
        List<TaskResponse> tasks = taskService.getTasksByReporter(reporterId);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/status/{status}")
    @ApiOperation(value = "Get tasks by status", notes = "Get tasks by status")
    public ResponseEntity<List<TaskResponse>> getTasksByStatus(@PathVariable TaskStatus status) {
        List<TaskResponse> tasks = taskService.getTasksByStatus(status);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/type/{type}")
    @ApiOperation(value = "Get tasks by type", notes = "Get tasks by type")
    public ResponseEntity<List<TaskResponse>> getTasksByType(@PathVariable TaskType type) {
        List<TaskResponse> tasks = taskService.getTasksByType(type);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{parentId}/subtasks")
    @ApiOperation(value = "Get subtasks", notes = "Get subtasks of a parent task")
    public ResponseEntity<List<TaskResponse>> getSubtasks(@PathVariable Long parentId) {
        List<TaskResponse> subtasks = taskService.getSubtasks(parentId);
        return ResponseEntity.ok(subtasks);
    }

    @GetMapping("/search")
    @ApiOperation(value = "Search tasks", notes = "Search tasks by title or description")
    public ResponseEntity<List<TaskResponse>> searchTasks(@RequestParam String searchTerm) {
        List<TaskResponse> tasks = taskService.searchTasks(searchTerm);
        return ResponseEntity.ok(tasks);
    }

    @PutMapping("/{id}")
    @ApiOperation(value = "Update task", notes = "Update task details")
    public ResponseEntity<TaskResponse> updateTask(@PathVariable Long id,
                                                   @Valid @RequestBody TaskUpdateRequest request,
                                                   Authentication authentication) {
        User currentUser = userService.findUserEntityByUsername(authentication.getName());
        TaskResponse updatedTask = taskService.updateTask(id, request, currentUser);
        return ResponseEntity.ok(updatedTask);
    }

    @PutMapping("/{id}/transition")
    @ApiOperation(value = "Transition task status", notes = "Transition task to a new status")
    public ResponseEntity<TaskResponse> transitionTaskStatus(@PathVariable Long id,
                                                             @Valid @RequestBody TaskStatusTransitionRequest request,
                                                             Authentication authentication) {
        User currentUser = userService.findUserEntityByUsername(authentication.getName());
        TaskResponse updatedTask = taskService.transitionTaskStatus(id, request, currentUser);
        return ResponseEntity.ok(updatedTask);
    }

    @PutMapping("/{taskId}/assign/{assigneeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or @taskService.canUserEditTask(authentication.principal, #taskId)")
    @ApiOperation(value = "Assign task", notes = "Assign task to a user")
    public ResponseEntity<TaskResponse> assignTask(@PathVariable Long taskId,
                                                   @PathVariable Long assigneeId,
                                                   Authentication authentication) {
        User currentUser = userService.findUserEntityByUsername(authentication.getName());
        TaskResponse updatedTask = taskService.assignTask(taskId, assigneeId, currentUser);
        return ResponseEntity.ok(updatedTask);
    }

    @GetMapping("/{id}/transitions")
    @ApiOperation(value = "Get available transitions", notes = "Get available status transitions for a task")
    public ResponseEntity<List<TaskStatus>> getAvailableTransitions(@PathVariable Long id) {
        List<TaskStatus> transitions = taskService.getAvailableTransitions(id);
        return ResponseEntity.ok(transitions);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @ApiOperation(value = "Delete task", notes = "Delete a task")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id, Authentication authentication) {
        User currentUser = userService.findUserEntityByUsername(authentication.getName());
        taskService.deleteTask(id, currentUser);
        return ResponseEntity.ok().build();
    }
}