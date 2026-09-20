package com.learningplatform.registrationlogin.service;

import com.learningplatform.registrationlogin.dto.*;
import com.learningplatform.registrationlogin.entity.Role;
import com.learningplatform.registrationlogin.entity.User;
import com.learningplatform.registrationlogin.repository.UserRepository;
import com.learningplatform.registrationlogin.util.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;

	public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtUtil = jwtUtil;
	}

	public AuthResponse signup(SignupRequest request) {
		if (userRepository.existsByEmail(request.getEmail())) {
			return new AuthResponse(null, "Email already registered");
		}

		User user = new User();
		user.setFullName(request.getFullName());
		user.setEmail(request.getEmail());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.setRoles(request.getRoles() != null ? request.getRoles() : Set.of(Role.USER));
		user.setBlocked(false);

		userRepository.save(user);

		String token = jwtUtil.generateToken(user.getEmail());
		return new AuthResponse(token, "Signup successful");
	}

	public AuthResponse login(LoginRequest request) {
		User user = userRepository.findByEmail(request.getEmail()).orElse(null);

		if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
			return new AuthResponse(null, "Invalid email or password");
		}

		if (user.isBlocked()) {
			return new AuthResponse(null, "Account is blocked");
		}

		String token = jwtUtil.generateToken(user.getEmail());
		return new AuthResponse(token, "Login successful");
	}

	public String setBlockedStatus(String email, boolean blocked) {
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new RuntimeException("User not found: " + email));
		user.setBlocked(blocked);
		userRepository.save(user);
		return blocked ? "User blocked" : "User unblocked";
	}
}