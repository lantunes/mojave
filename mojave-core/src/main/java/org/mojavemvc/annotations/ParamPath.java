/*
 * Copyright (C) 2011-2013 Mojavemvc.org
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
 * A controller class's action methods can be annotated with this annotation to
 * specify the mapping of the method's parameters to the request path. This 
 * allows for cleaner application URLs.
 * </p> 
 * 
 * <p>
 * Parameters in the path must be prefixed with a colon. Also, there must be a 
 * matching parameter in the method signature annotated with {@link Param}. When 
 * an action method is obtaining its parameters through the request path, any
 * matching HTTP request parameters are ignored. Therefore, matching path parameters 
 * and request parameters cannot both be used when invoking an action method. 
 * As an example, consider the following action method:
 * </p>
 * 
 * <pre>
 * &#064;Action
 * &#064;ParamPath(&quot;to/:name&quot;)
 * public View sayHello(&#064;Param(&quot;name&quot;) String name) {
 *     return new JSP(&quot;hello&quot;);
 * }
 * </pre>
 * 
 * <p>
 * We can invoke this method by making the following request:
 * </p>
 * 
 * <pre>
 * http://.../some-controller/sayHello/to/John
 * </pre>
 * 
 * <p>
 * Path parameters can also be defined with custom regular expression definitions.
 * Take the following action method as an example:
 * </p>
 * 
 * <pre>
 * &#064;Action
 * &#064;ParamPath(&quot;to/:name&lt;[a-z]+&gt;&quot;)
 * public View sayHello(&#064;Param(&quot;name&quot;) String name) {
 *     return new JSP(&quot;hello&quot;);
 * }
 * </pre>
 * 
 * <p>
 * The value inside the angle brackets, next to &quot;:name&quot;, is a custom
 * regular expression definition. When no such definition exists, the regular
 * expression <b>[^/]+</b> is used by default. When a custom definition exists,
 * it will be used to match an incoming path to the route. Thus, the following
 * request will <b>not</b> match the action method above:
 * </p>
 * 
 * <pre>
 * http://.../some-controller/sayHello/to/123
 * </pre>
 * 
 * @author Luis Antunes
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ParamPath {
    String value();
}
