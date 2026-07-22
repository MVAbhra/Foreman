package com.foreman.repos;

import org.springframework.data.jpa.repository.JpaRepository;

import com.foreman.entities.Project;

public interface ProjectRepo extends JpaRepository<Project, Long> {

}
