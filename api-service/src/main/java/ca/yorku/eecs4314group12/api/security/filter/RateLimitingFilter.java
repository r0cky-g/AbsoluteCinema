package ca.yorku.eecs4314group12.api.security.filter;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RateLimitingFilter implements Filter {

    private static final int MAX_TOKENS = 20;
    private static final double REFILL_RATE = 60.0 / 60.0; // number per minute

    private final Cache<String, TokenBucket> buckets = Caffeine.newBuilder()
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .maximumSize(1000)
            .build();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String clientIp = httpRequest.getRemoteAddr();

        TokenBucket bucket = buckets.get(clientIp, ip -> new TokenBucket(MAX_TOKENS));

        synchronized (bucket) {
            bucket.refill();

            if ((int) bucket.tokens > 0) {
                bucket.tokens--;
                chain.doFilter(request, response);
            } else {
                httpResponse.setStatus(429);
                httpResponse.getWriter().write("Too many requests. Please try again later.");
            }
        }
    }

    static class TokenBucket {
        double tokens;
        long lastRefillTime;

        TokenBucket(int capacity) {
            this.tokens = capacity;
            this.lastRefillTime = System.nanoTime();
        }

        void refill() {
            long now = System.nanoTime();
            double secondsPassed = (now - lastRefillTime) / 1_000_000_000.0;

            tokens = Math.min(MAX_TOKENS, tokens + secondsPassed * REFILL_RATE);
            lastRefillTime = now;
        }
    }
}
