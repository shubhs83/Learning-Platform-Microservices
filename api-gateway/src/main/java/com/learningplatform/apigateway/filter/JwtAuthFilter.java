//package com.learningplatform.apigateway.filter;
//
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.security.Keys;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.cloud.gateway.filter.GatewayFilterChain;
//import org.springframework.cloud.gateway.filter.GlobalFilter;
//import org.springframework.core.Ordered;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.server.reactive.ServerHttpRequest;
//import org.springframework.stereotype.Component;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Mono;
//
//import javax.crypto.SecretKey;
//import java.nio.charset.StandardCharsets;
//import java.util.List;
//
//@Component
//public class JwtAuthFilter implements GlobalFilter, Ordered {
//
//	@Value("${jwt.secret}")
//	private String secretString;
//
//	private static final List<String> PUBLIC_PATHS = List.of("/registration-login-service/api/auth/signup",
//			"/registration-login-service/api/auth/login");
//
//	@Override
//	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//		String path = exchange.getRequest().getURI().getPath();
//
//		if (PUBLIC_PATHS.contains(path)) {
//			return chain.filter(exchange);
//		}
//
//		String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
//
//		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//			System.out.println("JWT FILTER: Missing or malformed Authorization header: " + authHeader);
//			return unauthorized(exchange);
//		}
//
//		String token = authHeader.substring(7);
//
//		try {
//			System.out.println("GATEWAY SECRET: [" + secretString + "]");
//			SecretKey key = Keys.hmacShaKeyFor(secretString.getBytes(StandardCharsets.UTF_8));
//			String email = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getSubject();
//
//			System.out.println("JWT FILTER: Token valid, user = " + email);
//
//			ServerHttpRequest mutatedRequest = exchange.getRequest().mutate().header("X-User-Email", email).build();
//
//			return chain.filter(exchange.mutate().request(mutatedRequest).build());
//
//		} catch (Exception e) {
//			System.out
//					.println("JWT FILTER: Validation failed - " + e.getClass().getSimpleName() + ": " + e.getMessage());
//			e.printStackTrace();
//			return unauthorized(exchange);
//		}
//	}
//
//	private Mono<Void> unauthorized(ServerWebExchange exchange) {
//		exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
//		return exchange.getResponse().setComplete();
//	}
//
//	@Override
//	public int getOrder() {
//		return -1;
//	}
//}

package com.learningplatform.apigateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;

@Component
public class JwtAuthFilter implements GlobalFilter, Ordered {

	@Value("${jwt.secret}")
	private String secretString;

	private static final List<String> PUBLIC_PATHS = List.of("/registration-login-service/api/auth/signup",
			"/registration-login-service/api/auth/login");

	// Routes that require more than "logged in" - they require a specific role.
	// method == null means "any method". Matching is prefix-based on the raw
	// incoming gateway path (before StripPrefix runs).
	private static final List<RoleRule> ROLE_RULES = List.of(
			// Only admins may block/unblock accounts or manage discounts/reports.
			new RoleRule(null, "/admin-service/", Set.of("SITE_ADMIN")),
			new RoleRule(HttpMethod.PUT, "/registration-login-service/api/auth/admin/", Set.of("SITE_ADMIN")),
			// Only content creators may create courses, add modules, or publish.
			new RoleRule(HttpMethod.POST, "/course-creation-service/api/courses", Set.of("CONTENT_CREATOR")),
			new RoleRule(HttpMethod.PUT, "/course-creation-service/api/courses", Set.of("CONTENT_CREATOR")),
			// Only learners enroll or pay for courses.
			new RoleRule(HttpMethod.POST, "/course-access-service/api/access/enroll", Set.of("LEARNER")),
			new RoleRule(HttpMethod.POST, "/payment-service/api/payments", Set.of("LEARNER")),
			// The full payments ledger is admin-only.
			new RoleRule(HttpMethod.GET, "/payment-service/api/payments/all", Set.of("SITE_ADMIN")));

	private record RoleRule(HttpMethod method, String pathPrefix, Set<String> requiredRoles) {
		boolean matches(HttpMethod requestMethod, String path) {
			return (method == null || method.equals(requestMethod)) && path.startsWith(pathPrefix);
		}
	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		String path = exchange.getRequest().getURI().getPath();
		HttpMethod method = exchange.getRequest().getMethod();

		if (PUBLIC_PATHS.contains(path)) {
			return chain.filter(exchange);
		}

		String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");

		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			return unauthorized(exchange);
		}

		String token = authHeader.substring(7);

		try {
			SecretKey key = Keys.hmacShaKeyFor(secretString.getBytes(StandardCharsets.UTF_8));
			Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
			String email = claims.getSubject();
			@SuppressWarnings("unchecked")
			List<String> roles = claims.get("roles", List.class);
			Set<String> roleSet = roles == null ? Set.of() : Set.copyOf(roles);

			for (RoleRule rule : ROLE_RULES) {
				if (rule.matches(method, path) && rule.requiredRoles().stream().noneMatch(roleSet::contains)) {
					return forbidden(exchange);
				}
			}

			ServerHttpRequest mutatedRequest = exchange.getRequest().mutate().header("X-User-Email", email)
					.header("X-User-Roles", String.join(",", roleSet)).build();

			return chain.filter(exchange.mutate().request(mutatedRequest).build());

		} catch (Exception e) {
			return unauthorized(exchange);
		}
	}

	private Mono<Void> unauthorized(ServerWebExchange exchange) {
		exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
		return exchange.getResponse().setComplete();
	}

	private Mono<Void> forbidden(ServerWebExchange exchange) {
		exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
		return exchange.getResponse().setComplete();
	}

	@Override
	public int getOrder() {
		return -1;
	}
}