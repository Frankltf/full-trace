package full.trace.core.annotation;

import java.lang.annotation.*;

/**
 * @Intro
 * @Author liutengfei
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface TraceApi {
    boolean detail() default false;
    String tagName() default "";
}
