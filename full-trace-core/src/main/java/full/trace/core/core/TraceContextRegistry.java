package full.trace.core.core;

import brave.propagation.TraceContext;

/**
 * @Intro
 * @Author liutengfei
 */
public interface TraceContextRegistry {
    Boolean registerTraceContext(TraceContext context);
    Boolean removeTraceContext(TraceContext context);
    TraceContext getCurrentTraceContext();
}
