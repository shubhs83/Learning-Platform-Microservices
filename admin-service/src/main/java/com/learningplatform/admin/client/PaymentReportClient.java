package com.learningplatform.admin.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "PAYMENT-SERVICE")
public interface PaymentReportClient {

    @GetMapping("/api/payments/all")
    List<PaymentSummaryDto> getAllPayments();
}