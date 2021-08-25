package full.trace.demo;

import lombok.Data;

/**
 * @Intro
 * @Author liutengfei
 */
@Data
public class ReponseDTO {
    private Integer code;
    private Object data;
    private String msg;

    @Override
    public String toString() {
        return "ReponseDTO{" +
                "code=" + code +
                ", data=" + data +
                ", msg='" + msg + '\'' +
                '}';
    }
}
