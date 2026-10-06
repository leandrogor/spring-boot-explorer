package com.example.explorer.tracing;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/trace")
public class TraceController {

    @GetMapping("/{traceId}")
    public RequestTraceContext.TraceData getTrace(@PathVariable String traceId) {
        RequestTraceContext.TraceData data = RequestTraceFilter.getCompletedTraces().get(traceId);
        if (data == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Traza no encontrada");
        }
        return data;
    }
}
