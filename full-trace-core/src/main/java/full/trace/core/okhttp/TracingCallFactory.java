package full.trace.core.okhttp;

import brave.Tracing;
import brave.http.HttpTracing;
import brave.okhttp3.TracingInterceptor;
import brave.propagation.CurrentTraceContext;
import brave.propagation.TraceContext;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * @Intro
 * @Author liutengfei
 */
public class TracingCallFactory implements Call.Factory {
    static final TraceContext NULL_SENTINEL = TraceContext.newBuilder()
            .traceId(1L).spanId(1L).build();

    public static Call.Factory create(Tracing tracing, OkHttpClient ok) {
        return create(HttpTracing.create(tracing), ok);
    }

    public static Call.Factory create(HttpTracing httpTracing, OkHttpClient ok) {
        return new TracingCallFactory(httpTracing, ok);
    }

    final CurrentTraceContext currentTraceContext;
    final OkHttpClient ok;

    TracingCallFactory(HttpTracing httpTracing, OkHttpClient ok) {
        if (httpTracing == null) throw new NullPointerException("HttpTracing == null");
        if (ok == null) throw new NullPointerException("OkHttpClient == null");
        this.currentTraceContext = httpTracing.tracing().currentTraceContext();
        OkHttpClient.Builder builder = ok.newBuilder();
        builder.networkInterceptors().add(0, TracingInterceptor.create(httpTracing));
        this.ok = builder.build();
    }

    @Override public Call newCall(Request request) {
        TraceContext invocationContext = currentTraceContext.get();
        Call call = ok.newCall(request.newBuilder()
                .tag(TraceContext.class, invocationContext != null ? invocationContext : NULL_SENTINEL)
                .build());
        return new TraceContextCall(call, currentTraceContext, invocationContext);
    }
}
