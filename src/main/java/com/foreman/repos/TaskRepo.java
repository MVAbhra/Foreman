package com.foreman.repos;

import org.springframework.data.jpa.repository.JpaRepository;

import com.foreman.entities.Task;

public interface TaskRepo extends JpaRepository<Task, Long> {

}
