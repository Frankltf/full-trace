package full.trace.core.spring;


import brave.http.HttpTracing;
import brave.okhttp3.TracingInterceptor;
import full.trace.core.core.ApiFullTraceAspect;
import full.trace.core.core.GenericFullTraceContext;
import full.trace.core.okhttp.OkHttpInterceptorFactoryBean;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;

import static com.alibaba.spring.util.BeanRegistrar.registerInfrastructureBean;

/**
 * @Intro
 * @Author liutengfei
 */
public class FullTraceServiceClassPostProcessor implements BeanDefinitionRegistryPostProcessor {

    public FullTraceServiceClassPostProcessor() {
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        registerInfrastructureBean(registry, ApiFullTraceAspect.class.getName(), ApiFullTraceAspect.class);
        registerInfrastructureBean(registry, GenericFullTraceContext.class.getName(), GenericFullTraceContext.class);

        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(HttpTracing.class);
        beanDefinitionBuilder.getBeanDefinition().getConstructorArgumentValues().addIndexedArgumentValue(0 , HttpTracing.class.getName());
        beanDefinitionBuilder.getBeanDefinition().getConstructorArgumentValues().addIndexedArgumentValue(1 , new RuntimeBeanReference(GenericFullTraceContext.class.getName()));
        beanDefinitionBuilder.getBeanDefinition().setBeanClassName(FullTraceFactoryBean.class.getName());
        registry.registerBeanDefinition(HttpTracing.class.getName(), beanDefinitionBuilder.getBeanDefinition());

        BeanDefinitionBuilder beanDefinitionBuilder2 = BeanDefinitionBuilder.genericBeanDefinition(TracingInterceptor.class);
        beanDefinitionBuilder2.getBeanDefinition().getConstructorArgumentValues().addIndexedArgumentValue(0 , TracingInterceptor.class.getName());
        beanDefinitionBuilder2.getBeanDefinition().getConstructorArgumentValues().addIndexedArgumentValue(1 , new RuntimeBeanReference(GenericFullTraceContext.class.getName()));
        beanDefinitionBuilder2.getBeanDefinition().setBeanClassName(OkHttpInterceptorFactoryBean.class.getName());
        registry.registerBeanDefinition(TracingInterceptor.class.getName(), beanDefinitionBuilder2.getBeanDefinition());

    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }




}
