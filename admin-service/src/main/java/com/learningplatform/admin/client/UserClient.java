package com.learningplatform.admin.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(name = "REGISTRATION-LOGIN-SERVICE")
public interface UserClient {

	@PutMapping("/api/auth/admin/block/{email}")
	String blockUser(@PathVariable("email") String email);

	@PutMapping("/api/auth/admin/unblock/{email}")
	String unblockUser(@PathVariable("email") String email);
}