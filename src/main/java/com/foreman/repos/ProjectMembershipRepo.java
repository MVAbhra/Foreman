package com.foreman.repos;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.foreman.dtos.UserDisplayResponseDto;
import com.foreman.entities.ProjectMembership;
import com.foreman.enums.ProjectRole;

@Repository
public interface ProjectMembershipRepo extends JpaRepository<ProjectMembership, Long> {

	ProjectMembership findByProject_IdAndProjectRole(Long id, ProjectRole role);

	List<ProjectMembership> findByProject_Id(Long projId);

	
	
	@Query("""
			SELECT new com.foreman.dtos.UserDisplayResponseDto(
				
				pm.user.id, pm.user.firstName, pm.user.lastName, pm.user.email, 
				wm.workspace.id, wm.workspaceRole,
				pm.project.id, pm.projectRole
			)
			FROM ProjectMembership pm
			JOIN WorkspaceMembership wm
			ON pm.user.id = wm.user.id
			WHERE pm.project.id = :projId
			AND wm.workspace.id = :wrkspcId
			""")
	List<UserDisplayResponseDto> getAllProjectMembers(Long wrkspcId, Long projId);
	
	
	@Query("""
			SELECT new com.foreman.dtos.UserDisplayResponseDto(
				
				pm.user.id, pm.user.firstName, pm.user.lastName, pm.user.email, 
				wm.workspace.id, wm.workspaceRole,
				pm.project.id, pm.projectRole
			)
			FROM ProjectMembership pm
			JOIN WorkspaceMembership wm
			ON pm.user.id = wm.user.id
			WHERE pm.user.id = :memId 
			AND pm.project.id = :projId
			AND wm.workspace.id = :wrkspcId
			""")
	UserDisplayResponseDto getOneProjectMember(Long wrkspcId, Long projId, Long memId);

	Optional<ProjectMembership> findByProject_IdAndUser_Id(Long projId, Long memId);

	boolean existsByProject_IdAndProjectRole(Long projId, ProjectRole projectManager);

	boolean existsByProject_IdAndUser_Id(Long projId, Long id);
}
