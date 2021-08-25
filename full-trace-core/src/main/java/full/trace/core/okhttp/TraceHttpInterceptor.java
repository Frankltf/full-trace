/*
 * Copyright 2013-2020 The OpenZipkin Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package full.trace.core.okhttp;

import brave.Span;
import brave.Tracing;
import brave.http.HttpClientHandler;
import brave.http.HttpClientRequest;
import brave.http.HttpClientResponse;
import brave.http.HttpTracing;
import brave.internal.Nullable;
import brave.propagation.CurrentTraceContext;
import brave.propagation.CurrentTraceContext.Scope;
import brave.propagation.TraceContext;
import java.io.IOException;
import java.net.InetSocketAddress;

import full.trace.core.config.TraceConstant;
import full.trace.core.core.GenericFullTraceContext;
import okhttp3.Connection;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public final class TraceHttpInterceptor implements Interceptor {
    final CurrentTraceContext currentTraceContext;
    final HttpClientHandler<HttpClientRequest, HttpClientResponse> handler;

    private GenericFullTraceContext genericFullTraceContext;

    public static Interceptor create(Tracing tracing ,GenericFullTraceContext genericFullTraceContext) {
        return create(HttpTracing.create(tracing) , genericFullTraceContext);
    }

    public static Interceptor create(HttpTracing httpTracing , GenericFullTraceContext genericFullTraceContext) {
        return new TraceHttpInterceptor(httpTracing , genericFullTraceContext);
    }

    TraceHttpInterceptor(HttpTracing httpTracing , GenericFullTraceContext genericFullTraceContext) {
        if (httpTracing == null) {
            throw new NullPointerException("HttpTracing == null");
        };
        currentTraceContext = httpTracing.tracing().currentTraceContext();
        handler = HttpClientHandler.create(httpTracing);
        this.genericFullTraceContext = genericFullTraceContext;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        RequestWrapper request = new RequestWrapper(chain.request());
        Span span;
        TraceContext parent = genericFullTraceContext.getCurrentTraceContext();
        if (parent != null) {
            span = handler.handleSendWithParent(request, parent != TracingCallFactory.NULL_SENTINEL ? parent : null);
        } else {
            span = handler.handleSend(request);
        }

        parseRouteAddress(chain, span);

        Response response = null;
        Throwable error = null;
        try (Scope ws = this.currentTraceContext.newScope(span.context())) {
            span.tag(TraceConstant.FULL_TRACE_API_NAME,request.path());
            genericFullTraceContext.recordCurrentSpan(parent);
            return response = chain.proceed(request.build());
        } catch (Throwable t) {
            error = t;
            throw t;
        } finally {
            if (response != null) {
                request = new RequestWrapper(response.request());
            }
            handler.handleReceive(new ResponseWrapper(request, response, error), span);
        }
    }

    static void parseRouteAddress(Chain chain, Span span) {
        if (span.isNoop()) {
            return;
        }
        Connection connection = chain.connection();
        if (connection == null) {
            return;
        }
        InetSocketAddress socketAddress = connection.route().socketAddress();
        span.remoteIpAndPort(socketAddress.getHostString(), socketAddress.getPort());
    }

    static final class RequestWrapper extends HttpClientRequest {
        final Request delegate;
        Request.Builder builder;

        RequestWrapper(Request delegate) {
            this.delegate = delegate;
        }

        @Override public Object unwrap() {
            return delegate;
        }

        @Override public String method() {
            return delegate.method();
        }

        @Override public String path() {
            return delegate.url().encodedPath();
        }

        @Override public String url() {
            return delegate.url().toString();
        }

        @Override public String header(String name) {
            return delegate.header(name);
        }

        @Override public void header(String name, String value) {
            if (builder == null) {
                builder = delegate.newBuilder();
            }
            builder.header(name, value);
        }

        Request build() {
            return builder != null ? builder.build() : delegate;
        }
    }

    static final class ResponseWrapper extends HttpClientResponse {
        final RequestWrapper request;
        @Nullable final Response response;
        @Nullable final Throwable error;

        ResponseWrapper(RequestWrapper request, @Nullable Response response,
                        @Nullable Throwable error) {
            this.request = request;
            this.response = response;
            this.error = error;
        }

        @Override public Object unwrap() {
            return response;
        }

        @Override public RequestWrapper request() {
            return request;
        }

        @Override public Throwable error() {
            return error;
        }

        @Override public int statusCode() {
            return response != null ? response.code() : 0;
        }
    }
}
