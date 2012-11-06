## Mojave MVC Web Framework

========================

Mojave MVC is an annotation-driven, POJO-based web application development framework for Java. 
It borrows ideas from Spring Web MVC and EJB 3.1, and incorporates Guice. It attempts to:

* facilitate TDD and Dependency Injection, and ultimately the development of decoupled components, by providing IoC capabilities
* remove concurrency concerns from web application development by providing a single-thread programming model
* remove cross-cutting concerns from web application development by supporting AOP through the interceptor pattern
* be as minimally intrusive a framework as possible; it tries to get out of your way by minimizing framework-related metadata and boilerplate code

Mojave MVC incorporates the Google Guice framework. All user components in the application can 
be configured with injectable dependencies, through use of the standard @Inject annotation. 
Injection is configured through user-supplied Guice Modules, using only idiomatic Java.

# Getting Started
===================

Download the Mojave jar, which includes all dependencies:

http://mojavemvc.org/downloads

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
import org.mojavemvc.annotations.StatelessController;
import org.mojavemvc.views.JspView;
import org.mojavemvc.views.View;
 
@StatelessController
public class HelloWorld {
 
  @Action
  public View sayHello(@Param("name") String name) {
 
    return new JspView("hello.jsp").withAttribute("name", name);
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

    http://localhost:8080/app/serv/HelloWorld/sayHello?name=John
    
In a browser, you should see the following response:

    Hello John!

Learn more: http://mojavemvc.org/documentation