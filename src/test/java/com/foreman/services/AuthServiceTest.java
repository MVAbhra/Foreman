package com.foreman.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.foreman.dtos.UserRegisterRequestDto;
import com.foreman.entities.User;
import com.foreman.exception.DuplicateResourceException;
import com.foreman.repos.UserRepo;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepo userRepo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    
    @Test
    void register_ShouldSaveNewUser() {

    	//arrange
        UserRegisterRequestDto dto =
                new UserRegisterRequestDto(
                        "John",
                        "Doe",
                        "john@gmail.com",
                        "password123"
                );
        
        when(userRepo.existsByEmail(dto.getEmail()))
        .thenReturn(false);
        
        when(passwordEncoder.encode(dto.getPassword()))
        .thenReturn("encryptedPassword");
        
        //act
        authService.register(dto);
        
        //assert
       ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
       
       verify(userRepo).save(captor.capture());
       
       User savedUser = captor.getValue();
       
       assertEquals("John", savedUser.getFirstName());
       assertEquals("Doe", savedUser.getLastName());
       assertEquals("john@gmail.com", savedUser.getEmail());
       assertEquals("encryptedPassword", savedUser.getPassword());
    }
    
    
    @Test
    void register_ShouldThrowException_WhenEmailAlreadyExists() {

        UserRegisterRequestDto dto =
                new UserRegisterRequestDto(
                        "John",
                        "Doe",
                        "john@gmail.com",
                        "password123");

        when(userRepo.existsByEmail(dto.getEmail()))
                .thenReturn(true);

        assertThrows(
                DuplicateResourceException.class,
                () -> authService.register(dto));

        verify(userRepo, never()).save(any(User.class));
    }
}