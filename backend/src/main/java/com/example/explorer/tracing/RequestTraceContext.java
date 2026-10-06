package com.example.explorer.tracing;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RequestTraceContext {

    // ThreadLocal: cada thread tiene su propia copia (en Node.js usarías AsyncLocalStorage)
    private static final ThreadLocal<TraceData> CONTEXT = new ThreadLocal<>();

    public record TraceStep(String component, String className, String detail, Instant timestamp, long durationMs) {}
    public record TraceData(String traceId, List<TraceStep> steps, Instant startTime) {}

    public static String init() {
        String traceId = UUID.randomUUID().toString().substring(0, 8);
        CONTEXT.set(new TraceData(traceId, new ArrayList<>(), Instant.now()));
        return traceId;
    }

    public static void addStep(String component, String className, String detail) {
        TraceData data = CONTEXT.get();
        if (data != null) {
            long elapsed = java.time.Duration.between(data.startTime(), Instant.now()).toMillis();
            data.steps().add(new TraceStep(component, className, detail, Instant.now(), elapsed));
        }
    }

    public static TraceData current() {
        return CONTEXT.get();
    }

    public static void clear() {
        CONTEXT.remove(); // Importante: previene memory leaks en thread pools
    }
}
