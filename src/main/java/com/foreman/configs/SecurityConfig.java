package com.foreman.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.foreman.security.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration //telling spring that this class contains beans
@EnableWebSecurity //telling spring to use this configuration for security instead of default
@RequiredArgsConstructor
public class SecurityConfig {
	
	private final JwtAuthenticationFilter jwtAuthenticationFilter;
	
	@Bean
	PasswordEncoder passwordEncoder() {
		
		return new BCryptPasswordEncoder();
	}
	
	
	@Bean
	AuthenticationManager authenticationManager(
	        AuthenticationConfiguration config)
	        throws Exception {

	    return config.getAuthenticationManager();
	}
	
	
	@Bean //telling that this method is a bean
	//method to build a filter chain
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		
		//modifying layer actions in the chain
		http
		.csrf(csrf -> csrf.disable())
		.sessionManagement(session ->
        	session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
		.authorizeHttpRequests(auth -> 
			auth.requestMatchers("/api/auth/**", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
			.anyRequest().authenticated()
		)
		.addFilterBefore(jwtAuthenticationFilter,
		        UsernamePasswordAuthenticationFilter.class);
		
		//build the chain using the modifications and return
		return http.build();
	}
	
	
//	@Bean
//	UserDetailsService userDetailsService(CustomUserDetailsService service) {
//	    return service;
//	}
	
	
//	@Bean
//	AuthenticationProvider authenticationProvider(
//	        CustomUserDetailsService userDetailsService,
//	        PasswordEncoder passwordEncoder) {
//
//	    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
//
//	    provider.setUserDetailsService(userDetailsService);
//	    provider.setPasswordEncoder(passwordEncoder);
//
//	    return provider;
//	}
}
