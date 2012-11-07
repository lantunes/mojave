/*
 * Copyright (C) 2011-2012 Mojavemvc.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mojavemvc.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * A controller class's exposed methods are annotated with this annotation to
 * flag them as action methods. A request always invokes a controller action
 * method (unless it is intercepted by an interceptor). It's value is the value
 * of the action parameter sent in the request. It's value is optional. If no
 * value is specified, the method's name will be used as its value.
 * </p>
 * 
 * <p>
 * As an example, consider the following method (we'll assume it is declared in
 * the context of a controller class, called "some-controller"):
 * </p>
 * 
 * <pre>
 * &#064;Action
 * public View sayHello() {
 *     return new View(&quot;hello.jsp&quot;);
 * }
 * </pre>
 * 
 * <p>
 * We can invoke this method by making the following request:
 * </p>
 * 
 * <pre>
 * http://.../some-controller/sayHello
 * </pre>
 * 
 * <p>
 * If we provide a value to the annotation, then we specify the name of action
 * that is referred to in the request parameter:
 * </p>
 * 
 * <pre>
 * &#064;Action(&quot;say-hello&quot;)
 * public View sayHello() {
 *     return new View(&quot;hello.jsp&quot;);
 * }
 * </pre>
 * 
 * <p>
 * We can invoke the above method by making the following request:
 * </p>
 * 
 * <pre>
 * http://.../some-controller/say-hello
 * </pre>
 * 
 * <p>
 * A method annotated with this annotation must be public. It must also return a
 * View. If it does not, the application will throw an Exception during the
 * initialization of the application in the servlet container. An Exception will
 * also be thrown if it returns a null View.
 * </p>
 * 
 * <p>
 * Action methods can also accept parameters. Please see {@link Param} for more
 * information.
 * </p>
 * 
 * @author Luis Antunes
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Action {
    String value() default "";
}
