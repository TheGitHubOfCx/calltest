package com.example.calltest.ws.api;

import com.alibaba.fastjson.JSONObject;
import com.predic8.wsdl.Definitions;
import com.predic8.wsdl.WSDLParser;
import com.predic8.wstool.creator.RequestTemplateCreator;
import com.predic8.wstool.creator.SOARequestCreator;
import groovy.xml.MarkupBuilder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.StringWriter;

@RestController
public class WsController {

    @RequestMapping(value = "/getWsInfo", method = RequestMethod.POST)
    public String getWsRequestXml(@RequestBody JSONObject jsonObject) {
        String url = (String) jsonObject.get("url");
        String portTypeName = (String) jsonObject.get("portTypeName");
        String operationName = (String) jsonObject.get("operationName");
        String bindingName = (String) jsonObject.get("bindingName"); //operationName+"SoapBinding"
        WSDLParser parser = new WSDLParser();
        Definitions definitions = parser.parse(url);
        StringWriter writer = new StringWriter();
        SOARequestCreator creator = new SOARequestCreator(definitions, new RequestTemplateCreator(), new MarkupBuilder(writer));
        creator.createRequest(portTypeName, operationName, bindingName);
        return writer.toString();
    }
}
