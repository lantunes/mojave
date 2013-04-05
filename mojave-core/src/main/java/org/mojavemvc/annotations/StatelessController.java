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
 * A new instance of this controller is created per request. Its dependencies
 * are injected after creation.
 * </p>
 * 
 * <p>
 * The following are valid declarations of stateless controllers:
 * </p>
 * 
 * <pre>
 * &#064;StatelessController
 * public class SomeController {
 *  ...
 * }
 * </pre>
 * 
 * <pre>
 * &#064;StatelessController("some-name")
 * public class SomeController {
 *  ...
 * }
 * </pre>
 * 
 * <p>
 * NOTE: As a new instance of a stateless controller is created with every
 * request, the developer should not design the stateless controller with state.
 * That is, class fields should not be used with the intention of storing state
 * between requests, but they can be used for storing state in the object during
 * a request. For storing state between requests, use a
 * {@link StatefulController}.
 * </p>
 * 
 * @author Luis Antunes
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface StatelessController {
    String value() default "";
}
