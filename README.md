## Mojave MVC Web Framework

========================

Mojave (pronounced Mo-hav-ee) is an annotation-driven, POJO-based web application development framework for Java. 
It borrows ideas from Spring Web MVC, JAX-RS and EJB 3.1, and incorporates Guice. It attempts to:

* facilitate TDD and Dependency Injection, and ultimately the development of decoupled components, by providing IoC capabilities
* remove concurrency concerns from web application development by providing a single-thread programming model
* remove cross-cutting concerns from web application development by supporting AOP through the interceptor pattern
* be as minimally intrusive a framework as possible; it tries to get out of your way by minimizing framework-related metadata and boilerplate code

Mojave also:

* supports building RESTful applications: controller actions can be bound to specific HTTP methods and URIs
* supports JSP out-of-the-box
* is small and lightweight
* has very high test coverage, making the framework easy to change

Mojave MVC incorporates the Google Guice framework. All user components in the application can 
be configured with injectable dependencies, through use of the standard @Inject annotation. 
Injection is configured through user-supplied Guice Modules, using only idiomatic Java. Mojave also works 
out-of-the-box on Google App Engine and AWS Elastic Beanstalk.

# Getting Started
===================

Download the Mojave jar, which includes all dependencies:

http://mojavemvc.org/downloads

Or, add the following dependency to your pom.xml:

```xml
<dependency>
  <groupId>org.mojavemvc</groupId>
  <artifactId>mojave-core</artifactId>
  <version>1.0.6</version>
</dependency>
```

Create a Java web application project, and add the downloaded jar to the WEB-INF/lib folder. Also,
add the following web.xml:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app id="WebApp_ID" version="2.4" 
xmlns="http://java.sun.com/xml/ns/j2ee" 
xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
xsi:schemaLocation="http://java.sun.com/xml/ns/javaee 
  http://java.sun.com/xml/ns/javaee/web-app_2_5.xsd">
 
  <display-name>HelloWorld</display-name>
 
  <servlet>
    <servlet-name>FrontController</servlet-name>
    <servlet-class>org.mojavemvc.FrontController</servlet-class>
    <init-param>
      <param-name>controller-classes</param-name>
      <param-value>helloworld.controllers</param-value>
    </init-param>
    <init-param>
      <param-name>jsp-path</param-name>
      <param-value>/WEB-INF/jsp/</param-value>
    </init-param>
    <load-on-startup>1</load-on-startup>
  </servlet>
  <servlet-mapping>
    <servlet-name>FrontController</servlet-name>
    <url-pattern>/serv/*</url-pattern>
  </servlet-mapping>
   
</web-app>
```

Next, create a controller class:

```java
package helloworld.controllers;
 
import org.mojavemvc.annotations.Action;
import org.mojavemvc.annotations.Param;
import org.mojavemvc.annotations.ParamPath;
import org.mojavemvc.annotations.StatelessController;
import org.mojavemvc.views.JSP;
import org.mojavemvc.views.View;
 
@StatelessController
public class HelloWorld {
 
  @Action
  @ParamPath("to/:name")
  public View sayHello(@Param("name") String name) {
 
    return new JSP("hello").withAttribute("name", name);
  }
}
```

Finally, create a JSP file, and add it to WEB-INF/jsp:

```jsp
<html>
  <body>
    <p>Hello <%=request.getAttribute("name") %>!</p>
  </body>
</html>
```

Deploy your war file (assuming it's called app.war), and in a browser, go to:

    http://localhost:8080/app/serv/HelloWorld/sayHello/to/John
    
In a browser, you should see the following response:

    Hello John!

Mojave supports dependency injection through the Google Guice framework. Users specify resources that are to be injected using the @Inject 
annotation, as they would normally in any Guice-enabled application. To wire objects together, and to configure injection, users provide 
their own modules, which extend Guice's AbstractModule. Finally, users report the location of their modules to the Mojave framework 
through an init param.

To illustrate the use of a controller with injected dependencies, consider the following class:

```java
package injected.controllers;
 
import org.mojavemvc.annotations.Action;
import org.mojavemvc.annotations.Param;
import org.mojavemvc.annotations.StatelessController;
import org.mojavemvc.views.JSP;
import org.mojavemvc.views.View;
 
import com.google.inject.Inject;
 
@StatelessController
public class InjectedController {
 
  private final SomeService service;
 
  @Inject
  public InjectedController(SomeService service) {
 
    this.service = service;
  }
 
  @Action
  public View registerName(@Param("name") String name) {
 
    service.register(name);
    return new JSP("index");
  }
}
```

Learn more: http://mojavemvc.org/documentation