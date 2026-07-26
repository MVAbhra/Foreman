package com.foreman.repos;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.foreman.dtos.UserDisplayResponseDto;
import com.foreman.entities.WorkspaceMembership;
import com.foreman.enums.WorkspaceRole;

@Repository
public interface WorkspaceMembershipRepo extends JpaRepository<WorkspaceMembership, Long> {

	void deleteByWorkspace_Id(Long id);

	void deleteByUser_Id(Long id);

	boolean existsByWorkspace_IdAndUser_Id(Long id, Long id2);

	
	@Query("""
			SELECT new com.foreman.dtos.UserDisplayResponseDto(
				wm.user.id, wm.user.firstName, wm.user.lastName, wm.user.email, 
				wm.workspace.id, wm.workspaceRole, 
				null, null
			)
			FROM WorkspaceMembership wm
			WHERE wm.workspace.id = :wrkspcId
			""")
	List<UserDisplayResponseDto> getWorkspaceMembers(Long wrkspcId);

	
	@Query("""
			SELECT new com.foreman.dtos.UserDisplayResponseDto(
				wm.user.id, wm.user.firstName, wm.user.lastName, wm.user.email, 
				wm.workspace.id, wm.workspaceRole, 
				null, null
			)
			FROM WorkspaceMembership wm
			WHERE wm.workspace.id = :wrkspcId
			AND wm.user.id = :memId
			""")
	Optional<UserDisplayResponseDto> getOneWorkspaceMember(Long wrkspcId, Long memId);

	Optional<WorkspaceMembership> findByWorkspace_IdAndUser_Id(Long wrkspcId, Long userId);

	boolean existsByWorkspace_IdAndUser_IdAndWorkspaceRole(Long wrkspcId, Long id, WorkspaceRole owner);
}
