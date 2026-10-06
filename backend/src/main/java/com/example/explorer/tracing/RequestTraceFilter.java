package com.example.explorer.tracing;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RequestTraceFilter implements Filter {

    // Almacena trazas completadas para consultar después via /api/trace/{id}
    private static final Map<String, RequestTraceContext.TraceData> COMPLETED_TRACES = new ConcurrentHashMap<>();
    private static final int MAX_TRACES = 200;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (request instanceof HttpServletRequest httpReq && response instanceof HttpServletResponse httpRes) {
            String traceId = RequestTraceContext.init();
            RequestTraceContext.addStep("Filter", "RequestTraceFilter", httpReq.getMethod() + " " + httpReq.getRequestURI());
            
            httpRes.setHeader("X-Trace-Id", traceId);
            try {
                chain.doFilter(request, response);
            } finally {
                RequestTraceContext.addStep("Filter", "RequestTraceFilter", "Response status: " + httpRes.getStatus());
                // Guardar traza completada
                var traceData = RequestTraceContext.current();
                if (traceData != null) {
                    if (COMPLETED_TRACES.size() >= MAX_TRACES) {
                        // Limpiar las más viejas (simple: borrar todas y empezar de nuevo)
                        COMPLETED_TRACES.clear();
                    }
                    COMPLETED_TRACES.put(traceId, traceData);
                }
                RequestTraceContext.clear();
            }
        } else {
            chain.doFilter(request, response);
        }
    }

    public static Map<String, RequestTraceContext.TraceData> getCompletedTraces() {
        return COMPLETED_TRACES;
    }
}
