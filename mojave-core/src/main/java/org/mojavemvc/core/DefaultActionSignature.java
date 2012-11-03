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
package org.mojavemvc.core;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * 
 * @author Luis Antunes
 */
public class DefaultActionSignature extends BaseActionSignature {

    public DefaultActionSignature(int fastIndex, String methodName, Class<?>[] paramTypes,
            Annotation[][] paramAnnotations) {

        super(fastIndex, methodName, paramTypes, paramAnnotations);
    }

    @Override
    public List<Class<?>> getInterceptorClasses(ControllerDatabase controllerDb, Class<?> controllerClass, String action) {

        return controllerDb.getInterceptorsForDefaultAction(controllerClass);
    }
}
