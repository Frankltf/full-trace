package full.trace.core;

import brave.http.HttpTracing;
import full.trace.core.core.SpanRegistry;
import full.trace.core.core.TraceContextRegistry;

/**
 * @Intro
 * @Author liutengfei
 */
public interface FullTraceContext extends SpanRegistry , TraceContextRegistry {
    HttpTracing getTracing();


}
