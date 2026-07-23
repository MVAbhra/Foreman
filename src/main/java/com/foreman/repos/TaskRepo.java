package com.foreman.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.foreman.entities.Task;

@Repository
public interface TaskRepo extends JpaRepository<Task, Long> {

}
