package com.learningplatform.admin.client;

public class PaymentSummaryDto {
	private Double amount;
	private String status;

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}