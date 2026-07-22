package com.foreman.repos;

import org.springframework.data.jpa.repository.JpaRepository;

import com.foreman.entities.ProjectMembership;

public interface ProjectMembershipRepo extends JpaRepository<ProjectMembership, Long> {

}
