package com.learningplatform.apigateway.filter;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class JwtAuthFilter implements GlobalFilter, Ordered {

	@Value("${jwt.secret}")
	private String secretString;

	private static final List<String> PUBLIC_PATHS = List.of("/registration-login-service/api/auth/signup",
			"/registration-login-service/api/auth/login");

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		String path = exchange.getRequest().getURI().getPath();

		if (PUBLIC_PATHS.contains(path)) {
			return chain.filter(exchange);
		}

		String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");

		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			System.out.println("JWT FILTER: Missing or malformed Authorization header: " + authHeader);
			return unauthorized(exchange);
		}

		String token = authHeader.substring(7);

		try {
			System.out.println("GATEWAY SECRET: [" + secretString + "]");
			SecretKey key = Keys.hmacShaKeyFor(secretString.getBytes(StandardCharsets.UTF_8));
			String email = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getSubject();

			System.out.println("JWT FILTER: Token valid, user = " + email);

			ServerHttpRequest mutatedRequest = exchange.getRequest().mutate().header("X-User-Email", email).build();

			return chain.filter(exchange.mutate().request(mutatedRequest).build());

		} catch (Exception e) {
			System.out
					.println("JWT FILTER: Validation failed - " + e.getClass().getSimpleName() + ": " + e.getMessage());
			e.printStackTrace();
			return unauthorized(exchange);
		}
	}

	private Mono<Void> unauthorized(ServerWebExchange exchange) {
		exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
		return exchange.getResponse().setComplete();
	}

	@Override
	public int getOrder() {
		return -1;
	}
}