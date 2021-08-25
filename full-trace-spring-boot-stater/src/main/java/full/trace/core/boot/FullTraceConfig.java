package full.trace.core.boot;

import full.trace.core.core.GenericFullTraceContext;
import full.trace.core.web.FullTraceServletFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Intro
 * @Author liutengfei
 */
@Configuration
public class FullTraceConfig {

    @Bean
    public FilterRegistrationBean filterRegistrationBean(GenericFullTraceContext genericFullTraceContext){
        FilterRegistrationBean<FullTraceServletFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new FullTraceServletFilter(genericFullTraceContext));
        registration.addUrlPatterns("/*");
        registration.setName(FullTraceServletFilter.class.getName());
        return registration;
    }


}
