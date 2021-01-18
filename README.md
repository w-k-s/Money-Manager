# Money Manager

An excuse to get myself familiar with SOAP

## 1. Lessons Learnt

### 1.1 Endpoint mappings

- An `EndpointMapping` delivers a `EndpointInvocationChain`, which contains the endpoint that matches the incoming request, and may also contain a list of endpoint interceptors that will be applied to the request and response. 

-  When a request comes in, the `MessageDispatcher` will hand it over to the endpoint mapping to let it inspect the request and come up with an appropriate `EndpointInvocationChain`. Then the MessageDispatcher will invoke the endpoint and any interceptors in the chain

- There are some endpoint mappings that are enabled out of the box, for example, the `PayloadRootAnnotationMethodEndpointMapping` or the `SoapActionAnnotationMethodEndpointMapping`

- The `PayloadRootAnnotationMethodEndpointMapping` uses the `@PayloadRoot` annotation, with the localPart and namespace elements, to mark methods with a particular qualified name. Whenever a message comes in which has this qualified name for the payload root element, the method will be invoked.

- the `SoapActionAnnotationMethodEndpointMapping` uses the `@SoapAction` annotation to mark methods with a particular SOAP Action. Whenever a message comes in which has this SOAPAction header, the method will be invoked. 

[Reference](https://docs.spring.io/spring-ws/site/reference/html/server.html#server-endpoint-mapping)