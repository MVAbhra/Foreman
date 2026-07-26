package com.foreman.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.foreman.entities.User;

public class CustomUserDetails implements UserDetails {

	private static final long serialVersionUID = 478366365509111105L;
	
	private User user;
	
	public CustomUserDetails(User user) {
		
		this.user = user;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {

		return List.of();
	}
	
	
	public User getUser() {
		
		return user;
	}

	@Override
	public String getPassword() {
		
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		
		return user.getEmail();
	}

}
