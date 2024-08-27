package com.example.calendrier.Controller;

import com.example.calendrier.entity.Task;
import com.example.calendrier.entity.User;
import com.example.calendrier.payload.response.MessageResponse;
import com.example.calendrier.service.interfaces.TaskService;
import com.example.calendrier.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import net.bytebuddy.agent.builder.AgentBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/task")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService ;
    private final UserService userService;

    @GetMapping("/getAlltasks")
    public List<Task> getAll(){
        return taskService.getAllTask();
    }

    @PostMapping("/createtask/{creatorId}/{employeeId}")
    public ResponseEntity<?> createTask(@RequestBody Task task, @PathVariable Long creatorId, @PathVariable Long employeeId) {
        try {
            User user = userService.findUserbyId(creatorId);
            if (user == null) {
                return ResponseEntity.badRequest().body(new MessageResponse("ERROR: User not found!"));
            }
            task.setCraetedby(user);
            Task createdTask = taskService.createTask(task, creatorId,employeeId);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse("Failed to create task!"));
        }
    }

    @GetMapping("/gettaskbyid/{id}")
    public Task getTaskbyId(@PathVariable Long id){
        return taskService.getTaskById(id);
    }

    @GetMapping("/gettaskbyemployee/{userId}")
    public List<Task> findTaskByEmployeeId(@PathVariable Long userId){
        return taskService.findTaskByEmployee_id(userId);
    }

    @GetMapping("/gettaskbycreated/{userId}")
    public List<Task> findTasksByCraetedby_Id(@PathVariable Long userId){
        return taskService.findTasksByCraetedby_Id(userId);
    }

    @PutMapping("/updatetask")
    public Task updateTask(@RequestBody Task task){
        return taskService.updateTask(task);
    }

    @DeleteMapping("deletetask/{id}")
    public void deleteTask(@PathVariable Long id){
        taskService.deleteTask(id);
    }

    @PutMapping("/assign/{taskId}/{employeeId}")
    public ResponseEntity<?> assignTaskToEmployee(@PathVariable Long taskId, @PathVariable Long employeeId) {
        try {
            Task task = taskService.getTaskById(taskId);
            if (task == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("Task not found!"));
            }
            User user = userService.findUserbyId(employeeId);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("User not found!"));
            }
            Task updatedTask = taskService.assignTaskToEmployee(task, employeeId);
            return ResponseEntity.ok(updatedTask);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse("Failed to assign task!"));
        }
    }

}
