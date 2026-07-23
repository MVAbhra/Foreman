package com.foreman.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.foreman.entities.Workspace;

@Repository
public interface WorkspaceRepo extends JpaRepository<Workspace, Long> {
}
