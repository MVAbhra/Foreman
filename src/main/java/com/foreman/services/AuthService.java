package com.foreman.services;

import java.time.LocalDate;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.foreman.entities.User;
import com.foreman.dtos.UserLoginRequestDto;
import com.foreman.dtos.UserLoginResponseDto;
import com.foreman.dtos.UserRegisterRequestDto;
import com.foreman.exception.DuplicateResourceException;
import com.foreman.repos.UserRepo;
import com.foreman.security.CustomUserDetails;

@Service
@Transactional
@Validated
public class AuthService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;

	AuthService(UserService userService, 
			UserRepo userRepo,
			PasswordEncoder passwordEncoder,
			AuthenticationManager authenticationManager,
			JwtService jwtService) {
		
		this.userRepo = userRepo;
		this.passwordEncoder = passwordEncoder;
		this.authenticationManager = authenticationManager;
		this.jwtService = jwtService;
	}

	public void register(UserRegisterRequestDto dto) {
		
		//moved the following actions from UserService into here
		//commented out UserService.addOneUser()
		if(userRepo.existsByEmail(dto.getEmail())){
			
			throw new DuplicateResourceException("User with email "+dto.getEmail()+" already exists!");
		}
		
		dto.setPassword(passwordEncoder.encode(dto.getPassword()));
		
		User user = new User(
			 dto.getFirstName(), 
			 dto.getLastName(), 
			 dto.getEmail(), 
			 dto.getPassword());
		 
		user.setCreatedOn(LocalDate.now());
		
		//saving the user
		userRepo.save(user);
	}

	public UserLoginResponseDto login(UserLoginRequestDto dto) {
		
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword()));
		
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		
		String token = jwtService.generateToken(userDetails);
		
		System.out.println("Token: "+ token);

		return new UserLoginResponseDto(token);
	}
}
