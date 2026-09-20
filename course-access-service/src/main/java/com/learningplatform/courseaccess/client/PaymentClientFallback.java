package com.learningplatform.courseaccess.client;

import org.springframework.stereotype.Component;
import java.util.Collections;
import java.util.List;

@Component
public class PaymentClientFallback implements PaymentClient {

	@Override
	public List<PaymentDto> getPaymentsByUser(String email) {
		return Collections.emptyList();
	}
}