package full.trace.demo;

import full.trace.core.annotation.EnableFullTrace;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import probe.feign.boost.annotation.HystrixFeignScan;

@SpringBootApplication
@EnableFullTrace
//@HystrixFeignScan(value = {"full.trace.demo"})
public class FullTraceApplication {

	public static void main(String[] args) {
		System.setProperty("app.name","ltf");
		SpringApplication.run(FullTraceApplication.class, args);
	}

}
