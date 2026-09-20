package com.learningplatform.payment.controller;

import com.learningplatform.payment.dto.PaymentRequest;
import com.learningplatform.payment.entity.Payment;
import com.learningplatform.payment.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

	private final PaymentService paymentService;

	public PaymentController(PaymentService paymentService) {
		this.paymentService = paymentService;
	}

	@PostMapping
	public Payment makePayment(@Valid @RequestBody PaymentRequest request) {
		return paymentService.processPayment(request);
	}

	@GetMapping("/user/{email}")
	public List<Payment> getUserPayments(@PathVariable String email) {
		return paymentService.getPaymentsByUser(email);
	}

	@GetMapping("/all")
	public List<Payment> getAllPayments() {
		return paymentService.getAllPayments();
	}
}