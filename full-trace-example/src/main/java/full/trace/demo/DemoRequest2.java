package full.trace.demo;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import probe.feign.boost.annotation.FeignHost;

/**
 * @Intro
 * @Author liutengfei
 * @Date 2021-07-06 17:26
 */
@FeignHost(uri = "http://vtranslator.vmic.xyz")
public interface DemoRequest2 {

    @RequestLine("POST /api/lang_detect")
    @Headers({"Content-Type: application/json"})
    ReponseDTO detect(@Param("text") String text, @Param("fr") String fr, @Param("requestId") String requestId);

    public static class DemoRequestFallback implements DemoRequest2 {

        private Throwable cause;
        public DemoRequestFallback(Throwable cause) {
            this.cause = cause;
        }

        ReponseDTO empty = new ReponseDTO();

        @Override
        public ReponseDTO detect(String text, String fr, String requestId) {
            return empty;
        }
    }
}
