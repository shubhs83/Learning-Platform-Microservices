package com.learningplatform.coursecreation.repository;

import com.learningplatform.coursecreation.entity.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ModuleRepository extends JpaRepository<Module, Long> {
	List<Module> findByCourseIdOrderBySequenceOrderAsc(Long courseId);
}