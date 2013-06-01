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
 * This annotation is used to flag a method's return
 * value as the entity that is to be marshalled by
 * a marshaller. The class containing the method 
 * annotated with this annotation is not marshalled
 * itself.
 * <p/>
 * 
 * <p>
 * NOTE: Methods annotated with this annotation must return
 * the entity that is to be marshalled, and must not
 * require any parameters. Only the first method 
 * encountered with this annotation in a class will be
 * invoked.
 * </p>
 * 
 * @author Luis Antunes
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Marshall {

}
