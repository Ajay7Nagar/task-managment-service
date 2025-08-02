package com.talentica.taskmanagement.repository;

import com.talentica.taskmanagement.entity.Task;
import com.talentica.taskmanagement.entity.User;
import com.talentica.taskmanagement.enums.TaskStatus;
import com.talentica.taskmanagement.enums.TaskType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByAssignee(User assignee);

    List<Task> findByReporter(User reporter);

    List<Task> findByTaskStatus(TaskStatus taskStatus);

    List<Task> findByTaskType(TaskType taskType);

    List<Task> findByParentTask(Task parentTask);

    List<Task> findByParentTaskIsNull();

    @Query("SELECT t FROM Task t WHERE t.assignee = :assignee AND t.taskStatus = :status")
    List<Task> findByAssigneeAndStatus(@Param("assignee") User assignee, @Param("status") TaskStatus status);

    @Query("SELECT t FROM Task t WHERE t.assignee = :assignee AND t.taskStatus IN :statuses")
    List<Task> findByAssigneeAndStatusIn(@Param("assignee") User assignee, @Param("statuses") List<TaskStatus> statuses);

    @Query("SELECT t FROM Task t WHERE t.taskType = :taskType AND t.assignee = :assignee")
    List<Task> findByTaskTypeAndAssignee(@Param("taskType") TaskType taskType, @Param("assignee") User assignee);

    @Query("SELECT t FROM Task t WHERE t.dueDate IS NOT NULL AND t.dueDate < :date AND t.taskStatus != :status")
    List<Task> findOverdueTasks(@Param("date") LocalDateTime date, @Param("status") TaskStatus status);

    @Query("SELECT t FROM Task t WHERE t.parentTask IS NULL AND " +
           "(LOWER(t.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(t.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<Task> searchParentTasks(@Param("searchTerm") String searchTerm);

    @Query("SELECT t FROM Task t WHERE t.parentTask = :parentTask AND t.taskType = :taskType")
    List<Task> findSubtasksByParentAndType(@Param("parentTask") Task parentTask, @Param("taskType") TaskType taskType);

    @Query("SELECT t FROM Task t WHERE t.assignee = :user OR t.reporter = :user")
    List<Task> findTasksByUser(@Param("user") User user);

    @Query("SELECT t FROM Task t WHERE t.taskType IN :taskTypes AND t.taskStatus IN :statuses")
    List<Task> findByTaskTypesAndStatuses(@Param("taskTypes") List<TaskType> taskTypes, @Param("statuses") List<TaskStatus> statuses);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.assignee = :assignee AND t.taskStatus = :status")
    Long countByAssigneeAndStatus(@Param("assignee") User assignee, @Param("status") TaskStatus status);
}