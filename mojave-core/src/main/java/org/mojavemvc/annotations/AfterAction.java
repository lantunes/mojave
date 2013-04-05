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
 * A method annotated with this annotation is invoked after the &#064;Action
 * method is invoked. There can be only one method with this annotation in a
 * controller or interceptor. An &#064;AfterAction method must accept no
 * arguments, or accept only a {@link org.mojavemvc.aop.RequestContext}
 * argument. Methods annotated with this annotation must not be annotated with
 * &#064;Action, &#064;DefaultAction, or &#064;InterceptedBy. An
 * &#064;AfterAction method must be public.
 * </p>
 * 
 * <p>
 * An &#064;AfterAction method can return any return type, but only a
 * {@link org.mojavemvc.views.View} return type will have any significance. If a
 * concrete View is returned, it overrides the View returned by the &#064;Action
 * method. However, if a null View is returned, then the View returned by the
 * &#064;Action method is used.
 * </p>
 * 
 * <p>
 * The following are valid uses of this annotation:
 * </p>
 * 
 * <pre>
 * &#064;AfterAction
 * public View doSomethingAfter() {
 *     return new View(&quot;after.jsp&quot;);
 * }
 * </pre>
 * 
 * <pre>
 * &#064;AfterAction
 * public View doSomethingAfter() {
 *     return null; // use the View returned by the @Action method
 * }
 * </pre>
 * 
 * <pre>
 * &#064;AfterAction
 * public void doSomethingAfter() {
 *     // do something after and use the View returned by the @Action method
 * }
 * </pre>
 * 
 * <p>
 * An &#064;AfterAction method can be used either in a controller or in an
 * interceptor class. For more information about interceptors, see
 * {@link org.mojavemvc.annotations.InterceptedBy}.
 * </p>
 * 
 * @author Luis Antunes
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AfterAction {

}
