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

import java.io.InputStream;

import org.mojavemvc.views.View;

/**
 * <p>
 * Implementors of this interface provide a means
 * for marshalling objects from a request body to
 * an &#064;Action method parameter, and to a View 
 * from an &#064;Action method return value.
 * </p>
 * 
 * <p>
 * Implementors must provide a no-arg constructor,
 * and ensure that the implementation is thread-safe,
 * as only a single instance is likely to exist for 
 * all requests of the associated content types.
 * </p>
 * 
 * @author Luis Antunes
 */
public interface EntityMarshaller {

    /**
     * If an &#064;Action method returns a non-View value,
     * the framework will invoke this method to convert the 
     * value to a View.
     * 
     * @param entity the instance of the &#064;Action method return value
     * @return a View
     */
    View marshall(Object entity);
    
    /**
     * If an &#064;Action method contains an entity parameter
     * that must be marshalled from the request body, the 
     * framework will invoke this method to convert the 
     * request body to an instance of the entity parameter.
     * 
     * @param in the request body InputStream
     * @param type the &#064;Action method parameter type
     * @return an instance of the &#064;Action method parameter
     */
    <T> T unmarshall(InputStream in, Class<T> type);
    
    /**
     * A marshaller is designated for one or more content types.
     * For example, a JSON marshaller would return 
     * &quot;application/json&quot;. 
     * 
     * @return the content types handled by this marshaller
     */
    String[] contentTypesHandled();
}
