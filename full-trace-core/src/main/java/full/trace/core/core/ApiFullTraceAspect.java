package full.trace.core.core;

import brave.Span;
import brave.SpanCustomizer;
import brave.Tracing;
import full.trace.core.annotation.TraceApi;
import full.trace.core.annotation.TraceFun;
import full.trace.core.config.TraceConstant;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Intro
 * @Author liutengfei
 */
@Aspect
public class ApiFullTraceAspect {
    @Autowired
    private GenericFullTraceContext genericFullTraceContext;

    @Pointcut("@annotation(full.trace.core.annotation.TraceFun)")
    public void pointcut() { }

    @Before(value = "@annotation(traceApi)",argNames = "point,traceApi")
    public void handle(JoinPoint point, TraceApi traceApi) {
        Tracing tracing = Tracing.current();
        SpanCustomizer span = tracing != null ? tracing.tracer().currentSpan() : null;
        if (span != null) {
            span.tag(TraceConstant.FULL_TRACE_API_NAME, StringUtils.isEmpty(traceApi.tagName()) ? point.getSignature().getName() : traceApi.tagName());
            span.tag(TraceConstant.FULL_TRACE_ROOT_SPAN_ANNOTATION , String.valueOf(traceApi.detail()));
        }
    }

    @Around(value = "@annotation(traceFun)",argNames = "joinPoint,traceFun")
    public void around (ProceedingJoinPoint joinPoint, TraceFun traceFun) throws Throwable {
        String tagName = StringUtils.isEmpty(traceFun.tagName()) ? joinPoint.getSignature().getName() : traceFun.tagName();
        Span currentSpan = genericFullTraceContext
                .getTracing()
                .tracing()
                .tracer()
                .newChild(genericFullTraceContext.getCurrentTraceContext())
                .name(tagName)
                .tag(TraceConstant.FULL_TRACE_API_NAME , tagName)
                .start();
        genericFullTraceContext.recordCurrentSpan(currentSpan.context());
        Object[] args = joinPoint.getArgs();
        Object result = joinPoint.proceed(args);
        currentSpan.finish();
    }

}
