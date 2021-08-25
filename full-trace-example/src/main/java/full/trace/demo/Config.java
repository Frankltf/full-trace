package full.trace.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import probe.feign.boost.config.OkHttpConfig;

/**
 * @Intro
 * @Author liutengfei
 */
@Configuration
public class Config {
    @Bean
    public OkHttpConfig okHttpConfig(){
        OkHttpConfig okHttpConfig = new OkHttpConfig.Builder()
                .setConnectTimeout(2000L)
                .build();
        return okHttpConfig;
    }

}
