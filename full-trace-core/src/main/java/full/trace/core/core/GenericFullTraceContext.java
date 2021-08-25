package full.trace.core.core;

import brave.http.HttpTracing;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Intro
 * @Author liutengfei
 */
public class GenericFullTraceContext extends AbstractFullTraceContext {
    @Autowired
    private HttpTracing httpTracing;


    @Override
    public HttpTracing getTracing() {
        return httpTracing;
    }
}
