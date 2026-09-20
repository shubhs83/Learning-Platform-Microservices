package com.learningplatform.payment.service;

import com.learningplatform.payment.dto.PaymentRequest;
import com.learningplatform.payment.entity.Payment;
import com.learningplatform.payment.entity.PaymentStatus;
import com.learningplatform.payment.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PaymentService {

	private final PaymentRepository paymentRepository;

	public PaymentService(PaymentRepository paymentRepository) {
		this.paymentRepository = paymentRepository;
	}

	public Payment processPayment(PaymentRequest request) {
		Payment payment = new Payment();
		payment.setUserEmail(request.getUserEmail());
		payment.setCourseId(request.getCourseId());
		payment.setAmount(request.getAmount());
		payment.setStatus(PaymentStatus.SUCCESS);
		payment.setPaymentDate(LocalDateTime.now());
		payment.setTransactionId(UUID.randomUUID().toString());

		return paymentRepository.save(payment);
	}

	public List<Payment> getPaymentsByUser(String userEmail) {
		return paymentRepository.findByUserEmail(userEmail);
	}

	public List<Payment> getAllPayments() {
		return paymentRepository.findAll();
	}
}