/*
 * Copyright (C) 2011 Mojavemvc.org
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
import org.mojavemvc.views.JspView;

/**
 * @author Luis Antunes
 */
public class TestViews {

    public void testJspView_getJsp() throws Exception {

        JspView view = new JspView("test.jsp");
        assertEquals("test.jsp", view.getJsp());
    }

    @Test
    public void testJspView_setModel() throws Exception {

        SomeModel model = new SomeModel();
        model.setName("John");
        model.setNum(21);

        JspView view = new JspView("test.jsp");
        view.setModel(model);

        Map<String, Object> attributes = view.getAttributes();
        assertNotNull(attributes);
        assertEquals(2, attributes.size());
        assertEquals("John", attributes.get("name"));
        assertEquals(21, attributes.get("num"));
    }

    @Test
    public void testJspView_withModel() throws Exception {

        SomeModel model = new SomeModel();
        model.setName("John");
        model.setNum(21);

        JspView view = new JspView("test.jsp").withModel(model);

        Map<String, Object> attributes = view.getAttributes();
        assertNotNull(attributes);
        assertEquals(2, attributes.size());
        assertEquals("John", attributes.get("name"));
        assertEquals(21, attributes.get("num"));
    }

    @Test
    public void testJspView_getModel() throws Exception {

        SomeModel model = new SomeModel();
        model.setName("John");
        model.setNum(21);

        JspView view = new JspView("test.jsp").withModel(model);

        SomeModel model2 = view.getModel(SomeModel.class);

        assertNotNull(model2);
        assertEquals("John", model2.getName());
        assertEquals(21, model2.getNum());
    }

    @Test
    public void testJspView_getModelNoAttributes() throws Exception {

        JspView view = new JspView("test.jsp");
        SomeModel model2 = view.getModel(SomeModel.class);

        assertNotNull(model2);
        assertEquals(null, model2.getName());
        assertEquals(0, model2.getNum());
    }

    @Test
    public void testJspView_setAttributesFromPairs() throws Exception {

        JspView view = new JspView("test.jsp");
        view.setAttributesFromPairs(new String[] { "name", "num" }, new Object[] { "John", 21 });

        Map<String, Object> attributes = view.getAttributes();
        assertNotNull(attributes);
        assertEquals(2, attributes.size());
        assertEquals("John", attributes.get("name"));
        assertEquals(21, attributes.get("num"));
    }

    @Test
    public void testJspView_withAttributesFromPairs() throws Exception {

        JspView view = new JspView("test.jsp").withAttributesFromPairs(new String[] { "name", "num" }, new Object[] {
                "John", 21 });

        Map<String, Object> attributes = view.getAttributes();
        assertNotNull(attributes);
        assertEquals(2, attributes.size());
        assertEquals("John", attributes.get("name"));
        assertEquals(21, attributes.get("num"));
    }

    @Test
    public void testJspView_setAttribute() throws Exception {

        JspView view = new JspView("test.jsp");
        view.setAttribute("name", "John");
        view.setAttribute("num", 21);

        Map<String, Object> attributes = view.getAttributes();
        assertNotNull(attributes);
        assertEquals(2, attributes.size());
        assertEquals("John", attributes.get("name"));
        assertEquals(21, attributes.get("num"));
    }

    @Test
    public void testJspView_withAttribute() throws Exception {

        JspView view = new JspView("test.jsp").withAttribute("name", "John").withAttribute("num", 21);

        Map<String, Object> attributes = view.getAttributes();
        assertNotNull(attributes);
        assertEquals(2, attributes.size());
        assertEquals("John", attributes.get("name"));
        assertEquals(21, attributes.get("num"));
    }

    @Test
    public void testJspView_getAttribute() throws Exception {

        JspView view = new JspView("test.jsp").withAttribute("name", "John").withAttribute("num", 21);

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
