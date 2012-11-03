/*
 * Copyright (C) 2011 Mojavemvc.org
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
 * A method annotated with this annotation will be invoked after the controller
 * is constructed. The method can take no arguments. An &#064;AfterContruct
 * method will not be intercepted if it, or its class, is annotated with
 * &#064;InterceptedBy. Any servlet resources, such as HttpServletRequest,
 * HttpServletResponse, and HttpSession, annotated with &#064;Inject will not
 * have been injected when the method is called, but other fields annotated with
 * &#064;Inject will have been injected. A method annotated with this annotation
 * must be public, and there can be only one occurrence in a class.
 * </p>
 * 
 * <p>
 * An &#064;AfterContruct method can be used in controllers only. Currently,
 * interceptors cannot have methods annotated with this annotation. Typically,
 * this annotation will be used together with the {@link Init} annotation, so
 * that users can perform their own activities during application
 * initialization. For example, consider the following controller:
 * </p>
 * 
 * <pre>
 * &#064;Init
 * &#064;SingletonController
 * public class InitController {
 * 
 *     &#064;AfterConstruct
 *     public void init() {
 *         // do some application-specific initialization
 *     }
 * }
 * </pre>
 * 
 * @author Luis Antunes
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AfterConstruct {

}
