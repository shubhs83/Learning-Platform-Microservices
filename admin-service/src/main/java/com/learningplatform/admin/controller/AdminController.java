package com.learningplatform.admin.controller;

import com.learningplatform.admin.dto.DiscountRequest;
import com.learningplatform.admin.entity.Discount;
import com.learningplatform.admin.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

	private final AdminService adminService;

	public AdminController(AdminService adminService) {
		this.adminService = adminService;
	}

	@PostMapping("/discounts")
	public Discount createDiscount(@Valid @RequestBody DiscountRequest request) {
		return adminService.createDiscount(request);
	}

	@GetMapping("/discounts/{courseId}")
	public List<Discount> getDiscounts(@PathVariable Long courseId) {
		return adminService.getActiveDiscounts(courseId);
	}

	@PutMapping("/users/{email}/block")
	public String blockUser(@PathVariable String email) {
		return adminService.blockUser(email);
	}

	@PutMapping("/users/{email}/unblock")
	public String unblockUser(@PathVariable String email) {
		return adminService.unblockUser(email);
	}

	@GetMapping("/reports/platform")
	public Map<String, Object> platformReport() {
		return adminService.getPlatformReport();
	}
}