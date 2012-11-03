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

Learn more: http://mojavemvc.org