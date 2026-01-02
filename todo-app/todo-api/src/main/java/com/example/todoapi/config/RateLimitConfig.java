package com.example.todoapi.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
@EnableScheduling
public class RateLimitConfig {

    private static final int MAX_BUCKETS = 10000;
    private final Map<String, BucketEntry> buckets = new ConcurrentHashMap<>();

    private record BucketEntry(Bucket bucket, Instant lastAccess) {}

    @Bean
    public OncePerRequestFilter rateLimitFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request,
                                          HttpServletResponse response,
                                          FilterChain filterChain) throws ServletException, IOException {

                if (!request.getRequestURI().startsWith("/api/")) {
                    filterChain.doFilter(request, response);
                    return;
                }

                String clientIp = getClientIp(request);
                Bucket bucket = getOrCreateBucket(clientIp);

                if (bucket.tryConsume(1)) {
                    filterChain.doFilter(request, response);
                } else {
                    response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\": \"Too many requests. Please try again later.\"}");
                }
            }

            private String getClientIp(HttpServletRequest request) {
                String xForwardedFor = request.getHeader("X-Forwarded-For");
                if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
                    return xForwardedFor.split(",")[0].trim();
                }
                return request.getRemoteAddr();
            }
        };
    }

    private Bucket getOrCreateBucket(String clientIp) {
        BucketEntry entry = buckets.compute(clientIp, (key, existing) -> {
            if (existing != null) {
                return new BucketEntry(existing.bucket(), Instant.now());
            }
            return new BucketEntry(createBucket(), Instant.now());
        });
        return entry.bucket();
    }

    private Bucket createBucket() {
        Bandwidth limit = Bandwidth.builder()
                .capacity(100)
                .refillGreedy(100, Duration.ofMinutes(1))
                .build();
        return Bucket.builder().addLimit(limit).build();
    }

    @Scheduled(fixedRate = 300000) // Every 5 minutes
    public void cleanupStaleBuckets() {
        if (buckets.size() <= MAX_BUCKETS) {
            return;
        }

        Instant cutoff = Instant.now().minus(Duration.ofMinutes(5));
        buckets.entrySet().removeIf(entry -> entry.getValue().lastAccess().isBefore(cutoff));
    }
}
