package com.example.calendrier.service;

import com.example.calendrier.entity.Task;
import com.example.calendrier.entity.User;
import com.example.calendrier.repository.TaskRepository;
import com.example.calendrier.service.interfaces.TaskService;
import com.example.calendrier.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
private final TaskRepository taskRepository ;
private final UserService userService ;

    @Override
    public Task createTask(Task task, Long creatorId, Long employeeId) {
        User user= userService.findUserbyId(creatorId);
        User employee = userService.findUserbyId(employeeId);
        task.setCraetedby(user);
        task.setAssignedto(employee);
        return taskRepository.save(task);
    }

    @Override
    public List<Task> getAllTask() {
        return taskRepository.findAll();
    }

    @Override
    public Task getTaskById(Long id) {
        return taskRepository.findById(id).orElse(null);
    }

    @Override
    public Task updateTask(Task task) {
        return taskRepository.save(task);
    }

    @Override
    public void deleteTask(Long id) {
        taskRepository.deleteById(id);

    }

    @Override
    public Task assignTaskToEmployee(Task task, Long assignedtoId) {
        User user= userService.findUserbyId(assignedtoId);
        task.setAssignedto(user);
        return taskRepository.save(task);
    }

    @Override
    public List<Task> findTaskByEmployee_id(Long userId) {
        return taskRepository.findTasksByAssignedto_Id(userId) ;    }

    @Override
    public List<Task> findTasksByCraetedby_Id(Long userId) {
        return taskRepository.findTasksByCraetedby_Id(userId);
    }
}
