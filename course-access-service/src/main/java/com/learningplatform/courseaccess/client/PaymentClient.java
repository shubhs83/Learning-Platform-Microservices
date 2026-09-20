package com.learningplatform.courseaccess.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "PAYMENT-SERVICE", fallback = PaymentClientFallback.class)
public interface PaymentClient {

	@GetMapping("/api/payments/user/{email}")
	List<PaymentDto> getPaymentsByUser(@PathVariable("email") String email);
}