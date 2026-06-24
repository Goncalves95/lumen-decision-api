package com.lumendecision.config;

import com.lumendecision.exception.RateLimitExceededException;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class RateLimitConfig implements WebMvcConfigurer {

    @Value("${lumen.rate-limit.capacity:100}")
    private int capacity;

    @Value("${lumen.rate-limit.refill-tokens:100}")
    private int refillTokens;

    @Value("${lumen.rate-limit.refill-duration-hours:1}")
    private int refillDurationHours;

    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        registry.addInterceptor(new RateLimitInterceptor(capacity, refillTokens, refillDurationHours))
                .addPathPatterns("/api/v1/score");
    }

    public static class RateLimitInterceptor implements HandlerInterceptor {

        private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();
        private final int capacity;
        private final int refillTokens;
        private final int refillDurationHours;

        public RateLimitInterceptor(int capacity, int refillTokens, int refillDurationHours) {
            this.capacity = capacity;
            this.refillTokens = refillTokens;
            this.refillDurationHours = refillDurationHours;
        }

        @Override
        public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                  @NonNull Object handler) {
            if (!"POST".equalsIgnoreCase(request.getMethod())) {
                return true;
            }

            String clientIp = resolveClientIp(request);
            Bucket bucket = buckets.computeIfAbsent(clientIp, ip -> newBucket());

            if (!bucket.tryConsume(1)) {
                throw new RateLimitExceededException(
                        "Rate limit exceeded: maximum " + capacity + " requests per hour. Please try again later.");
            }
            return true;
        }

        private Bucket newBucket() {
            Bandwidth limit = Bandwidth.classic(capacity,
                    Refill.intervally(refillTokens, Duration.ofHours(refillDurationHours)));
            return Bucket.builder().addLimit(limit).build();
        }

        private String resolveClientIp(HttpServletRequest request) {
            String forwarded = request.getHeader("X-Forwarded-For");
            if (forwarded != null && !forwarded.isBlank()) {
                return forwarded.split(",")[0].trim();
            }
            return request.getRemoteAddr();
        }
    }
}
