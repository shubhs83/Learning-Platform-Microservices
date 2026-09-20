package com.learningplatform.admin.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.learningplatform.admin.entity.Discount;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, Long> {
	List<Discount> findByCourseIdAndActiveTrue(Long courseId);
}