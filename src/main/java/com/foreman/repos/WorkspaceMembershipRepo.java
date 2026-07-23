package com.foreman.repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.foreman.dtos.UserDisplayResponseDto;
import com.foreman.entities.WorkspaceMembership;

public interface WorkspaceMembershipRepo extends JpaRepository<WorkspaceMembership, Long> {

	void deleteByWorkspace_Id(Long id);

	void deleteByUser_Id(Long id);

	boolean existsByWorkspace_IdAndUser_Id(Long id, Long id2);

	@Query("""
			select new com.foreman.dtos.UserDisplayResponseDto(u.id, u.firstName, u.lastName, u.email, :workspaceId, wm.workspaceRole, null, null)
			from
			WorkspaceMembership wm
			JOIN
			wm.user u
			WHERE
			wm.workspace.id = :workspaceId
	""")
	List<UserDisplayResponseDto> findWorkspaceMembers(Long workspaceId);
}
