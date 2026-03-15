package com.r7b7.llm.optimize;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.r7b7.constant.HospAiKeys;
import com.r7b7.entity.CompletionResponse;
import com.r7b7.llm.LlmClient;
import com.r7b7.llm.exception.LlmException;
import com.r7b7.llm.exception.LlmRequestException;
import com.r7b7.model.ILLMRequest;

public final class AutoOptimizingLlmClient implements LlmClient {
    private static final Logger log = LoggerFactory.getLogger(AutoOptimizingLlmClient.class);

    private final LlmClient delegate;
    private final RetryConfig retryConfig;
    private final AdaptiveTuningConfig tuningConfig;
    private final Random jitterRandom;

    private AutoOptimizingLlmClient(LlmClient delegate, OptimizerConfig config) {
        this.delegate = Objects.requireNonNull(delegate, "delegate must not be null");
        OptimizerConfig cfg = config != null ? config : OptimizerConfig.defaults();
        this.retryConfig = cfg.retry() != null ? cfg.retry() : RetryConfig.defaults();
        this.tuningConfig = cfg.tuning() != null ? cfg.tuning() : AdaptiveTuningConfig.defaults();
        this.jitterRandom = new Random();
    }

    public static AutoOptimizingLlmClient wrap(LlmClient delegate) {
        return new AutoOptimizingLlmClient(delegate, OptimizerConfig.defaults());
    }

    public static AutoOptimizingLlmClient wrap(LlmClient delegate, OptimizerConfig config) {
        return new AutoOptimizingLlmClient(delegate, config);
    }

    @Override
    public CompletionResponse chat(ILLMRequest request) {
        ILLMRequest tunedRequest = applyAdaptiveTuning(request);

        int attempt = 0;
        Throwable last = null;
        while (attempt < retryConfig.maxAttempts()) {
            attempt++;
            try {
                return delegate.chat(tunedRequest);
            } catch (Throwable t) {
                last = t;
                if (!shouldRetry(t) || attempt >= retryConfig.maxAttempts()) {
                    throw t;
                }

                Duration sleepFor = computeBackoff(attempt, retryConfig.baseDelay(), retryConfig.maxDelay());
                log.debug("Retrying LLM request after failure (attempt {}/{}), sleeping {}ms: {}",
                        attempt, retryConfig.maxAttempts(), sleepFor.toMillis(), t.toString());
                sleepUninterruptibly(sleepFor);
            }
        }

        if (last instanceof RuntimeException re) {
            throw re;
        }
        throw new RuntimeException(last);
    }

    private ILLMRequest applyAdaptiveTuning(ILLMRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("request must not be null");
        }

        Map<String, Object> merged = new HashMap<>();
        if (request.getParameters() != null) {
            merged.putAll(request.getParameters());
        }

        merged.putIfAbsent(HospAiKeys.Json.MAX_TOKENS, tuningConfig.defaultMaxTokens());
        clampInt(merged, HospAiKeys.Json.MAX_TOKENS, tuningConfig.minMaxTokens(), tuningConfig.maxMaxTokens());

        merged.putIfAbsent(HospAiKeys.Json.TEMPERATURE, tuningConfig.defaultTemperature());
        clampDouble(merged, HospAiKeys.Json.TEMPERATURE, tuningConfig.minTemperature(), tuningConfig.maxTemperature());

        return new OptimizedRequest(request, merged);
    }

    private static void clampInt(Map<String, Object> params, String key, int min, int max) {
        Object val = params.get(key);
        if (val == null) {
            return;
        }
        try {
            int parsed = (val instanceof Number n) ? n.intValue() : Integer.parseInt(val.toString());
            int clamped = Math.max(min, Math.min(max, parsed));
            params.put(key, clamped);
        } catch (Exception ignored) {
        }
    }

    private static void clampDouble(Map<String, Object> params, String key, double min, double max) {
        Object val = params.get(key);
        if (val == null) {
            return;
        }
        try {
            double parsed = (val instanceof Number n) ? n.doubleValue() : Double.parseDouble(val.toString());
            double clamped = Math.max(min, Math.min(max, parsed));
            params.put(key, clamped);
        } catch (Exception ignored) {
        }
    }

    private boolean shouldRetry(Throwable t) {
        if (t instanceof LlmRequestException lre) {
            int sc = lre.getStatusCode();
            return sc == 429 || sc == 408 || (sc >= 500 && sc <= 599);
        }

        if (t instanceof LlmException le) {
            Throwable cause = le.getCause();
            if (cause == null) {
                return false;
            }
            String cn = cause.getClass().getName();
            if (cause instanceof java.io.IOException) {
                return true;
            }
            if ("java.net.http.HttpTimeoutException".equals(cn)) {
                return true;
            }
            if (cause instanceof InterruptedException) {
                Thread.currentThread().interrupt();
                return false;
            }
            return false;
        }

        if (t instanceof java.io.IOException) {
            return true;
        }

        return false;
    }

    private Duration computeBackoff(int attempt, Duration baseDelay, Duration maxDelay) {
        long baseMs = baseDelay.toMillis();
        long maxMs = maxDelay.toMillis();

        long exp = baseMs * (1L << Math.max(0, attempt - 1));
        long capped = Math.min(exp, maxMs);

        long jitter = (long) (capped * (0.2 * jitterRandom.nextDouble()));
        long finalMs = Math.min(maxMs, capped + jitter);
        return Duration.ofMillis(Math.max(1, finalMs));
    }

    private static void sleepUninterruptibly(Duration d) {
        try {
            Thread.sleep(d.toMillis());
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new LlmException("Retry sleep interrupted", ie);
        }
    }
}
