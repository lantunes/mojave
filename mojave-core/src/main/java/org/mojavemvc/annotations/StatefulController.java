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
 * Only one instance of this controller will exist per request session. It is
 * kept in the HttpSession. The instance goes away when the session is
 * invalidated, the session times out, or the application is restarted. Its
 * dependencies, if any, are re-injected with every new request.
 * </p>
 * 
 * <p>
 * The following are valid declarations of stateful controllers:
 * </p>
 * 
 * <pre>
 * &#064;StatefulController
 * public class SomeController {
 *  ...
 * }
 * </pre>
 * 
 * <pre>
 * &#064;StatefulController("some-name")
 * public class SomeController {
 *  ...
 * }
 * </pre>
 * 
 * @author Luis Antunes
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface StatefulController {
    String value() default "";
}
