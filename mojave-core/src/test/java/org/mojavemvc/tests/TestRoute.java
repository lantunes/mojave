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

import java.util.List;

import org.bigtesting.routd.PathParameterElement;
import org.junit.Test;
import org.mojavemvc.core.MojaveRoute;

/**
 * @author Luis Antunes
 */
public class TestRoute {

    @Test
    public void equals_NotEqual() {
        MojaveRoute r1 = new MojaveRoute(null, null, null);
        MojaveRoute r2 = new MojaveRoute("cntrl", null, null);
        assertFalse(r1.equals(r2));
        assertFalse(r1.hashCode() == r2.hashCode());
    }
    
    @Test
    public void equals_NotEqual_WithController() {
        MojaveRoute r1 = new MojaveRoute("cntrl", null, null);
        MojaveRoute r2 = new MojaveRoute("cntrl2", null, null);
        assertFalse(r1.equals(r2));
        assertFalse(r1.hashCode() == r2.hashCode());
    }
    
    @Test
    public void equals_NotEqual_WithControllerAndAction() {
        MojaveRoute r1 = new MojaveRoute("cntrl", "actn", null);
        MojaveRoute r2 = new MojaveRoute("cntrl", "actn2", null);
        assertFalse(r1.equals(r2));
        assertFalse(r1.hashCode() == r2.hashCode());
    }
    
    @Test
    public void equals_NotEqual_WithControllerAndActionAndParams() {
        MojaveRoute r1 = new MojaveRoute("cntrl", "actn", ":id");
        MojaveRoute r2 = new MojaveRoute("cntrl", "actn2", ":id");
        assertFalse(r1.equals(r2));
        assertFalse(r1.hashCode() == r2.hashCode());
    }
    
    @Test
    public void equals_NotEqual_WithControllerAndActionSameNames() {
        MojaveRoute r1 = new MojaveRoute("var", null, null);
        MojaveRoute r2 = new MojaveRoute(null, "var", null);
        assertFalse(r1.equals(r2));
        assertFalse(r1.hashCode() == r2.hashCode());
    }
    
    @Test
    public void equals_Equal_AllNull() {
        MojaveRoute r1 = new MojaveRoute(null, null, null);
        MojaveRoute r2 = new MojaveRoute(null, null, null);
        assertTrue(r1.equals(r2));
        assertTrue(r1.hashCode() == r2.hashCode());
    }
    
    @Test
    public void equals_Equal_WithController() {
        MojaveRoute r1 = new MojaveRoute("cntrl", null, null);
        MojaveRoute r2 = new MojaveRoute("cntrl", null, null);
        assertTrue(r1.equals(r2));
        assertTrue(r1.hashCode() == r2.hashCode());
    }
    
    @Test
    public void equals_Equal_WithControllerAndAction() {
        MojaveRoute r1 = new MojaveRoute("cntrl", "actn", null);
        MojaveRoute r2 = new MojaveRoute("cntrl", "actn", null);
        assertTrue(r1.equals(r2));
        assertTrue(r1.hashCode() == r2.hashCode());
    }
    
    @Test
    public void equals_Equal_WithControllerAndActionAndParams() {
        MojaveRoute r1 = new MojaveRoute("cntrl", "actn", ":id");
        MojaveRoute r2 = new MojaveRoute("cntrl", "actn", ":id");
        assertTrue(r1.equals(r2));
        assertTrue(r1.hashCode() == r2.hashCode());
    }
    
    @Test
    public void toString_NoController_NoAction_NoParamPath() {
        MojaveRoute r = new MojaveRoute(null, null, null);
        String expected = "/";
        String actual = r.toString();
        assertEquals(expected, actual);
    }
    
    @Test
    public void toString_WithController_NoAction_NoParamPath() {
        MojaveRoute r = new MojaveRoute("cntrl", null, null);
        String expected = "/cntrl";
        String actual = r.toString();
        assertEquals(expected, actual);
    }
    
    @Test
    public void toString_WithController_WithAction_NoParamPath() {
        MojaveRoute r = new MojaveRoute("cntrl", "actn", null);
        String expected = "/cntrl/actn";
        String actual = r.toString();
        assertEquals(expected, actual);
    }
    
    @Test
    public void toString_NoController_WithAction_NoParamPath() {
        MojaveRoute r = new MojaveRoute(null, "actn", null);
        String expected = "/actn";
        String actual = r.toString();
        assertEquals(expected, actual);
    }
    
    @Test
    public void toString_WithController_WithAction_WithParamPath() {
        MojaveRoute r = new MojaveRoute("cntrl", "actn", "clients/:id");
        String expected = "/cntrl/actn/clients/:id";
        String actual = r.toString();
        assertEquals(expected, actual);
    }
    
    @Test
    public void toString_NoController_WithAction_WithParamPath() {
        MojaveRoute r = new MojaveRoute(null, "actn", "clients/:id");
        String expected = "/actn/clients/:id";
        String actual = r.toString();
        assertEquals(expected, actual);
    }
    
    @Test
    public void toString_WithController_NoAction_WithParamPath() {
        MojaveRoute r = new MojaveRoute("cntrl", null, "clients/:id");
        String expected = "/cntrl/clients/:id";
        String actual = r.toString();
        assertEquals(expected, actual);
    }
    
    @Test
    public void toString_NoController_NoAction_WithParamPath() {
        MojaveRoute r = new MojaveRoute(null, null, "clients/:id");
        String expected = "/clients/:id";
        String actual = r.toString();
        assertEquals(expected, actual);
    }
    
    @Test
    public void pathParameterElements_NoneExist() {
        MojaveRoute r = new MojaveRoute(null, "actn", null);
        List<PathParameterElement> params = r.pathParameterElements();
        assertTrue(params.isEmpty());
    }
    
    @Test
    public void pathParameterElements_OneExistsWithAction() {
        MojaveRoute r = new MojaveRoute(null, "actn", ":id");
        List<PathParameterElement> params = r.pathParameterElements();
        assertEquals(1, params.size());
        PathParameterElement elem = params.get(0);
        assertEquals("id", elem.name());
        assertEquals(1, elem.index());
    }
    
    @Test
    public void pathParameterElements_OneExistsAlone() {
        MojaveRoute r = new MojaveRoute(null, null, ":id");
        List<PathParameterElement> params = r.pathParameterElements();
        assertEquals(1, params.size());
        PathParameterElement elem = params.get(0);
        assertEquals("id", elem.name());
        assertEquals(0, elem.index());
    }
    
    @Test
    public void pathParameterElements_ManyExistAlone() {
        MojaveRoute r = new MojaveRoute(null, null, ":id/:name");
        List<PathParameterElement> params = r.pathParameterElements();
        assertEquals(2, params.size());
        PathParameterElement elem = params.get(0);
        assertEquals("id", elem.name());
        assertEquals(0, elem.index());
        elem = params.get(1);
        assertEquals("name", elem.name());
        assertEquals(1, elem.index());
    }
    
    @Test
    public void pathParameterElements_ManyExistWithControllerAndAction() {
        MojaveRoute r = new MojaveRoute("cntrl", "actn", ":id/:name");
        List<PathParameterElement> params = r.pathParameterElements();
        assertEquals(2, params.size());
        PathParameterElement elem = params.get(0);
        assertEquals("id", elem.name());
        assertEquals(2, elem.index());
        elem = params.get(1);
        assertEquals("name", elem.name());
        assertEquals(3, elem.index());
    }
    
    @Test
    public void pathParameterElements_ManyExistWithRegexWithControllerAndAction() {
        MojaveRoute r = new MojaveRoute("cntrl", "actn", ":id<[0-9]+>/:name<[a-z]+>");
        List<PathParameterElement> params = r.pathParameterElements();
        assertEquals(2, params.size());
        PathParameterElement elem = params.get(0);
        assertEquals("id", elem.name());
        assertEquals(2, elem.index());
        elem = params.get(1);
        assertEquals("name", elem.name());
        assertEquals(3, elem.index());
    }
    
    @Test
    public void pathParameterElements_OneExistsWithRegexWithSlashWithControllerAndAction() {
        MojaveRoute r = new MojaveRoute("cntrl", "actn", ":id<[^/]+>/:name<[a-z]+>");
        List<PathParameterElement> params = r.pathParameterElements();
        assertEquals(2, params.size());
        PathParameterElement elem = params.get(0);
        assertEquals("id", elem.name());
        assertEquals(2, elem.index());
        elem = params.get(1);
        assertEquals("name", elem.name());
        assertEquals(3, elem.index());
    }
}
