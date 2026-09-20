package com.learningplatform.courseaccess.service;

import com.learningplatform.courseaccess.client.PaymentClient;
import com.learningplatform.courseaccess.client.PaymentDto;
import com.learningplatform.courseaccess.dto.EnrollRequest;
import com.learningplatform.courseaccess.entity.Enrollment;
import com.learningplatform.courseaccess.repository.EnrollmentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CourseAccessService {

	private final EnrollmentRepository enrollmentRepository;
	private final PaymentClient paymentClient;

	public CourseAccessService(EnrollmentRepository enrollmentRepository, PaymentClient paymentClient) {
		this.enrollmentRepository = enrollmentRepository;
		this.paymentClient = paymentClient;
	}

	public Enrollment enroll(EnrollRequest request) {
		List<PaymentDto> payments;
		try {
			payments = paymentClient.getPaymentsByUser(request.getLearnerEmail());
		} catch (Exception e) {
			throw new RuntimeException("Payment service is currently unavailable. Please try again later.");
		}

		PaymentDto matchingPayment = payments.stream().filter(p -> p.getCourseId().equals(request.getCourseId()))
				.filter(p -> "SUCCESS".equals(p.getStatus())).findFirst().orElseThrow(() -> new RuntimeException(
						"No successful payment found for this course. Cannot grant access."));

		Enrollment enrollment = new Enrollment();
		enrollment.setLearnerEmail(request.getLearnerEmail());
		enrollment.setCourseId(request.getCourseId());
		enrollment.setEnrolledAt(LocalDateTime.now());
		enrollment.setTransactionId(matchingPayment.getTransactionId());

		return enrollmentRepository.save(enrollment);
	}

	public List<Enrollment> getMyCourses(String learnerEmail) {
		return enrollmentRepository.findByLearnerEmail(learnerEmail);
	}

	public boolean hasAccess(String learnerEmail, Long courseId) {
		return enrollmentRepository.findByLearnerEmailAndCourseId(learnerEmail, courseId).isPresent();
	}
}