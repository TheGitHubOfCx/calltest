package com.example.calltest.ws.impl;

import com.example.calltest.ws.service.TestWsService;

import javax.jws.WebParam;
import javax.jws.WebService;
import java.time.Duration;

@WebService(targetNamespace = "http://ws.calltest.com",
        endpointInterface = "com.example.calltest.ws.service.TestWsService",
        serviceName = "wsTest",
        portName = "wsTestPort")
public class TestWsServiceImpl implements TestWsService {

    @Override
    public Object wsTest(@WebParam(name = "testMessage") String message, @WebParam(name = "timeSleep") Integer timeSleep) {
        //模拟业务处理
        try {
            Duration duration = Duration.ofSeconds(timeSleep);
            Thread.sleep(duration.toMillis());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "调用成功\n" + message;
    }

}
