package com.example.calendrier.service.interfaces;

import com.example.calendrier.entity.Task;

import java.util.List;

public interface TaskService {
    Task createTask(Task task, Long creatorId, Long employeeId);
    List<Task> getAllTask();
    Task getTaskById(Long id);
    Task updateTask(Task task);
    void deleteTask(Long id);
    Task assignTaskToEmployee(Task task, Long EmployeeId);
    List<Task> findTaskByEmployee_id(Long userId);
    List<Task> findTasksByCraetedby_Id(Long userId);
}
