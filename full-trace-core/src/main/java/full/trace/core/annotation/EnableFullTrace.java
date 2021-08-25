package full.trace.core.annotation;

import full.trace.core.spring.FullTraceComponentScanRegistrar;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @Intro
 * @Author liutengfei
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Import(FullTraceComponentScanRegistrar.class)
@EnableAspectJAutoProxy
public @interface EnableFullTrace {
}
