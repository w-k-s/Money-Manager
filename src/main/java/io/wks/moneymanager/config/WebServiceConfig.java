package io.wks.moneymanager.config;

import com.google.common.collect.ImmutableMap;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.server.EndpointInterceptor;
import org.springframework.ws.server.endpoint.interceptor.PayloadLoggingInterceptor;
import org.springframework.ws.soap.security.xwss.callback.SimplePasswordValidationCallbackHandler;
import org.springframework.ws.soap.security.xwss.XwsSecurityInterceptor;
import org.springframework.ws.soap.server.endpoint.interceptor.PayloadValidatingInterceptor;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static io.wks.moneymanager.constants.Constants.NAMESPACE_URI;
import static io.wks.moneymanager.constants.Constants.RESOURCE_PATH_SCHEMA;

@EnableWs
@Configuration
public class WebServiceConfig extends WsConfigurerAdapter {

    @Bean
    public ServletRegistrationBean messageDispatcherServlet(ApplicationContext applicationContext) {
        MessageDispatcherServlet servlet = new MessageDispatcherServlet();
        servlet.setApplicationContext(applicationContext);
        servlet.setTransformWsdlLocations(true); // updates host of webservice in wsdl (i.e. localhost or server host name)
        return new ServletRegistrationBean(servlet, "/ws/*");
    }

    /**
     * Bean name sets path for wsdl
     * i.e. http://localhost:8080/ws/transactions.wsdl
     */
    @Bean(name = "transactionz")
    public DefaultWsdl11Definition defaultWsdl11Definition(XsdSchema transactionsSchema) {
        /**
         * DefaultWsdl11Definition expects all Request object to be suffixed with 'Request'
         * and all Response objects to be suffixed with 'Response',
         * otherwise an incorrect WSDL will be generated.
         */
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("RecordTransaction");
        wsdl11Definition.setLocationUri("/ws");
        wsdl11Definition.setTargetNamespace(NAMESPACE_URI);
        wsdl11Definition.setSchema(transactionsSchema);
        return wsdl11Definition;
    }

    @Bean
    public XsdSchema transactionsSchema() {
        return new SimpleXsdSchema(new ClassPathResource(RESOURCE_PATH_SCHEMA));
    }

    @Bean
    PayloadLoggingInterceptor payloadLoggingInterceptor() {
        return new PayloadLoggingInterceptor();
    }

    @Bean
    PayloadValidatingInterceptor payloadValidatingInterceptor() {
        final PayloadValidatingInterceptor payloadValidatingInterceptor = new PayloadValidatingInterceptor();
        payloadValidatingInterceptor.setSchema(new ClassPathResource(RESOURCE_PATH_SCHEMA));
        return payloadValidatingInterceptor;
    }

    @Bean
    XwsSecurityInterceptor securityInterceptor() {
        XwsSecurityInterceptor securityInterceptor = new XwsSecurityInterceptor();
        securityInterceptor.setCallbackHandler(callbackHandler());
        securityInterceptor.setPolicyConfiguration(new ClassPathResource("securityPolicy.xml"));
        return securityInterceptor;
    }

    @Bean
    SimplePasswordValidationCallbackHandler callbackHandler() {
        //
        // This is import org.springframework.ws.soap.security.xwss.callback.SimplePasswordValidationCallbackHandler;
        // Not to be confused with import org.springframework.ws.soap.security.wss4j2.callback.SimplePasswordValidationCallbackHandler;
        //
        SimplePasswordValidationCallbackHandler callbackHandler = new SimplePasswordValidationCallbackHandler();
        callbackHandler.setUsersMap(Collections.singletonMap("admin","password"));
        return callbackHandler;
    }

    @Override
    public void addInterceptors(List<EndpointInterceptor> interceptors) {
        interceptors.add(payloadLoggingInterceptor());
        interceptors.add(payloadValidatingInterceptor());
        interceptors.add(securityInterceptor());
    }
}
