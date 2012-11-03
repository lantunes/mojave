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
 * A controller class or its methods can be annotated with this annotation 
 * to specify which interceptors will intercept &#064;Action method
 * invocations. The order of the interceptors specified is important, as 
 * they will be invoked in the sequence in which they are declared.
 * </p>
 * 
 * <p>
 * If a controller class is annotated with this 
 * annotation, all &#064;Action methods, and any &#064;DefaultAction
 * method, will be intercepted by the declared interceptor(s).
 * </p>
 * 
 * @author Luis Antunes
 */
@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.TYPE,ElementType.METHOD})
public @interface InterceptedBy {
	Class<?>[] value();
}
