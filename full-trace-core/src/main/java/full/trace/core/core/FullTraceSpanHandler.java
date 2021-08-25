package full.trace.core.core;

import brave.handler.MutableSpan;
import brave.handler.SpanHandler;
import brave.propagation.TraceContext;

/**
 * @Intro
 * @Author liutengfei
 */
public class FullTraceSpanHandler extends SpanHandler {
    private GenericFullTraceContext genericFullTraceContext;

    public FullTraceSpanHandler(GenericFullTraceContext genericFullTraceContext) {
        this.genericFullTraceContext = genericFullTraceContext;
    }

    @Override
    public boolean begin(TraceContext context, MutableSpan span, TraceContext parent) {
        genericFullTraceContext.registerSpan(context , span);
        genericFullTraceContext.registerTraceContext(context);
        System.out.print("ddd");
        return true;
    }

    @Override
    public boolean end(TraceContext context, MutableSpan span, Cause cause) {

        return true;
    }

    @Override
    public boolean handlesAbandoned() {
        return super.handlesAbandoned();
    }

}
