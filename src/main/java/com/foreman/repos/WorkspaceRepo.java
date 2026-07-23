package com.foreman.repos;

import org.springframework.data.jpa.repository.JpaRepository;

import com.foreman.entities.Workspace;

public interface WorkspaceRepo extends JpaRepository<Workspace, Long> {
}
