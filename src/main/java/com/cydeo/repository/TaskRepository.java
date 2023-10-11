package com.cydeo.repository;

import com.cydeo.entity.Project;
import com.cydeo.entity.Task;
import com.cydeo.entity.User;
import com.cydeo.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task,Long> {
    @Query("select count (t) from Task t where t.project.projectCode = ?1 and t.taskStatus <> 'COMPlETE'")
    int nonCompletedTask(String projectCode);
    @Query("select count (t) from Task t where t.project.projectCode = ?1 and t.taskStatus = 'COMPLETE'")
    int completedTask(String projectCode);

    List<Task> findAllByProject(Project project);

    List<Task> findAllByTaskStatusIsNotAndAssignedEmployee(Status status , User user);

    List<Task> findAllByTaskStatusAndAssignedEmployee(Status status , User user);
}
