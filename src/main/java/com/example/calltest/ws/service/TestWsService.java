package com.example.calltest.ws.service;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.ws.BindingType;
import javax.xml.ws.soap.SOAPBinding;

@WebService(targetNamespace = "http://ws.calltest.com")
@BindingType(value = SOAPBinding.SOAP12HTTP_BINDING)
public interface TestWsService {

    @WebMethod
    Object wsTest(@WebParam(name = "testMessage") String message,@WebParam(name = "timeSleep") Integer timeSleep);
}
