package com.example.calltest.controller;

import com.example.calltest.kit.ResponseResult;
import com.example.calltest.monitor.Cpu;
import com.example.calltest.monitor.SystemHardwareInfo;
import com.example.calltest.service.TestService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class TestSysController {

    @Autowired
    TestService testService;

    @Autowired
    SystemHardwareInfo systemHardwareInfo;

    @RequestMapping("/testWs")
    public ResponseResult testWs(@RequestParam Map param) {
        String wsdl = String.valueOf(param.get("wsdl"));
        int testMsgNum = Integer.valueOf(String.valueOf(param.get("num")));
        String flow = String.valueOf(param.get("flow"));
        String app = String.valueOf(param.get("app"));
//        Integer sleepTime = Integer.valueOf(String.valueOf(param.get("sleepTime")));
        testService.test(wsdl, testMsgNum, flow, app);
        return ResponseResult.success("调用成功", wsdl);
    }

    @RequestMapping("/testWsLocal")
    public ResponseResult testWsLocal(@RequestParam Map param) {
        String wsdl = String.valueOf(param.get("wsdl"));
        Integer testMsgNum = Integer.valueOf(String.valueOf(param.get("num")));
        Integer timeSleep = Integer.valueOf(String.valueOf(param.get("timeSleep")));
        testService.testWsLocal(wsdl, testMsgNum, timeSleep);
        return ResponseResult.success("调用成功", wsdl);
    }

    @RequestMapping("/getSysInfo")
    public Map<String, Object> getSysInfo() {
        return systemHardwareInfo.copyTo();
    }

    @Test
    public void getLocalIp(){
        InetAddress addr = null;
        try {
            addr = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        System.out.println("Local HostAddress: "+addr.getHostAddress());
        String hostname = addr.getHostName();
        System.out.println("Local host name: "+hostname);
    }


}
