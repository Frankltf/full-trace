package full.trace.core.core;

import brave.baggage.BaggageField;
import brave.handler.MutableSpan;
import brave.propagation.TraceContext;
import com.google.common.collect.Lists;
import full.trace.core.FullTraceContext;
import full.trace.core.config.TraceConstant;
import full.trace.core.model.FinalTraceBO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @Intro
 * @Author liutengfei
 */
public abstract class AbstractFullTraceContext implements FullTraceContext {
    private ConcurrentMap<String, Map<String , MutableSpan>> fullSpans = new ConcurrentHashMap<>();
    private ConcurrentMap<String, Map<Long , TraceContext>> fullTraceContext = new ConcurrentHashMap<>();

    @Override
    public List<FinalTraceBO> getDetailTrace() {
        BaggageField fullTrace = BaggageField.getByName(TraceConstant.FULL_TRACE);
        String localRootIdString = fullTrace.getValue();
        List<FinalTraceBO> finalTraceBOS = transformTrace(fullSpans.get(localRootIdString) ,true);
        return finalTraceBOS;
    }

    @Override
    public List<FinalTraceBO> getFinalTrace() {
        BaggageField fullTrace = BaggageField.getByName(TraceConstant.FULL_TRACE);
        String localRootIdString = fullTrace.getValue();
        MutableSpan mutableSpan = fullSpans.get(localRootIdString).get(localRootIdString);
        Boolean detail = Boolean.valueOf(mutableSpan.tag(TraceConstant.FULL_TRACE_ROOT_SPAN_ANNOTATION));
        List<FinalTraceBO> finalTraceBOS = transformTrace(fullSpans.get(localRootIdString) ,detail);
        return finalTraceBOS;
    }

    @Override
    public Boolean registerTraceContext(TraceContext context) {
        try {
            String localRootIdString =context.localRootIdString();
            if(null == fullTraceContext.get(localRootIdString)){
                Map<Long , TraceContext> contextMap = new HashMap<>();
                contextMap.put(context.spanId(),context);
                fullTraceContext.putIfAbsent(localRootIdString , contextMap);
            }else {
                Map<Long , TraceContext>  contextMap = fullTraceContext.get(localRootIdString);
                contextMap.put(context.spanId(),context);
                fullTraceContext.putIfAbsent(localRootIdString , contextMap);
            }
            return true;
        }catch (Exception e){
            return false;
        }
    }

    @Override
    public Boolean removeTraceContext(TraceContext context) {
        try {
            String localRootIdString =context.localRootIdString();
            if(null != fullTraceContext.get(localRootIdString)){
                fullTraceContext.remove(localRootIdString);
            }
            return true;
        }catch (Exception e){
            return false;
        }
    }

    private FinalTraceBO createFinalTraceBOS(MutableSpan mutableSpan){
        FinalTraceBO finalTraceBO = new FinalTraceBO();
        finalTraceBO.setTraceId(mutableSpan.id());
        finalTraceBO.setParentId(mutableSpan.parentId());
        long startTimestamp = mutableSpan.startTimestamp()/1000;
        finalTraceBO.setStartTime(startTimestamp);
        finalTraceBO.setEndTime(mutableSpan.finishTimestamp()/1000);
        finalTraceBO.setTraceTime(finalTraceBO.getEndTime() - finalTraceBO.getStartTime());
        finalTraceBO.setServer(mutableSpan.localServiceName());
        finalTraceBO.setTag( mutableSpan.tag(TraceConstant.FULL_TRACE_API_NAME));
        finalTraceBO.setErrMsg(null == mutableSpan.error() ? "" : mutableSpan.error().getMessage());
        return finalTraceBO;
    }

    private List<FinalTraceBO> transformTrace(Map<String , MutableSpan> spans, Boolean detail){
        List<FinalTraceBO> finalTraceBOS = Lists.newArrayList();
        if( detail){
            spans.forEach((key , value) -> {
                finalTraceBOS.add(createFinalTraceBOS(value));
            });
        }else{
            BaggageField localRoot = BaggageField.getByName(TraceConstant.FULL_TRACE);
            MutableSpan mutableSpan = spans.get(localRoot.getValue());
            finalTraceBOS.add(createFinalTraceBOS(mutableSpan));
        }
        return finalTraceBOS;
    }

    @Override
    public Boolean registerSpan(TraceContext context, MutableSpan span) {
        try {
            String localRootIdString =context.localRootIdString();
            if(null == fullSpans.get(localRootIdString)){
                Map<String , MutableSpan> spans = new HashMap<>();
                spans.put(span.id(),span);
                fullSpans.putIfAbsent(localRootIdString , spans);
            }else {
                Map<String , MutableSpan>  mutableSpans = fullSpans.get(localRootIdString);
                mutableSpans.put(span.id() , span);
                fullSpans.putIfAbsent(localRootIdString , mutableSpans);
            }
            return true;
        }catch (Exception e){
            return false;
        }
    }

    @Override
    public Boolean removeSpan(TraceContext context) {
        try {
            String localRootIdString =context.localRootIdString();
            if(null != fullSpans.get(localRootIdString)){
                fullSpans.remove(localRootIdString);
            }
            return true;
        }catch (Exception e){
            return false;
        }
    }

    @Override
    public Boolean recordCurrentSpan(TraceContext context) {
        BaggageField fullTrace = BaggageField.getByName(TraceConstant.FULL_TRACE_CURRENT_SPAN);
        if(null != fullTrace){
            fullTrace.updateValue(String.valueOf(context.spanId()));
        }
        BaggageField localRoot = BaggageField.getByName(TraceConstant.FULL_TRACE);
        localRoot.updateValue(context.localRootIdString());
        return true;
    }

    @Override
    public TraceContext getCurrentTraceContext() {
        BaggageField currentTrace = BaggageField.getByName(TraceConstant.FULL_TRACE_CURRENT_SPAN);
        if(null != currentTrace){
            String currentSpanId = currentTrace.getValue();
            BaggageField fullTrace = BaggageField.getByName(TraceConstant.FULL_TRACE);
            String localRootIdString = fullTrace.getValue();
            return fullTraceContext.get(localRootIdString).get(Long.valueOf(currentSpanId));
        }
        return null;
    }
}
