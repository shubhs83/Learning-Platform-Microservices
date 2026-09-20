package com.learningplatform.coursecreation.service;

import com.learningplatform.coursecreation.dto.CourseRequest;
import com.learningplatform.coursecreation.dto.ModuleRequest;
import com.learningplatform.coursecreation.entity.Course;
import com.learningplatform.coursecreation.entity.CourseStatus;
import com.learningplatform.coursecreation.entity.Module;
import com.learningplatform.coursecreation.repository.CourseRepository;
import com.learningplatform.coursecreation.repository.ModuleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseService {

	private final CourseRepository courseRepository;
	private final ModuleRepository moduleRepository;

	public CourseService(CourseRepository courseRepository, ModuleRepository moduleRepository) {
		this.courseRepository = courseRepository;
		this.moduleRepository = moduleRepository;
	}

	public Course createCourse(CourseRequest request) {
		Course course = new Course();
		course.setTitle(request.getTitle());
		course.setDescription(request.getDescription());
		course.setCreatorEmail(request.getCreatorEmail());
		course.setPrice(request.getPrice());
		course.setStatus(CourseStatus.DRAFT);
		return courseRepository.save(course);
	}

	public Module addModule(Long courseId, ModuleRequest request) {
		Course course = courseRepository.findById(courseId)
				.orElseThrow(() -> new RuntimeException("Course not found: " + courseId));

		Module module = new Module();
		module.setTitle(request.getTitle());
		module.setContentType(request.getContentType());
		module.setContentUrl(request.getContentUrl());
		module.setSequenceOrder(request.getSequenceOrder());
		module.setCourse(course);

		return moduleRepository.save(module);
	}

	public Course publishCourse(Long courseId) {
		Course course = courseRepository.findById(courseId)
				.orElseThrow(() -> new RuntimeException("Course not found: " + courseId));
		course.setStatus(CourseStatus.PUBLISHED);
		return courseRepository.save(course);
	}

	public List<Course> getCoursesByCreator(String creatorEmail) {
		return courseRepository.findByCreatorEmail(creatorEmail);
	}

	public Course getCourseById(Long courseId) {
		return courseRepository.findById(courseId)
				.orElseThrow(() -> new RuntimeException("Course not found: " + courseId));
	}

	public List<Course> getAllPublishedCourses() {
		return courseRepository.findAll().stream().filter(c -> c.getStatus() == CourseStatus.PUBLISHED).toList();
	}
}