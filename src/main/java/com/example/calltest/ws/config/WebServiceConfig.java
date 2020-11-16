package com.example.calltest.ws.config;

import com.example.calltest.ws.impl.TestWsServiceImpl;
import com.example.calltest.ws.service.TestWsService;
import org.apache.cxf.Bus;
import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.xml.ws.Endpoint;

@Configuration
public class WebServiceConfig {

    //开放servlet功能，处理发过来的http请求
    @Bean
    public ServletRegistrationBean getServletSupport() {
        return new ServletRegistrationBean(new CXFServlet(), "/wsTest/*");
    }

    //注册CXF的总线BUS
    @Bean(name = Bus.DEFAULT_BUS_ID)
    public SpringBus bus() {
        return new SpringBus();
    }


    @Bean
    public TestWsService getTestWs() {
        return new TestWsServiceImpl();
    }

    @Bean
    public Endpoint getEndPoint() {
        EndpointImpl endpoint = new EndpointImpl(bus(), getTestWs());
        endpoint.publish("/say");
        return endpoint;
    }


}
