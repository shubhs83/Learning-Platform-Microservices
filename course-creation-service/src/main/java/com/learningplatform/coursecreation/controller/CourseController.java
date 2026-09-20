package com.learningplatform.coursecreation.controller;

import com.learningplatform.coursecreation.dto.CourseRequest;
import java.util.HashMap;
import java.util.Map;
import com.learningplatform.coursecreation.entity.CourseStatus;
import com.learningplatform.coursecreation.dto.ModuleRequest;
import com.learningplatform.coursecreation.entity.Course;
import com.learningplatform.coursecreation.entity.Module;
import com.learningplatform.coursecreation.service.CourseService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

	private final CourseService courseService;

	public CourseController(CourseService courseService) {
		this.courseService = courseService;
	}

	@PostMapping
	public Course createCourse(@Valid @RequestBody CourseRequest request) {
		return courseService.createCourse(request);
	}

	@PostMapping("/{courseId}/modules")
	public Module addModule(@PathVariable Long courseId, @Valid @RequestBody ModuleRequest request) {
		return courseService.addModule(courseId, request);
	}

	@PutMapping("/{courseId}/publish")
	public Course publishCourse(@PathVariable Long courseId) {
		return courseService.publishCourse(courseId);
	}

	@GetMapping("/creator/{email}")
	public List<Course> getCoursesByCreator(@PathVariable String email) {
		return courseService.getCoursesByCreator(email);
	}

	@GetMapping("/{courseId}")
	public Course getCourse(@PathVariable Long courseId) {
		return courseService.getCourseById(courseId);
	}

	@GetMapping
	public List<Course> getAllPublishedCourses() {
		return courseService.getAllPublishedCourses();
	}

	@GetMapping("/reports/creator/{email}")
	public Map<String, Object> creatorReport(@PathVariable String email) {
		List<Course> courses = courseService.getCoursesByCreator(email);
		long published = courses.stream().filter(c -> c.getStatus() == CourseStatus.PUBLISHED).count();
		long draft = courses.stream().filter(c -> c.getStatus() == CourseStatus.DRAFT).count();

		Map<String, Object> report = new HashMap<>();
		report.put("creatorEmail", email);
		report.put("totalCourses", courses.size());
		report.put("publishedCourses", published);
		report.put("draftCourses", draft);
		report.put("courses", courses);
		return report;
	}
}