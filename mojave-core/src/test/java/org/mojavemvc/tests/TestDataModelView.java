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

import java.util.Map;

import org.junit.Test;
import org.mojavemvc.tests.views.HTMLPage;

/**
 * @author Luis Antunes
 */
public class TestDataModelView {

    @Test
    public void testView_setModel() throws Exception {

        SomeModel model = new SomeModel();
        model.setName("John");
        model.setNum(21);

        HTMLPage view = new HTMLPage();
        view.setModel(model);

        Map<String, Object> attributes = view.getAttributes();
        assertNotNull(attributes);
        assertEquals(2, attributes.size());
        assertEquals("John", attributes.get("name"));
        assertEquals(21, attributes.get("num"));
    }

    @Test
    public void testView_withModel() throws Exception {

        SomeModel model = new SomeModel();
        model.setName("John");
        model.setNum(21);

        HTMLPage view = new HTMLPage().withModel(model);

        Map<String, Object> attributes = view.getAttributes();
        assertNotNull(attributes);
        assertEquals(2, attributes.size());
        assertEquals("John", attributes.get("name"));
        assertEquals(21, attributes.get("num"));
    }

    @Test
    public void testView_getModel() throws Exception {

        SomeModel model = new SomeModel();
        model.setName("John");
        model.setNum(21);

        HTMLPage view = new HTMLPage().withModel(model);

        SomeModel model2 = view.getModel(SomeModel.class);

        assertNotNull(model2);
        assertEquals("John", model2.getName());
        assertEquals(21, model2.getNum());
    }

    @Test
    public void testView_getModelNoAttributes() throws Exception {

        HTMLPage view = new HTMLPage();
        SomeModel model2 = view.getModel(SomeModel.class);

        assertNotNull(model2);
        assertEquals(null, model2.getName());
        assertEquals(0, model2.getNum());
    }

    @Test
    public void testView_setAttributesFromPairs() throws Exception {

        HTMLPage view = new HTMLPage();
        view.setAttributesFromPairs(new String[] { "name", "num" }, new Object[] { "John", 21 });

        Map<String, Object> attributes = view.getAttributes();
        assertNotNull(attributes);
        assertEquals(2, attributes.size());
        assertEquals("John", attributes.get("name"));
        assertEquals(21, attributes.get("num"));
    }

    @Test
    public void testView_withAttributesFromPairs() throws Exception {

        HTMLPage view = new HTMLPage().withAttributesFromPairs(
                new String[] { "name", "num" }, 
                new Object[] { "John", 21 });

        Map<String, Object> attributes = view.getAttributes();
        assertNotNull(attributes);
        assertEquals(2, attributes.size());
        assertEquals("John", attributes.get("name"));
        assertEquals(21, attributes.get("num"));
    }

    @Test
    public void testView_setAttribute() throws Exception {

        HTMLPage view = new HTMLPage();
        view.setAttribute("name", "John");
        view.setAttribute("num", 21);

        Map<String, Object> attributes = view.getAttributes();
        assertNotNull(attributes);
        assertEquals(2, attributes.size());
        assertEquals("John", attributes.get("name"));
        assertEquals(21, attributes.get("num"));
    }

    @Test
    public void testView_withAttribute() throws Exception {

        HTMLPage view = new HTMLPage().withAttribute("name", "John").withAttribute("num", 21);

        Map<String, Object> attributes = view.getAttributes();
        assertNotNull(attributes);
        assertEquals(2, attributes.size());
        assertEquals("John", attributes.get("name"));
        assertEquals(21, attributes.get("num"));
    }

    @Test
    public void testView_getAttribute() throws Exception {

        HTMLPage view = new HTMLPage().withAttribute("name", "John").withAttribute("num", 21);

        assertEquals("John", view.getAttribute("name"));
        assertEquals(21, view.getAttribute("num"));
    }

    public static class SomeModel {

        private String name;
        private int num;

        public SomeModel() {
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getNum() {
            return num;
        }

        public void setNum(int num) {
            this.num = num;
        }
    }
}
