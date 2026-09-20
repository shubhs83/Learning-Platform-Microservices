package com.learningplatform.courseaccess.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.learningplatform.courseaccess.entity.Enrollment;


@Repository

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
	List<Enrollment> findByLearnerEmail(String learnerEmail);

	Optional<Enrollment> findByLearnerEmailAndCourseId(String learnerEmail, Long courseId);
}