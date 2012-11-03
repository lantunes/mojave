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
 * This annotation specifies the default action for a controller. There can be
 * only one method with this annotation in a controller. A method annotated with
 * this annotation must be public. Generally, a default action behaves like an
 * &#064;Action method, in that it must return a View, and can accept web
 * parameters. (See {@link Action} for more information.) However, it is invoked
 * only if no action variable is specified in the request.
 * </p>
 * 
 * <p>
 * As an example, consider the following controller:
 * </p>
 * 
 * <pre>
 * &#064;StatelessController(&quot;test&quot;)
 * public class SomeController {
 * 
 *     &#064;DefaultAction
 *     public View defaultAction() {
 * 
 *         return new View(&quot;test.jsp&quot;);
 *     }
 * }
 * </pre>
 * 
 * <p>
 * We can invoke the default action above by making the following request:
 * </p>
 * 
 * <pre>
 * http://...?cntrl=test
 * </pre>
 * 
 * <p>
 * Note that we cannot invoke the default action method by supplying
 * "actn=defaultAction" in the query string above.
 * </p>
 * 
 * @author Luis Antunes
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface DefaultAction {

}
