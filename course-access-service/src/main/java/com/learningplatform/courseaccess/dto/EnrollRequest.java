package com.learningplatform.courseaccess.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class EnrollRequest {

	@NotBlank
	@Email
	private String learnerEmail;

	@NotNull
	private Long courseId;

	public String getLearnerEmail() {
		return learnerEmail;
	}

	public void setLearnerEmail(String learnerEmail) {
		this.learnerEmail = learnerEmail;
	}

	public Long getCourseId() {
		return courseId;
	}

	public void setCourseId(Long courseId) {
		this.courseId = courseId;
	}
}