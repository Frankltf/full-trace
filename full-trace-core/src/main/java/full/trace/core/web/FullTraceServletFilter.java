package full.trace.core.web;

import brave.Span;
import brave.SpanCustomizer;
import brave.Tracing;
import brave.baggage.BaggageField;
import brave.baggage.BaggagePropagation;
import brave.baggage.BaggagePropagationConfig;
import brave.http.HttpServerHandler;
import brave.http.HttpServerRequest;
import brave.http.HttpServerResponse;
import brave.http.HttpTracing;
import brave.propagation.*;
import brave.servlet.HttpServletRequestWrapper;
import brave.servlet.HttpServletResponseWrapper;
import brave.servlet.internal.ServletRuntime;
import com.alibaba.fastjson.JSON;
import full.trace.core.config.TraceConstant;
import full.trace.core.core.FullTraceSpanHandler;
import full.trace.core.core.GenericFullTraceContext;
import full.trace.core.model.FinalTraceBO;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;


/**
 * @Intro
 * @Author liutengfei
 */
public class FullTraceServletFilter implements Filter {
    final ServletRuntime servlet = ServletRuntime.get();
    final HttpServerHandler<HttpServerRequest, HttpServerResponse> handler;
    private GenericFullTraceContext genericFullTraceContext;
    private HttpTracing httpTracing;

    public FullTraceServletFilter(GenericFullTraceContext genericFullTraceContext){
        this.genericFullTraceContext = genericFullTraceContext;
        this.httpTracing = genericFullTraceContext.getTracing();
        this.handler = HttpServerHandler.create(httpTracing);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse res = servlet.httpServletResponse(servletResponse);

        TraceContext context = (TraceContext) servletRequest.getAttribute(TraceContext.class.getName());
        if (context != null) {
            CurrentTraceContext.Scope scope = httpTracing.tracing().currentTraceContext().maybeScope(context);
            try {
                filterChain.doFilter(servletRequest, servletResponse);
            } finally {
                scope.close();
            }
            return;
        }

        Span span = handler.handleReceive(HttpServletRequestWrapper.create(req));
        servletRequest.setAttribute(SpanCustomizer.class.getName(), span.customizer());
        servletRequest.setAttribute(TraceContext.class.getName(), span.context());
        Throwable error = null;
        CurrentTraceContext.Scope scope = httpTracing.tracing().currentTraceContext().newScope(span.context());

        genericFullTraceContext.recordCurrentSpan(span.context());

        ContentCachingResponseWrapper wrapperResponse = new ContentCachingResponseWrapper(res);
        try {
            filterChain.doFilter(req, wrapperResponse);
        } catch (Throwable e) {
            error = e;
            throw e;
        } finally {
            if (servlet.isAsync(req)) {
                servlet.handleAsync(handler, req, wrapperResponse, span);
            } else{
                HttpServerResponse responseWrapper = HttpServletResponseWrapper.create(req, wrapperResponse, error);
                handler.handleSend(responseWrapper, span);
            }
            interceptBeforeResponseReturn(wrapperResponse , span.context());
            wrapperResponse.copyBodyToResponse();
            scope.close();
        }
    }

    public void interceptBeforeResponseReturn(ContentCachingResponseWrapper wrapperResponse , TraceContext context) {
        try {
            List<FinalTraceBO> finalTrace = genericFullTraceContext.getFinalTrace();
            wrapperResponse.setHeader(TraceConstant.FULL_TRACE_HEADER_NAME , JSON.toJSONString(finalTrace));
            genericFullTraceContext.removeSpan(context);
            genericFullTraceContext.removeTraceContext(context);
        } catch (Exception e) {
            System.out.print("ff");
        }
    }

    @Override
    public void destroy() {
    }
}
