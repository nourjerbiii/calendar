package com.example.calendrier.repository;

import com.example.calendrier.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findTasksByAssignedto_Id(Long userId);
    List<Task> findTasksByCraetedby_Id(Long userId);
}