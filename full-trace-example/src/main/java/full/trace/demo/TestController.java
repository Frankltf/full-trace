package full.trace.demo;

import full.trace.core.annotation.TraceApi;
import full.trace.core.core.GenericFullTraceContext;
import full.trace.core.model.FinalTraceBO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.handler.AbstractHandlerMethodMapping;
import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @Intro
 * @Author liutengfei
 */
@RestController
public class TestController {

    @Autowired
    private RequestMappingHandlerMapping requestMappingHandlerMapping;


    @RequestMapping("ok")
    public String ok( ) throws Exception {
        Object handler = "testController";
        RequestMappingInfo mapping = createRequestMappingInfo();
        Class<?> cls = Class.forName(TestController.class.getName());
        Method method =cls.getMethod("requestMappingHandlerMapping" );
        requestMappingHandlerMapping.registerMapping(mapping,handler,method);
        return "ok";
    }

    protected RequestMappingInfo createRequestMappingInfo() {
        RequestMappingInfo.Builder builder = RequestMappingInfo
                .paths("okk");
        return builder.build();
    }

    public String requestMappingHandlerMapping( ){
        return "requestMappingHandlerMapping";
    }

    @Autowired
    private GenericFullTraceContext genericFullTraceContext;

    @Autowired
    private DemoService demoService;

    @RequestMapping("testErr")
    @TraceApi
    public String testApi( ) throws Exception {
            List<FinalTraceBO> test = genericFullTraceContext.getFinalTrace();
            demoService.test();
        return "ok";
    }

    @RequestMapping("test")
    @TraceApi
    public String testApi2( ) throws Exception {
        List<FinalTraceBO> test = genericFullTraceContext.getFinalTrace();
        return "ok";
    }

    @RequestMapping("testfun")
    @TraceApi(detail = true)
    public String testApi3( ) throws Exception {
        demoService.test2();
        return "ok";
    }

    @RequestMapping("testfun4")
    @TraceApi
    public String testApi4( ) throws Exception {
//        demoService.test4();
        return "ok";
    }
}
