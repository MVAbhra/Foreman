package com.foreman.repos;

import org.springframework.data.jpa.repository.JpaRepository;

import com.foreman.entities.WorkspaceMembership;

public interface WorkspaceMembershipRepo extends JpaRepository<WorkspaceMembership, Long> {

	void deleteByWorkspace_Id(Long id);

	void deleteByUser_Id(Long id);

	boolean existsByWorkspace_IdAndUser_Id(Long id, Long id2);

}
