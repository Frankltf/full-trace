# full-trace

## full-trace简介
- 采集服务端耗时等性能指标，添加到response header中，客户端通过埋点采集上报，从而实现对全链路性能指标监控。

## full-trace原理
- 集成brave和spring组件，实现自动添加拦截器，对拦截到的请求添加tracer和span。

## full-trace使用
- 见example
```
  //在接口入库添加@TraceApi注解
    @RequestMapping("testfun")
    @TraceApi(detail = true)  
    public String testApi3( ) throws Exception {
        demoService.test2();
        return "ok";
    }
```
```
  //在service层方法添加@TraceFun注解，既可以自动收集方法性能指标
    @TraceFun
    public String test2() throws Exception {
        Thread.sleep(1000);
        demoService2.test22();
        return "ok";
    }
```