package com.learningplatform.coursecreation.repository;

import com.learningplatform.coursecreation.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {
	List<Course> findByCreatorEmail(String creatorEmail);
}