package full.trace.core.core;

import brave.handler.MutableSpan;
import brave.propagation.TraceContext;
import full.trace.core.model.FinalTraceBO;

import java.util.List;

/**
 * @Intro
 * @Author liutengfei
 */
public interface SpanRegistry {
    List<FinalTraceBO> getDetailTrace();
    List<FinalTraceBO> getFinalTrace();
    Boolean registerSpan(TraceContext context , MutableSpan span);
    Boolean removeSpan(TraceContext context);
    Boolean recordCurrentSpan(TraceContext context);

}
