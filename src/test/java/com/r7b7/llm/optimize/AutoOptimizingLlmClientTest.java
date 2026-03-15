package com.r7b7.llm.optimize;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

import com.r7b7.constant.HospAiKeys;
import com.r7b7.entity.CompletionResponse;
import com.r7b7.entity.Message;
import com.r7b7.entity.Role;
import com.r7b7.llm.LlmClient;
import com.r7b7.llm.exception.LlmRequestException;
import com.r7b7.model.BaseLLMRequest;
import com.r7b7.model.ILLMRequest;

public class AutoOptimizingLlmClientTest {

    @Test
    public void adaptiveTuning_addsDefaultsWhenAbsent() {
        AtomicInteger calls = new AtomicInteger();

        LlmClient delegate = new LlmClient() {
            @Override
            public CompletionResponse chat(ILLMRequest request) {
                calls.incrementAndGet();
                assertNotNull(request.getParameters());
                assertTrue(request.getParameters().containsKey(HospAiKeys.Json.MAX_TOKENS));
                assertTrue(request.getParameters().containsKey(HospAiKeys.Json.TEMPERATURE));
                return new CompletionResponse(null, null, null);
            }
        };

        AutoOptimizingLlmClient client = AutoOptimizingLlmClient.wrap(delegate);

        BaseLLMRequest req = new BaseLLMRequest(
                List.of(new Message(Role.user, "hi")),
                Map.of(),
                null,
                null);

        client.chat(req);
        assertEquals(1, calls.get());
    }

    @Test
    public void retry_retriesOn429() {
        AtomicInteger calls = new AtomicInteger();

        LlmClient delegate = new LlmClient() {
            @Override
            public CompletionResponse chat(ILLMRequest request) {
                int c = calls.incrementAndGet();
                if (c == 1) {
                    throw new LlmRequestException("rate limited", 429, "");
                }
                return new CompletionResponse(null, null, null);
            }
        };

        AutoOptimizingLlmClient client = AutoOptimizingLlmClient.wrap(delegate,
                new OptimizerConfig(new RetryConfig(2, java.time.Duration.ofMillis(1), java.time.Duration.ofMillis(5)),
                        AdaptiveTuningConfig.defaults()));

        BaseLLMRequest req = new BaseLLMRequest(
                List.of(new Message(Role.user, "hi")),
                Map.of(HospAiKeys.Json.TEMPERATURE, 0.9),
                null,
                null);

        client.chat(req);
        assertEquals(2, calls.get());
    }
}
