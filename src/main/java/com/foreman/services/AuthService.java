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

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@Validated
@RequiredArgsConstructor
public class AuthService {

    private final UserRepo uRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;

	public void register(UserRegisterRequestDto dto) {
		
		//moved the following actions from UserService into here
		//commented out UserService.addOneUser()
		if(uRepo.existsByEmail(dto.getEmail())){
			
			throw new DuplicateResourceException("User with email "+dto.getEmail()+" already exists!");
		}
		
		User user = new User(
			 dto.getFirstName(), 
			 dto.getLastName(), 
			 dto.getEmail(), 
			 passwordEncoder.encode(dto.getPassword()));
		 
		user.setCreatedOn(LocalDate.now());
		
		//saving the user
		uRepo.save(user);
	}

	public UserLoginResponseDto login(UserLoginRequestDto dto) {
		
		//authenticate() calls Spring to authenticate the user details
		//Spring calls CustomUserDetailsService.loadUserByUsername() which returns a user
		//wrapped within CustomeUserDetails object, because Spring only understands that, not User
		//Spring does CustomUserDetails.getPassword() and calls BCryptPasswordEncoder.matches()
		//this compares dto.getPassword() and customUserDetails.getPassword()
		//if successful authentication object is returned or else Status 401
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword()));
		
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		
		String token = jwtService.generateToken(userDetails);
		
		System.out.println("Token: "+ token);

		return new UserLoginResponseDto(token);
	}
}
