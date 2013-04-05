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
 * A controller annotated with this annotation is flagged as the default
 * controller for all application requests not specifying a controller. Only one
 * controller in the application can be annotated with this annotation.
 * </p>
 * 
 * <p>
 * As an example, consider the following controller:
 * </p>
 * 
 * <pre>
 * &#064;DefaultController
 * &#064;StatelessController(&quot;test&quot;)
 * public class SomeController {
 * 
 *     &#064;DefaultAction
 *     public View someAction() {
 *         return new JSP(&quot;test&quot;);
 *     }
 * }
 * </pre>
 * 
 * <p>
 * We can invoke the someAction method by making the following request:
 * </p>
 * 
 * <pre>
 * http://.../
 * </pre>
 * 
 * <p>
 * Since there was no controller name supplied, the default controller was
 * used as the controller for the request.
 * </p>
 * 
 * @author Luis Antunes
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DefaultController {

}
