package full.trace.core.spring;

import brave.Tracing;
import brave.baggage.BaggageField;
import brave.baggage.BaggagePropagation;
import brave.baggage.BaggagePropagationConfig;
import brave.http.HttpTracing;
import brave.propagation.B3Propagation;
import brave.propagation.Propagation;
import brave.propagation.ThreadLocalCurrentTraceContext;
import full.trace.core.config.TraceConstant;
import full.trace.core.core.FullTraceSpanHandler;
import full.trace.core.core.GenericFullTraceContext;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @Intro
 * @Author liutengfei
 */
public class FullTraceFactoryBean<T> implements FactoryBean<T>, ApplicationContextAware {
    private GenericFullTraceContext genericFullTraceContext;
    private Class<T> type;
    private ApplicationContext applicationContext;

    public FullTraceFactoryBean(Class type , GenericFullTraceContext genericFullTraceContext) {
        this.type = type;
        this.genericFullTraceContext = genericFullTraceContext;
    }

    @Override
    public T getObject() throws Exception {
        BaggageField FULL_TRACE = BaggageField.create(TraceConstant.FULL_TRACE);
        BaggageField FULL_TRACE_CURRENT_SPAN = BaggageField.create(TraceConstant.FULL_TRACE_CURRENT_SPAN);

        Propagation.Factory factory = BaggagePropagation.newFactoryBuilder(B3Propagation.FACTORY)
                .add(BaggagePropagationConfig.SingleBaggageField.newBuilder(FULL_TRACE).build())
                .add(BaggagePropagationConfig.SingleBaggageField.newBuilder(FULL_TRACE_CURRENT_SPAN).build())
                .build();
        Tracing tracing = Tracing.newBuilder()
                .localServiceName(applicationContext.getEnvironment().getProperty(TraceConstant.FULL_TRACE_APP_NAME,"default"))
                .propagationFactory(factory)
                .currentTraceContext(ThreadLocalCurrentTraceContext.newBuilder().build())
                .addSpanHandler(new FullTraceSpanHandler(genericFullTraceContext))
                .build();
        HttpTracing httpTracing = HttpTracing.create(tracing);
        return (T) httpTracing;
    }

    @Override
    public Class<?> getObjectType() {
        return type;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
