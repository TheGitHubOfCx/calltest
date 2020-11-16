package com.example.calltest.service;

import com.example.calltest.kit.FreeMarker;
import com.example.calltest.kit.HttpKit;
import com.example.calltest.kit.WsdlResultParser;
import org.apache.http.client.config.RequestConfig;
import org.junit.Test;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TestService {

    private String payload = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
            "\n" +
            "<MESSAGE>\n" +
            "  <BODY>\n" +
            "    <ROWS>\n" +
            "      <ROW>\n" +
            "        <ID>${i!}</ID>\n" +
            "        <VALUE>测试值${i!}</VALUE>\n" +
            "      </ROW>\n" +
            "    </ROWS>\n" +
            "  </BODY>\n" +
            "</MESSAGE>\n";

    private String requestXmlTemplete = "<s11:Envelope xmlns:s11='http://schemas.xmlsoap.org/soap/envelope/'>\n" +
            "  <s11:Body>\n" +
            "    <ns1:service xmlns:ns1='http://ws.sie.com'>\n" +
            "      <param>" + "flow=${flow},app=${app}" + "</param>\n" +
            "      <data><![CDATA[${payload}]]></data>\n" +
            "    </ns1:service>\n" +
            "  </s11:Body>\n" +
            "</s11:Envelope>";

    private String requestXmlLocalTemplete = "<s11:Envelope xmlns:s11='http://schemas.xmlsoap.org/soap/envelope/'>\n" +
            "  <s11:Body>\n" +
            "    <ns1:wsTest xmlns:ns1='http://ws.calltest.com'>\n" +
            "<!-- optional -->\n" +
            "      <testMessage><![CDATA[${payload}]]></testMessage>\n" +
            "<!-- optional -->\n" +
            "      <timeSleep>${timeSleep}</timeSleep>\n" +
            "    </ns1:wsTest>\n" +
            "  </s11:Body>\n" +
            "</s11:Envelope>";

    public void test(String wsdl, int testMsgNum, String flow, String app) {
        String result;
        FreeMarker freeMarker = new FreeMarker();
        Map<String, Object> freeMarkerMap = new ConcurrentHashMap<>();
        freeMarkerMap.put("payload", payload);
        for (int i = 0; i < testMsgNum; i++) {
            HashMap<String, String> headers = new HashMap<>();
            headers.put("SOAPAction", "");
            freeMarker.setVariable("i", i);
            freeMarkerMap.put("flow", flow);
            freeMarkerMap.put("app", app);
            freeMarkerMap.put("payload", freeMarker.parseExpression(payload));
            freeMarker.setMap(freeMarkerMap);
            String requestXml = freeMarker.parseExpression(requestXmlTemplete);
            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectTimeout(3000)
                    .setConnectionRequestTimeout(65 * 1000)
                    .setSocketTimeout(65 * 1000).build();
            String soap = null;
            try {
//                Duration duration = Duration.ofSeconds(sleepTime);
//                Thread.sleep(duration.toMillis());
//                System.err.println("模拟业务处理时长,单位：秒:" + duration.getSeconds());

                long startTime = System.currentTimeMillis();
                soap = HttpKit.postBody(wsdl, headers, "text/xml", requestXml, requestConfig);
                long endTime = System.currentTimeMillis();
                System.err.println("ws真实调用时长,单位：毫秒:" + (endTime - startTime));

                result = WsdlResultParser.parse("return", soap);
                System.err.println("调用ws结果:" + result);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void testWsLocal(String wsdl, Integer testMsgNum, Integer timeSleep) {
        String result;
        FreeMarker freeMarker = new FreeMarker();
        Map<String, Object> freeMarkerMap = new ConcurrentHashMap<>();
        freeMarkerMap.put("payload", payload);
        for (int i = 0; i < testMsgNum; i++) {
            HashMap<String, String> headers = new HashMap<>();
            headers.put("SOAPAction", "");
            freeMarker.setVariable("i", i);
            freeMarkerMap.put("payload", freeMarker.parseExpression(payload));
            freeMarker.setVariable("timeSleep", timeSleep);
            freeMarker.setMap(freeMarkerMap);
            String requestXml = freeMarker.parseExpression(requestXmlLocalTemplete);
            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectTimeout(3000)
                    .setConnectionRequestTimeout(65 * 1000)
                    .setSocketTimeout(65 * 1000).build();
            String soap = null;
            try {
                soap = HttpKit.postBody(wsdl, headers, "text/xml", requestXml, requestConfig);
                result = WsdlResultParser.parse("return", soap);
                System.err.println("调用ws结果:" + result);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
