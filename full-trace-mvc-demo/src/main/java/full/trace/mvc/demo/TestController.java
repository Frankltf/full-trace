package full.trace.mvc.demo;

import full.trace.core.annotation.TraceApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * @Intro
 * @Author liutengfei
 */
@RestController
public class TestController {


    @RequestMapping("ok")
    @TraceApi
    public String ok() {

        return "ok";
    }
}
