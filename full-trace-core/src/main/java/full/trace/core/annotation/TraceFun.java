package full.trace.core.annotation;

import java.lang.annotation.*;

/**
 * @Intro
 * @Author liutengfei
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface TraceFun {
    String tagName() default "";
}
