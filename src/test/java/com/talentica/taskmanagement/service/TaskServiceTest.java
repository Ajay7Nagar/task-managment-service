package com.talentica.taskmanagement.service;

import com.talentica.taskmanagement.dto.request.TaskCreateRequest;
import com.talentica.taskmanagement.dto.request.TaskStatusTransitionRequest;
import com.talentica.taskmanagement.dto.request.TaskUpdateRequest;
import com.talentica.taskmanagement.dto.response.TaskResponse;
import com.talentica.taskmanagement.entity.Task;
import com.talentica.taskmanagement.entity.User;
import com.talentica.taskmanagement.enums.TaskStatus;
import com.talentica.taskmanagement.enums.TaskType;
import com.talentica.taskmanagement.enums.UserRole;
import com.talentica.taskmanagement.exception.InvalidWorkflowTransitionException;
import com.talentica.taskmanagement.exception.ResourceNotFoundException;
import com.talentica.taskmanagement.exception.UnauthorizedException;
import com.talentica.taskmanagement.repository.TaskRepository;
import com.talentica.taskmanagement.service.impl.TaskServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private TaskServiceImpl taskService;

    private User adminUser;
    private User managerUser;
    private User developerUser;
    private Task testTask;
    private TaskCreateRequest createRequest;

    @BeforeEach
    void setUp() {
        adminUser = createUser(1L, "admin", UserRole.ADMIN);
        managerUser = createUser(2L, "manager", UserRole.MANAGER);
        developerUser = createUser(3L, "developer", UserRole.DEVELOPER);

        testTask = new Task();
        testTask.setId(1L);
        testTask.setTitle("Test Task");
        testTask.setDescription("Test Description");
        testTask.setTaskType(TaskType.STORY);
        testTask.setTaskStatus(TaskStatus.DRAFT);
        testTask.setReporter(managerUser);
        testTask.setCreatedAt(LocalDateTime.now());
        testTask.setUpdatedAt(LocalDateTime.now());

        createRequest = new TaskCreateRequest();
        createRequest.setTitle("New Task");
        createRequest.setDescription("New Description");
        createRequest.setTaskType(TaskType.STORY);
    }

    private User createUser(Long id, String username, UserRole role) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setRole(role);
        user.setIsActive(true);
        return user;
    }

    @Test
    void createTask_Success() {
        // Given
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        // When
        TaskResponse response = taskService.createTask(createRequest, managerUser);

        // Then
        assertNotNull(response);
        assertEquals(testTask.getTitle(), response.getTitle());
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void createTask_UnauthorizedRole_ThrowsException() {
        // Given
        createRequest.setTaskType(TaskType.EPIC);

        // When & Then
        assertThrows(UnauthorizedException.class, () -> {
            taskService.createTask(createRequest, developerUser);
        });
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void getTaskById_Success() {
        // Given
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));

        // When
        TaskResponse response = taskService.getTaskById(1L);

        // Then
        assertNotNull(response);
        assertEquals(testTask.getId(), response.getId());
        assertEquals(testTask.getTitle(), response.getTitle());
    }

    @Test
    void getTaskById_NotFound_ThrowsException() {
        // Given
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            taskService.getTaskById(1L);
        });
    }

    @Test
    void updateTask_Success() {
        // Given
        TaskUpdateRequest updateRequest = new TaskUpdateRequest();
        updateRequest.setTitle("Updated Title");
        updateRequest.setDescription("Updated Description");

        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        // When
        TaskResponse response = taskService.updateTask(1L, updateRequest, managerUser);

        // Then
        assertNotNull(response);
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void updateTask_Unauthorized_ThrowsException() {
        // Given
        TaskUpdateRequest updateRequest = new TaskUpdateRequest();
        updateRequest.setTitle("Updated Title");

        User unauthorizedUser = createUser(4L, "unauthorized", UserRole.DEVELOPER);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));

        // When & Then
        assertThrows(UnauthorizedException.class, () -> {
            taskService.updateTask(1L, updateRequest, unauthorizedUser);
        });
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void transitionTaskStatus_Success() {
        // Given
        TaskStatusTransitionRequest transitionRequest = new TaskStatusTransitionRequest();
        transitionRequest.setTargetStatus(TaskStatus.TODO);

        testTask.setAssignee(developerUser);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        // When
        TaskResponse response = taskService.transitionTaskStatus(1L, transitionRequest, developerUser);

        // Then
        assertNotNull(response);
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void transitionTaskStatus_InvalidTransition_ThrowsException() {
        // Given
        TaskStatusTransitionRequest transitionRequest = new TaskStatusTransitionRequest();
        transitionRequest.setTargetStatus(TaskStatus.DONE); // Can't go directly from DRAFT to DONE

        testTask.setAssignee(developerUser);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));

        // When & Then
        assertThrows(InvalidWorkflowTransitionException.class, () -> {
            taskService.transitionTaskStatus(1L, transitionRequest, developerUser);
        });
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void canUserCreateTaskType_AdminCanCreateAll() {
        // Given
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        
        // When & Then
        assertTrue(taskService.canUserCreateTaskType(adminUser, TaskType.EPIC, null));
        assertTrue(taskService.canUserCreateTaskType(adminUser, TaskType.STORY, null));
        assertTrue(taskService.canUserCreateTaskType(adminUser, TaskType.TASK, null));
        assertTrue(taskService.canUserCreateTaskType(adminUser, TaskType.SUBTASK, 1L));
        assertTrue(taskService.canUserCreateTaskType(adminUser, TaskType.SPIKE, null));
    }

    @Test
    void canUserCreateTaskType_ManagerCanCreateMost() {
        // When & Then
        assertTrue(taskService.canUserCreateTaskType(managerUser, TaskType.EPIC, null));
        assertTrue(taskService.canUserCreateTaskType(managerUser, TaskType.STORY, null));
        assertTrue(taskService.canUserCreateTaskType(managerUser, TaskType.TASK, null));
        assertTrue(taskService.canUserCreateTaskType(managerUser, TaskType.SPIKE, null));
    }

    @Test
    void canUserCreateTaskType_DeveloperCanOnlyCreateSubtasks() {
        // Given
        testTask.setTaskType(TaskType.STORY);
        testTask.setAssignee(developerUser);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        
        // When & Then
        assertFalse(taskService.canUserCreateTaskType(developerUser, TaskType.EPIC, null));
        assertFalse(taskService.canUserCreateTaskType(developerUser, TaskType.STORY, null));
        assertFalse(taskService.canUserCreateTaskType(developerUser, TaskType.TASK, null));
        assertTrue(taskService.canUserCreateTaskType(developerUser, TaskType.SUBTASK, 1L));
        assertFalse(taskService.canUserCreateTaskType(developerUser, TaskType.SPIKE, null));
    }

    @Test
    void canUserEditTask_AdminCanEditAll() {
        // Given
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));

        // When & Then
        assertTrue(taskService.canUserEditTask(adminUser, 1L));
    }

    @Test
    void canUserEditTask_ReporterCanEditOwnTasks() {
        // Given
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));

        // When & Then
        assertTrue(taskService.canUserEditTask(managerUser, 1L)); // managerUser is the reporter
    }

    @Test
    void canUserEditTask_AssigneeCanEditAssignedTasks() {
        // Given
        testTask.setAssignee(developerUser);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));

        // When & Then
        assertTrue(taskService.canUserEditTask(developerUser, 1L));
    }

    @Test
    void getTasksByStatus_Success() {
        // Given
        List<Task> tasks = Arrays.asList(testTask);
        when(taskRepository.findByTaskStatus(TaskStatus.DRAFT)).thenReturn(tasks);

        // When
        List<TaskResponse> responses = taskService.getTasksByStatus(TaskStatus.DRAFT);

        // Then
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(testTask.getTitle(), responses.get(0).getTitle());
    }

    @Test
    void assignTask_Success() {
        // Given
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        when(userService.findUserEntityById(3L)).thenReturn(developerUser);
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        // When
        TaskResponse response = taskService.assignTask(1L, 3L, managerUser);

        // Then
        assertNotNull(response);
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void deleteTask_Success() {
        // Given
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));

        // When
        taskService.deleteTask(1L, managerUser);

        // Then
        verify(taskRepository).delete(testTask);
    }

    @Test
    void deleteTask_Unauthorized_ThrowsException() {
        // Given
        User unauthorizedUser = createUser(4L, "unauthorized", UserRole.DEVELOPER);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));

        // When & Then
        assertThrows(UnauthorizedException.class, () -> {
            taskService.deleteTask(1L, unauthorizedUser);
        });
        verify(taskRepository, never()).delete(any(Task.class));
    }
}