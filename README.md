# Money Manager

An excuse to get myself familiar with SOAP

## 1. Project Goals

- [x] Learn how to make SOAP endpoints; how does implementing SOAP compare to REST?
- [x] Use Casbin for Attribute based Authentication (ABAC)
- [x] Use ~~Couchbase~~ DynamoDB as the NoSQL store
- [x] Learn to write some simple bash scripts
- [ ] Try to use Token Authentication for SOAP (like [Sabre](https://developer.sabre.com/guides/travel-agency/quickstart-guides/get-token)).
- [ ] Use ~~Auth0~~ Cognito as the IAM.

## 2. Lessons Learnt

### 2.1 SOAP

#### 2.1.1 WSDL structure

![WSDL Structure](./docs/wsdl-structure.png)

#### 2.1.2 Endpoint Mappings

- An `EndpointMapping` delivers a `EndpointInvocationChain`, which contains the endpoint that matches the incoming request, and may also contain a list of endpoint interceptors that will be applied to the request and response. 

-  When a request comes in, the `MessageDispatcher` will hand it over to the endpoint mapping to let it inspect the request and come up with an appropriate `EndpointInvocationChain`. Then the MessageDispatcher will invoke the endpoint and any interceptors in the chain

- There are some endpoint mappings that are enabled out of the box, for example, the `PayloadRootAnnotationMethodEndpointMapping` or the `SoapActionAnnotationMethodEndpointMapping`

- The `PayloadRootAnnotationMethodEndpointMapping` uses the `@PayloadRoot` annotation, with the localPart and namespace elements, to mark methods with a particular qualified name. Whenever a message comes in which has this qualified name for the payload root element, the method will be invoked.

- the `SoapActionAnnotationMethodEndpointMapping` uses the `@SoapAction` annotation to mark methods with a particular SOAP Action. Whenever a message comes in which has this SOAPAction header, the method will be invoked. 

[Reference](https://docs.spring.io/spring-ws/site/reference/html/server.html#server-endpoint-mapping)

### 2.2 DynamoDB

#### 2.2.1 Reserved keywords

- `DynamoDB` has an [extensive list](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/ReservedWords.html) of reserved words (including date) which can not be used as attribute names.
- AWS docs on Querying a Table states that you can do `WHERE` condition queries (e.g. `SQL query SELECT * FROM Music WHERE Artist='No One You Know'`) in the DynamoDB way, but with one important caveat:
    
  > You MUST specify an EQUALITY condition for the PARTITION key, and you can optionally provide another condition for the SORT key.
  
  Meaning you can only use key attributes with Query. Doing it in any other way would mean that DynamoDB would run a full scan for you which is NOT efficient - less efficient than using Global secondary indexes.

### 2.3 Couchbase

- It's actually quite poorly documented. I had trouble finding up-to-date documentation
- Couchbase wasn't able to invoke a all-args constructor using the document even though Mongo can - why?
- On the whole, I felt it lacked polish.

### 2.4 Casbin

- It's great! Just works :)
- Although, it's eco-system is largely centered around Go e.g. there is no mongo adapter for Java yet. I should write one.

## 3. Setting up the project

- To generate the schema objects, run `mvn clean compile`

## 4. Setting up the DynamoDB table

1. Log in to the AWS console
1. Navigate to the DynamoDB section
1. Click on Create Table
1. For the table name, enter `Finance`
1. For the Primary Key, enter `UUID` and select type `String`.
1. Add a sort key, enter `TransactionDate` and select type `String`.
1. Click on `Create Table`.
1. Once the table has been created, navigate to the `Indexes` tab.
1. Click on `Create Index`
1. For the Primary Key, enter `Category` and select type `String`. Name this index `CategoryIndex` and then click `Create Index`.
1. Click on `Create Index` again
1. For the Primary Key, enter `CreatedBy` and select type `String`. Name this index `CreatedByIndex` and then click `Create Index`.

## 5. References

- [How to implement security in SOAP webservice using Spring-WS](https://tutorialflix.com/How-to-implement-security-in-SOAP-webservice-using-Spring-WS/)
- [Chapter 7. Securing your Web services with Spring-WS](https://docs.spring.io/spring-ws/site/reference/html/security.html)
- [Fixing Security header not found when delegating to XwssSecurityInterceptor](https://stackoverflow.com/a/43733139)
- [Casbin](https://casbin.org/docs/en/get-started)
- [jCasbin](https://github.com/casbin/jcasbin)
- [jCasbin Spring Boot Starter](https://github.com/jcasbin/casbin-spring-boot-starter)
- [Bash Cheat Sheet](https://devhints.io/bash)