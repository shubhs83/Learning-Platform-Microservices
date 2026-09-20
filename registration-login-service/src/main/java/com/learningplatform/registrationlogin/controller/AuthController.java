package com.learningplatform.registrationlogin.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.learningplatform.registrationlogin.dto.AuthResponse;
import com.learningplatform.registrationlogin.dto.LoginRequest;
import com.learningplatform.registrationlogin.dto.SignupRequest;
import com.learningplatform.registrationlogin.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@PostMapping("/signup")
	public AuthResponse signup(@Valid @RequestBody SignupRequest request) {
		return authService.signup(request);
	}

	@PostMapping("/login")
	public AuthResponse login(@Valid @RequestBody LoginRequest request) {
		return authService.login(request);
	}

	@PutMapping("/admin/block/{email}")
	public String blockUser(@PathVariable String email) {
		return authService.setBlockedStatus(email, true);
	}

	@PutMapping("/admin/unblock/{email}")
	public String unblockUser(@PathVariable String email) {
		return authService.setBlockedStatus(email, false);
	}
}