package org.mojavemvc.tests;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.mojavemvc.core.Route;
import org.mojavemvc.core.Route.PathParameterElement;

public class TestRoute {

    @Test
    public void equals_NotEqual() {
        Route r1 = new Route(null, null, null);
        Route r2 = new Route("cntrl", null, null);
        assertFalse(r1.equals(r2));
        assertFalse(r1.hashCode() == r2.hashCode());
    }
    
    @Test
    public void equals_NotEqual_WithController() {
        Route r1 = new Route("cntrl", null, null);
        Route r2 = new Route("cntrl2", null, null);
        assertFalse(r1.equals(r2));
        assertFalse(r1.hashCode() == r2.hashCode());
    }
    
    @Test
    public void equals_NotEqual_WithControllerAndAction() {
        Route r1 = new Route("cntrl", "actn", null);
        Route r2 = new Route("cntrl", "actn2", null);
        assertFalse(r1.equals(r2));
        assertFalse(r1.hashCode() == r2.hashCode());
    }
    
    @Test
    public void equals_NotEqual_WithControllerAndActionAndParams() {
        Route r1 = new Route("cntrl", "actn", ":id");
        Route r2 = new Route("cntrl", "actn2", ":id");
        assertFalse(r1.equals(r2));
        assertFalse(r1.hashCode() == r2.hashCode());
    }
    
    @Test
    public void equals_NotEqual_WithControllerAndActionSameNames() {
        Route r1 = new Route("var", null, null);
        Route r2 = new Route(null, "var", null);
        assertFalse(r1.equals(r2));
        assertFalse(r1.hashCode() == r2.hashCode());
    }
    
    @Test
    public void equals_Equal_AllNull() {
        Route r1 = new Route(null, null, null);
        Route r2 = new Route(null, null, null);
        assertTrue(r1.equals(r2));
        assertTrue(r1.hashCode() == r2.hashCode());
    }
    
    @Test
    public void equals_Equal_WithController() {
        Route r1 = new Route("cntrl", null, null);
        Route r2 = new Route("cntrl", null, null);
        assertTrue(r1.equals(r2));
        assertTrue(r1.hashCode() == r2.hashCode());
    }
    
    @Test
    public void equals_Equal_WithControllerAndAction() {
        Route r1 = new Route("cntrl", "actn", null);
        Route r2 = new Route("cntrl", "actn", null);
        assertTrue(r1.equals(r2));
        assertTrue(r1.hashCode() == r2.hashCode());
    }
    
    @Test
    public void equals_Equal_WithControllerAndActionAndParams() {
        Route r1 = new Route("cntrl", "actn", ":id");
        Route r2 = new Route("cntrl", "actn", ":id");
        assertTrue(r1.equals(r2));
        assertTrue(r1.hashCode() == r2.hashCode());
    }
    
    @Test
    public void toString_NoController_NoAction_NoParamPath() {
        Route r = new Route(null, null, null);
        String expected = "/";
        String actual = r.toString();
        assertEquals(expected, actual);
    }
    
    @Test
    public void toString_WithController_NoAction_NoParamPath() {
        Route r = new Route("cntrl", null, null);
        String expected = "/cntrl";
        String actual = r.toString();
        assertEquals(expected, actual);
    }
    
    @Test
    public void toString_WithController_WithAction_NoParamPath() {
        Route r = new Route("cntrl", "actn", null);
        String expected = "/cntrl/actn";
        String actual = r.toString();
        assertEquals(expected, actual);
    }
    
    @Test
    public void toString_NoController_WithAction_NoParamPath() {
        Route r = new Route(null, "actn", null);
        String expected = "/actn";
        String actual = r.toString();
        assertEquals(expected, actual);
    }
    
    @Test
    public void toString_WithController_WithAction_WithParamPath() {
        Route r = new Route("cntrl", "actn", "clients/:id");
        String expected = "/cntrl/actn/clients/:id";
        String actual = r.toString();
        assertEquals(expected, actual);
    }
    
    @Test
    public void toString_NoController_WithAction_WithParamPath() {
        Route r = new Route(null, "actn", "clients/:id");
        String expected = "/actn/clients/:id";
        String actual = r.toString();
        assertEquals(expected, actual);
    }
    
    @Test
    public void toString_WithController_NoAction_WithParamPath() {
        Route r = new Route("cntrl", null, "clients/:id");
        String expected = "/cntrl/clients/:id";
        String actual = r.toString();
        assertEquals(expected, actual);
    }
    
    @Test
    public void toString_NoController_NoAction_WithParamPath() {
        Route r = new Route(null, null, "clients/:id");
        String expected = "/clients/:id";
        String actual = r.toString();
        assertEquals(expected, actual);
    }
    
    @Test
    public void pathParameterElements_NoneExist() {
        Route r = new Route(null, "actn", null);
        List<PathParameterElement> params = r.pathParameterElements();
        assertTrue(params.isEmpty());
    }
    
    @Test
    public void pathParameterElements_OneExistsWithAction() {
        Route r = new Route(null, "actn", ":id");
        List<PathParameterElement> params = r.pathParameterElements();
        assertEquals(1, params.size());
        PathParameterElement elem = params.get(0);
        assertEquals("id", elem.name());
        assertEquals(1, elem.index());
    }
    
    @Test
    public void pathParameterElements_OneExistsAlone() {
        Route r = new Route(null, null, ":id");
        List<PathParameterElement> params = r.pathParameterElements();
        assertEquals(1, params.size());
        PathParameterElement elem = params.get(0);
        assertEquals("id", elem.name());
        assertEquals(0, elem.index());
    }
    
    @Test
    public void pathParameterElements_ManyExistAlone() {
        Route r = new Route(null, null, ":id/:name");
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
        Route r = new Route("cntrl", "actn", ":id/:name");
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
        Route r = new Route("cntrl", "actn", ":id<[0-9]+>/:name<[a-z]+>");
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
        Route r = new Route("cntrl", "actn", ":id<[^/]+>/:name<[a-z]+>");
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
