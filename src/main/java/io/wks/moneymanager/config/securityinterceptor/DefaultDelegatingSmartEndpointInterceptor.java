package io.wks.moneymanager.config.securityinterceptor;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.endpoint.MethodEndpoint;
import org.springframework.ws.soap.SoapHeaderElement;
import org.springframework.ws.soap.security.AbstractWsSecurityInterceptor;
import org.springframework.ws.soap.server.SmartSoapEndpointInterceptor;

import javax.xml.namespace.QName;
import java.util.*;
import java.util.stream.Stream;

public class DefaultDelegatingSmartEndpointInterceptor implements SmartSoapEndpointInterceptor, InitializingBean {
    private static final QName WS_SECURITY_NAME = new QName("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "Security");

    private final List<Class<?>> permitAllEndpoints;
    private final Map<Class<?>, AbstractWsSecurityInterceptor> securedEndpoints;
    private final AbstractWsSecurityInterceptor defaultInterceptor;

    private DefaultDelegatingSmartEndpointInterceptor(Builder builder) {
        Objects.requireNonNull(builder);
        this.permitAllEndpoints = Objects.requireNonNullElse(
                builder.getPermitAllEndpoints(),
                Collections.emptyList()
        );
        this.securedEndpoints = Objects.requireNonNullElse(
                builder.getSecuredEndpoints(),
                Collections.emptyMap()
        );
        this.defaultInterceptor = builder.getDefaultInterceptor();
    }

    @Override
    public boolean understands(SoapHeaderElement soapHeaderElement) {
        return WS_SECURITY_NAME.equals(soapHeaderElement.getName());
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Stream.concat(this.securedEndpoints.values().stream(), Stream.of(this.defaultInterceptor))
                .filter(Objects::nonNull)
                .filter(interceptor -> interceptor instanceof InitializingBean)
                .map(interceptor -> (InitializingBean) interceptor)
                .forEach(bean -> {
                    try {
                        bean.afterPropertiesSet();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    @Override
    public boolean shouldIntercept(MessageContext messageContext, Object endpoint) {
        return !permitAllEndpoints.contains(getMethodEndpointClass(endpoint));
    }

    @Override
    public boolean handleRequest(MessageContext messageContext, Object o) throws Exception {
        return getMethodEndpointInterceptor(getMethodEndpointClass(o))
                .handleRequest(messageContext, o);
    }

    @Override
    public boolean handleResponse(MessageContext messageContext, Object o) throws Exception {
        return getMethodEndpointInterceptor(getMethodEndpointClass(o))
                .handleResponse(messageContext, o);
    }

    @Override
    public boolean handleFault(MessageContext messageContext, Object o) throws Exception {
        return getMethodEndpointInterceptor(getMethodEndpointClass(o))
                .handleFault(messageContext, o);
    }

    @Override
    public void afterCompletion(MessageContext messageContext, Object o, Exception e) throws Exception {
        getMethodEndpointInterceptor(getMethodEndpointClass(o))
                .afterCompletion(messageContext, o, e);
    }

    private AbstractWsSecurityInterceptor getMethodEndpointInterceptor(Class<?> endpointClass) {
        var interceptor = Optional.ofNullable(securedEndpoints.get(endpointClass))
                .orElse(defaultInterceptor);
        if (interceptor == null) {
            throw new RuntimeException(String.format("No interceptor found for endpoint %s", endpointClass));
        }
        return interceptor;
    }

    private Class<?> getMethodEndpointClass(Object endpoint) {
        Objects.requireNonNull(endpoint);
        if (endpoint instanceof MethodEndpoint) {
            return ((MethodEndpoint) endpoint).getMethod().getDeclaringClass();
        }
        throw new RuntimeException(String.format("Endpoint %s is not a method endpoint", endpoint));
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class InterceptorAssignmentBuilder {
        private final Builder builder;
        private final AbstractWsSecurityInterceptor interceptor;

        private InterceptorAssignmentBuilder(Builder builder, AbstractWsSecurityInterceptor interceptor) {
            this.builder = builder;
            this.interceptor = interceptor;
        }

        public Builder to(Class<?>... endpoints) {
            Stream.of(endpoints)
                    .filter(Objects::nonNull)
                    .forEach(endpoint -> builder.securedEndpoints.put(endpoint, interceptor));
            return builder;
        }
    }

    public static class Builder {

        private List<Class<?>> permitAllEndpoints = new ArrayList<>();
        private final Map<Class<?>, AbstractWsSecurityInterceptor> securedEndpoints = new HashMap<>();
        private AbstractWsSecurityInterceptor defaultInterceptor = null;

        private Builder() {
        }

        public Builder permitAllEndpoints(Class<?>... endpoints) {
            this.permitAllEndpoints = Arrays.asList(endpoints);
            return this;
        }

        public Builder defaultInterceptor(AbstractWsSecurityInterceptor interceptor) {
            this.defaultInterceptor = interceptor;
            return this;
        }

        public InterceptorAssignmentBuilder assignInterceptor(AbstractWsSecurityInterceptor interceptor) {
            return new InterceptorAssignmentBuilder(this, interceptor);
        }

        public DefaultDelegatingSmartEndpointInterceptor build() {
            return new DefaultDelegatingSmartEndpointInterceptor(this);
        }

        private List<Class<?>> getPermitAllEndpoints() {
            return permitAllEndpoints;
        }

        private Map<Class<?>, AbstractWsSecurityInterceptor> getSecuredEndpoints() {
            return securedEndpoints;
        }

        private AbstractWsSecurityInterceptor getDefaultInterceptor() {
            return defaultInterceptor;
        }
    }
}
