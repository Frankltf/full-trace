package full.trace.demo;

import full.trace.core.annotation.TraceFun;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Intro
 * @Author liutengfei
 */
@Service
public class DemoService {
//    @Autowired
//    private DemoRequest2 demoRequest2;

    @Autowired
    private DemoService2 demoService2;

    public String test() throws Exception {
        throw new Exception("dd");
    }

    @TraceFun
    public String test2() throws Exception {
        Thread.sleep(1000);
        demoService2.test22();
        return "ok";
    }

//    @TraceFun
//    public String test4() throws Exception {
//        ReponseDTO res = demoRequest2.detect("hello","fr","test");
//        return "ok";
//    }
}
