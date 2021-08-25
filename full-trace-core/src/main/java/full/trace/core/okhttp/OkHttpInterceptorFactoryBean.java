package full.trace.core.okhttp;

import full.trace.core.core.GenericFullTraceContext;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @Intro
 * @Author liutengfei
 */
public class OkHttpInterceptorFactoryBean<T> implements FactoryBean<T>, ApplicationContextAware {
    private GenericFullTraceContext genericFullTraceContext;
    private Class<T> type;
    private ApplicationContext applicationContext;

    public OkHttpInterceptorFactoryBean(Class type , GenericFullTraceContext genericFullTraceContext) {
        this.type = type;
        this.genericFullTraceContext = genericFullTraceContext;
    }

    @Override
    public T getObject() throws Exception {
        return (T) TraceHttpInterceptor.create(genericFullTraceContext.getTracing() , genericFullTraceContext);
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
