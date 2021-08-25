package full.trace.mvc.demo;

import full.trace.core.annotation.EnableFullTrace;
import org.springframework.context.annotation.Configuration;

/**
 * @Intro
 * @Author liutengfei
 */
@Configuration
@EnableFullTrace
public class TestConfig {
    public TestConfig() {

        System.out.print("fefe");
    }
}
