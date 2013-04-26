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

import org.mojavemvc.views.JSON;
import org.mojavemvc.views.View;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Luis Antunes
 */
public class JSONEntityMarshaller implements EntityMarshaller {
    
    /*
     * ObjectMapper is thread-safe
     */
    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public View marshall(Object entity) {
        return new JSON(entity);
    }

    @Override
    public <T> T unmarshall(InputStream in, Class<T> type) {

        try {
            return mapper.readValue(in, type);
        } catch (Exception e) {
            throw new RuntimeException("could not construct JSON View", e);
        }
    }

    @Override
    public String[] contentTypesHandled() {
        return new String[]{"application/json"};
    }
}
