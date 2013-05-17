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
package org.mojavemvc.tests;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.mojavemvc.marshalling.DefaultEntityMarshaller;
import org.mojavemvc.marshalling.JSONEntityMarshaller;
import org.mojavemvc.marshalling.PlainTextEntityMarshaller;
import org.mojavemvc.marshalling.XMLEntityMarshaller;
import org.mojavemvc.views.EmptyView;
import org.mojavemvc.views.JSON;
import org.mojavemvc.views.PlainText;
import org.mojavemvc.views.View;
import org.mojavemvc.views.XML;

/**
 * @author Luis Antunes
 */
public class TestEntityMarshallers {

    @Test
    public void defaultEntityMarshallerSupportsNoContentTypes() {
        
        DefaultEntityMarshaller m = new DefaultEntityMarshaller();
        String[] contentTypes = m.contentTypesHandled();
        assertNull(contentTypes);
    }
    
    @Test
    public void defaultEntityMarshallerReturnsView() {
        
        DefaultEntityMarshaller m = new DefaultEntityMarshaller();
        View v = new EmptyView();
        assertEquals(v, m.marshall(v));
    }
    
    @Test
    public void defaultEntityMarshallerReturnsNullAfterUnmarshall() {
        
        DefaultEntityMarshaller m = new DefaultEntityMarshaller();
        assertNull(m.unmarshall(null, null));
    }
    
    @Test
    public void jsonEntityMarshallerSupportsJSONContentType() {
        
        JSONEntityMarshaller m = new JSONEntityMarshaller();
        String[] contentTypes = m.contentTypesHandled();
        assertEquals(1, contentTypes.length);
        assertEquals("application/json", contentTypes[0]);
    }
    
    @Test
    public void jsonEntityMarshallerReturnsView() {
        
        JSONEntityMarshaller m = new JSONEntityMarshaller();
        SimplePojo entity = new SimplePojo("test");
        View v = m.marshall(entity);
        assertTrue(v instanceof JSON);
        assertEquals(new JSON(entity).toString(), ((JSON)v).toString());
    }
    
    @Test
    public void jsonEntityMarshallerUnmarshalls() {
        
        JSONEntityMarshaller m = new JSONEntityMarshaller();
        String json = "{\"val\":\"test\"}";
        ByteArrayInputStream in = new ByteArrayInputStream(json.getBytes());
        SimplePojo entity = m.unmarshall(in, SimplePojo.class);
        assertNotNull(entity);
        assertEquals("test", entity.getVal());
    }
    
    @Test
    public void xmlEntityMarshallerSupportsXMLContentType() {
        
        XMLEntityMarshaller m = new XMLEntityMarshaller();
        String[] contentTypes = m.contentTypesHandled();
        assertEquals(2, contentTypes.length);
        Set<String> contentTypeSet = new HashSet<String>(Arrays.asList(contentTypes));
        assertTrue(contentTypeSet.contains("application/xml"));
        assertTrue(contentTypeSet.contains("text/xml"));
    }
    
    @Test
    public void xmlEntityMarshallerReturnsView() {
        
        XMLEntityMarshaller m = new XMLEntityMarshaller();
        SimplePojo entity = new SimplePojo("test");
        View v = m.marshall(entity);
        assertTrue(v instanceof XML);
        assertEquals(new XML(entity).toString(), ((XML)v).toString());
    }
    
    @Test
    public void xmlEntityMarshallerUnmarshalls() {
        
        XMLEntityMarshaller m = new XMLEntityMarshaller();
        String xml = "<SimplePojo><val>test</val></SimplePojo>";
        ByteArrayInputStream in = new ByteArrayInputStream(xml.getBytes());
        SimplePojo entity = m.unmarshall(in, SimplePojo.class);
        assertNotNull(entity);
        assertEquals("test", entity.getVal());
    }
    
    @Test
    public void plainTextEntityMarshallerSupportsPlainTextContentType() {
        
        PlainTextEntityMarshaller m = new PlainTextEntityMarshaller();
        String[] contentTypes = m.contentTypesHandled();
        assertEquals(1, contentTypes.length);        
        assertEquals("text/plain", contentTypes[0]);
    }
    
    @Test
    public void plainTextEntityMarshallerReturnsView() {
        
        PlainTextEntityMarshaller m = new PlainTextEntityMarshaller();
        SimplePojo entity = new SimplePojo("test");
        View v = m.marshall(entity);
        assertTrue(v instanceof PlainText);
        assertEquals(new PlainText(entity).toString(), ((PlainText)v).toString());
    }
    
    @Test
    public void plainTextEntityMarshallerUnmarshallsString() {
        
        PlainTextEntityMarshaller m = new PlainTextEntityMarshaller();
        String text = "test";
        ByteArrayInputStream in = new ByteArrayInputStream(text.getBytes());
        String entity = m.unmarshall(in, String.class);
        assertNotNull(entity);
        assertEquals("test", entity);
    }
    
    @Test
    public void plainTextEntityMarshallerUnmarshallsSimplePojo() {
        
        PlainTextEntityMarshaller m = new PlainTextEntityMarshaller();
        String text = "test";
        ByteArrayInputStream in = new ByteArrayInputStream(text.getBytes());
        SimplePojo entity = m.unmarshall(in, SimplePojo.class);
        assertNotNull(entity);
        assertEquals("test", entity.getVal());
    }
    
    /*------------------------------------*/
    
    public static class SimplePojo {
        private String val;
        
        public SimplePojo() {}
        
        public SimplePojo(String val) {
            this.val = val;
        }
        
        public String getVal() {
            return val;
        }
        
        public void setVal(String val) {
            this.val = val;
        }
        
        @Override
        public String toString() {
            return val;
        }
    }
}
