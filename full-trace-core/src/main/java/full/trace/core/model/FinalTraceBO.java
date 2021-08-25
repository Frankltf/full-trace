package full.trace.core.model;

/**
 * @Intro
 * @Author liutengfei
 */
public class FinalTraceBO {
    private String traceId;
    private String parentId;
    private Long startTime;
    private Long endTime;
    private Long traceTime;
    private String tag;
    private String server;
    private String errMsg;

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public Long getTraceTime() {
        return traceTime;
    }

    public void setTraceTime(Long traceTime) {
        this.traceTime = traceTime;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    @Override
    public String toString() {
        return "FinalTraceBO{" +
                "traceId='" + traceId + '\'' +
                ", parentId='" + parentId + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", traceTime=" + traceTime +
                ", tag='" + tag + '\'' +
                ", server='" + server + '\'' +
                ", errMsg='" + errMsg + '\'' +
                '}';
    }
}
