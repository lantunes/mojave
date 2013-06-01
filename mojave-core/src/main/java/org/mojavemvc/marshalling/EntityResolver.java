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
package org.mojavemvc.marshalling;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.mojavemvc.annotations.Marshall;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class provides a means of resolving entities for
 * marshalling; that is, it checks if the entity is a 
 * {@link Marshallable} or if it contains a method
 * annotated with {@link org.mojavemvc.annotations.Marshall}.
 * 
 * @author Luis Antunes
 */
public class EntityResolver {
    
    private static final Logger logger = LoggerFactory.getLogger("org.mojavemvc");

    /*
     * A cache for methods annotated with org.mojavemvc.annotations.Marshall
     * - the reflection operation of searching for a declared method 
     *   annotated with org.mojavemvc.annotations.Marshall is a potential
     *   performance bottleneck; this cache is supposed to address that issue
     */
    private final Map<Class<? extends Object>, Method> marshallMethodMap = 
            new HashMap<Class<? extends Object>, Method>();
    
    public Object resolve(Object entity) {
        
        if (entity == null) return entity;
        
        Class<? extends Object> entityClass = entity.getClass();
        
        if (Marshallable.class.isAssignableFrom(entityClass)) {
            return ((Marshallable<?>)entity).getEntity();
        }
        
        if (!marshallMethodMap.containsKey(entityClass)) {
            cacheMarshallMethod(entityClass);
        }
        
        Method marshallMethod = marshallMethodMap.get(entityClass);
        if (marshallMethod != null) {
            return invokeMarshallMethod(marshallMethod, entity, entityClass.getName());
        }
        
        return entity;
    }

    private Object invokeMarshallMethod(Method marshallMethod, Object entity, 
            String entityClassName) {
        
        try {
            
            Object o = marshallMethod.invoke(entity);
            return o;
            
        } catch (Exception e) {
            logger.error("error invoking method " + marshallMethod.getName() + 
                    " annotated with @" + Marshall.class.getSimpleName() + 
                    " in class " + entityClassName, e);
            return null;
        }
    }

    private void cacheMarshallMethod(Class<? extends Object> entityClass) {
        
        Method[] methods = entityClass.getDeclaredMethods();
        Method marshallMethod = null;
        for (Method method : methods) {
            Annotation ann = method.getAnnotation(Marshall.class);
            if (ann != null) {
                marshallMethod = method;
                break;
            }
        }
        marshallMethodMap.put(entityClass, marshallMethod);
    }
}
