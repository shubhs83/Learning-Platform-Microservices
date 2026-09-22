package com.learningplatform.registrationlogin.dto;

import java.util.Set;

public class UserResponse {
	private String email;
	private String fullName;
	private Set<String> roles;

	public UserResponse(String email, String fullName, Set<String> roles) {
		this.email = email;
		this.fullName = fullName;
		this.roles = roles;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public Set<String> getRoles() {
		return roles;
	}

	public void setRoles(Set<String> roles) {
		this.roles = roles;
	}
}