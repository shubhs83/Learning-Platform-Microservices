package com.learningplatform.admin.service;

import com.learningplatform.admin.client.PaymentReportClient;
import com.learningplatform.admin.client.PaymentSummaryDto;
import com.learningplatform.admin.client.UserClient;
import com.learningplatform.admin.dto.DiscountRequest;
import com.learningplatform.admin.entity.Discount;
import com.learningplatform.admin.repository.DiscountRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminService {

    private final DiscountRepository discountRepository;
    private final UserClient userClient;
    private final PaymentReportClient paymentReportClient;

    public AdminService(DiscountRepository discountRepository, UserClient userClient, PaymentReportClient paymentReportClient) {
        this.discountRepository = discountRepository;
        this.userClient = userClient;
        this.paymentReportClient = paymentReportClient;
    }

    public Discount createDiscount(DiscountRequest request) {
        Discount discount = new Discount();
        discount.setCourseId(request.getCourseId());
        discount.setDiscountPercentage(request.getDiscountPercentage());
        discount.setActive(true);
        return discountRepository.save(discount);
    }

    public List<Discount> getActiveDiscounts(Long courseId) {
        return discountRepository.findByCourseIdAndActiveTrue(courseId);
    }

    public String blockUser(String email) {
        return userClient.blockUser(email);
    }

    public String unblockUser(String email) {
        return userClient.unblockUser(email);
    }

    public Map<String, Object> getPlatformReport() {
        List<PaymentSummaryDto> payments = paymentReportClient.getAllPayments();

        double totalRevenue = payments.stream()
                .filter(p -> "SUCCESS".equals(p.getStatus()))
                .mapToDouble(PaymentSummaryDto::getAmount)
                .sum();

        long successfulPayments = payments.stream()
                .filter(p -> "SUCCESS".equals(p.getStatus()))
                .count();

        Map<String, Object> report = new HashMap<>();
        report.put("totalPayments", payments.size());
        report.put("successfulPayments", successfulPayments);
        report.put("totalRevenue", totalRevenue);
        return report;
    }
}