package com.learningplatform.courseaccess.controller;

import com.learningplatform.courseaccess.dto.EnrollRequest;
import com.learningplatform.courseaccess.entity.Enrollment;
import com.learningplatform.courseaccess.service.CourseAccessService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

import java.util.List;

@RestController
@RequestMapping("/api/access")
public class CourseAccessController {

	private final CourseAccessService courseAccessService;

	public CourseAccessController(CourseAccessService courseAccessService) {
		this.courseAccessService = courseAccessService;
	}

	@PostMapping("/enroll")
	public Enrollment enroll(@Valid @RequestBody EnrollRequest request) {
		return courseAccessService.enroll(request);
	}

	@GetMapping("/my-courses/{email}")
	public List<Enrollment> getMyCourses(@PathVariable String email) {
		return courseAccessService.getMyCourses(email);
	}

	@GetMapping("/check/{email}/{courseId}")
	public boolean hasAccess(@PathVariable String email, @PathVariable Long courseId) {
		return courseAccessService.hasAccess(email, courseId);
	}

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<String> handleException(RuntimeException e) {
		return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(e.getMessage());
	}

	@GetMapping("/reports/my-courses/{email}")
	public Map<String, Object> myCoursesReport(@PathVariable String email) {
		List<Enrollment> enrollments = courseAccessService.getMyCourses(email);
		Map<String, Object> report = new HashMap<>();
		report.put("learnerEmail", email);
		report.put("totalCoursesEnrolled", enrollments.size());
		report.put("enrollments", enrollments);
		return report;
	}
}