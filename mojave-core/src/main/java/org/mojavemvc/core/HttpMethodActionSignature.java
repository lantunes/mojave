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
package org.mojavemvc.core;

import java.lang.annotation.Annotation;
import java.util.List;

import org.mojavemvc.marshalling.EntityMarshaller;

/**
 * 
 * @author Luis Antunes
 */
public class HttpMethodActionSignature extends BaseActionSignature {

    private final HttpMethod httpMethod;

    public HttpMethodActionSignature(HttpMethod httpMethod, int fastIndex, String methodName, Class<?>[] paramTypes,
            Annotation[][] paramAnnotations, Annotation[] methodAnnotations) {

        super(fastIndex, methodName, paramTypes, paramAnnotations, methodAnnotations);
        this.httpMethod = httpMethod;
    }
    
    public HttpMethodActionSignature(HttpMethod httpMethod, int fastIndex, String methodName, Class<?>[] paramTypes,
            Annotation[][] paramAnnotations, Annotation[] methodAnnotations, 
            EntityMarshaller paramMarshaller, EntityMarshaller viewMarshaller) {

        super(fastIndex, methodName, paramTypes, paramAnnotations, methodAnnotations, 
                paramMarshaller, viewMarshaller);
        this.httpMethod = httpMethod;
    }

    @Override
    public List<Class<?>> getInterceptorClasses(ControllerDatabase controllerDb, Class<?> controllerClass, String action) {

        return controllerDb.getInterceptorsForHttpMethodAction(controllerClass, httpMethod);
    }
}
