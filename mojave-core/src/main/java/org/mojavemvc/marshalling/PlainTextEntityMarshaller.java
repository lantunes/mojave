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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;

import org.mojavemvc.views.PlainText;
import org.mojavemvc.views.View;

/**
 * @author Luis Antunes
 */
public class PlainTextEntityMarshaller implements EntityMarshaller {

    private final EntityResolver entityResolver = new EntityResolver();
    
    @Override
    public View marshall(Object entity) {
        entity = entityResolver.resolve(entity);
        return new PlainText(entity);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T unmarshall(InputStream in, Class<T> type) {
        
        String content = readTextFromStream(in);
        
        if (type.equals(String.class)) {
            return (T)content;
        }
        
        return newEntityInstance(type, content);
    }

    private String readTextFromStream(InputStream in) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buff = new byte[1024];
            int len = 0;
            while ((len = in.read(buff)) != -1) {
                out.write(buff, 0, len);
            }
            return new String(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("error unmarshalling plain text: " +
                    "error reading from request stream", e);
        }
    }
    
    private <T> T newEntityInstance(Class<T> type, String content) {
        T entity = null;
        try {
            
            Constructor<T> constructor = type.getConstructor(String.class);
            entity = constructor.newInstance(content);
            
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("error unmarshalling plain text: " +
                    "entity does not contain a constructor that accepts a String", e);
        } catch (Exception e) {
            throw new RuntimeException("error unmarshalling plain text: " +
                    "error invoking entity constructor", e);
        }
        return entity;
    }
    
    @Override
    public String[] contentTypesHandled() {
        return new String[]{"text/plain"};
    }
}
