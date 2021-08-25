package full.trace.demo;

import full.trace.core.annotation.TraceFun;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Intro
 * @Author liutengfei
 */
@Service
public class DemoService2 {


    @TraceFun
    public String test22() throws Exception {
        Thread.sleep(1000);
        return "ok";
    }

}
