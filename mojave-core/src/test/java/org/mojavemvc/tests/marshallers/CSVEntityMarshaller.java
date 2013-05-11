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
package org.mojavemvc.tests.marshallers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;

import org.mojavemvc.marshalling.EntityMarshaller;
import org.mojavemvc.views.View;

/**
 * 
 * @author Luis Antunes
 */
public class CSVEntityMarshaller implements EntityMarshaller {

    @Override
    public View marshall(Object entity) {
        return new CSV(entity);
    }

    @Override
    public <T> T unmarshall(InputStream in, Class<T> type) {
        
        String[] content = readTextFromStream(in);
        return newEntityInstance(type, content);
    }

    private String[] readTextFromStream(InputStream in) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buff = new byte[1024];
            int len = 0;
            while ((len = in.read(buff)) != -1) {
                out.write(buff, 0, len);
            }
            String content = new String(out.toByteArray());
            return content.split(",");
        } catch (IOException e) {
            throw new RuntimeException("error unmarshalling csv text: " +
                    "error reading from request stream", e);
        }
    }
    
    private <T> T newEntityInstance(Class<T> type, String[] content) {
        T entity = null;
        try {
            
            Constructor<T> constructor = type.getConstructor(String[].class);
            entity = constructor.newInstance(new Object[]{content});
            
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("error unmarshalling csv text: " +
                    "entity does not contain a constructor that accepts a String[]", e);
        } catch (Exception e) {
            throw new RuntimeException("error unmarshalling csv text: " +
                    "error invoking entity constructor", e);
        }
        return entity;
    }

    @Override
    public String[] contentTypesHandled() {
        return new String[]{"text/csv"};
    }

}
