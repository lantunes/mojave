/*
 * Copyright (C) 2011-2012 Mojavemvc.org
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
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.mojavemvc.core.HttpRequestRouter;
import org.mojavemvc.core.Route;
import org.mojavemvc.core.RouteMap;
import org.mojavemvc.core.RoutedRequest;
import org.mojavemvc.exception.NoMatchingRouteException;

/**
 * @author Luis Antunes
 */
public class TestHttpRequestRouter {

    private Map<String, Object> paramMap;
    private RouteMap routeMap;
    private HttpServletRequest req;
    
    @Before
    public void beforeEachTest() {
        
        paramMap = new HashMap<String, Object>();
        routeMap = mock(RouteMap.class);
        req = mock(HttpServletRequest.class);
        when(req.getParameterMap()).thenReturn(paramMap);
    }
    
    @Test
    public void handlesNullPath() {
        
        when(req.getPathInfo()).thenReturn(null);
        
        RoutedRequest routed = newRouter().route();
        
        assertNull(routed.getController());
        assertNull(routed.getAction());
        assertTrue(routed.getParameterMap().isEmpty());
    }
    
    @Test
    public void handlesEmptyPath() {
        
        when(req.getPathInfo()).thenReturn("");
        
        RoutedRequest routed = newRouter().route();
        
        assertNull(routed.getController());
        assertNull(routed.getAction());
        assertTrue(routed.getParameterMap().isEmpty());
    }
    
    @Test
    public void handlesRootPath() {
        
        String pathInfo = "/";
        when(req.getPathInfo()).thenReturn(pathInfo);
        when(routeMap.getRoute(pathInfo))
            .thenReturn(new Route(null, null, null));
        
        RoutedRequest routed = newRouter().route();
        
        assertNull(routed.getController());
        assertNull(routed.getAction());
        assertTrue(routed.getParameterMap().isEmpty());
    }
    
    @Test
    public void handlesControllerOnly() {
        
        String pathInfo = "/cntrl";
        when(req.getPathInfo()).thenReturn(pathInfo);
        when(routeMap.getRoute(pathInfo))
            .thenReturn(new Route("cntrl", null, null));
        
        
        RoutedRequest routed = newRouter().route();
        
        assertEquals("cntrl", routed.getController());
        assertNull(routed.getAction());
        assertTrue(routed.getParameterMap().isEmpty());
    }
    
    @Test
    public void handlesControllerAndAction_NoPathParams() {
        
        String pathInfo = "/cntrl/actn";
        when(req.getPathInfo()).thenReturn(pathInfo);
        when(routeMap.getRoute(pathInfo))
            .thenReturn(new Route("cntrl", "actn", null));
        
        RoutedRequest routed = newRouter().route();
        
        assertEquals("cntrl", routed.getController());
        assertEquals("actn", routed.getAction());
        assertTrue(routed.getParameterMap().isEmpty());
    }
    
    @Test
    public void handlesRouteNotFound() {
        
        String pathInfo = "/cntrl/actn/unknown";
        when(req.getPathInfo()).thenReturn(pathInfo);
        when(routeMap.getRoute(pathInfo)).thenReturn(null);
        when(req.getParameterMap()).thenReturn(paramMap);
        
        try {
            newRouter().route();
            fail("should have thrown exception");
        } catch (Exception e) {
            assertTrue(e instanceof NoMatchingRouteException);
        }
    }
    
    @Test
    public void handlesControllerAndAction_OnePathParam() {
        
        String pathInfo = "/cntrl/actn/123";
        when(req.getPathInfo()).thenReturn(pathInfo);
        when(routeMap.getRoute(pathInfo))
            .thenReturn(new Route("cntrl", "actn", ":id"));
        
        RoutedRequest routed = newRouter().route();
        
        assertEquals("cntrl", routed.getController());
        assertEquals("actn", routed.getAction());
        Map<String, Object> paramMap = routed.getParameterMap();
        Object val = paramMap.get("id");
        assertNotNull(val);
        assertArrayEquals(new String[]{"123"}, (String[])val);
    }
    
    @Test
    public void handlesControllerAndAction_TwoPathParams() {
        
        String pathInfo = "/cntrl/actn/123/tom";
        when(req.getPathInfo()).thenReturn(pathInfo);
        when(routeMap.getRoute(pathInfo))
            .thenReturn(new Route("cntrl", "actn", ":id/:name"));
        
        RoutedRequest routed = newRouter().route();
        
        assertEquals("cntrl", routed.getController());
        assertEquals("actn", routed.getAction());
        Map<String, Object> paramMap = routed.getParameterMap();
        Object val = paramMap.get("id");
        assertNotNull(val);
        assertArrayEquals(new String[]{"123"}, (String[])val);
        val = paramMap.get("name");
        assertNotNull(val);
        assertArrayEquals(new String[]{"tom"}, (String[])val);
    }
    
    @Test
    public void handlesControllerAndAction_OnePathParam_AlreadyExists() {
        
        String pathInfo = "/cntrl/actn/123";
        when(req.getPathInfo()).thenReturn(pathInfo);
        when(routeMap.getRoute(pathInfo))
            .thenReturn(new Route("cntrl", "actn", ":id"));
        paramMap.put("id", new String[]{"456"});
        
        RoutedRequest routed = newRouter().route();
        
        assertEquals("cntrl", routed.getController());
        assertEquals("actn", routed.getAction());
        Map<String, Object> paramMap = routed.getParameterMap();
        Object val = paramMap.get("id");
        assertNotNull(val);
        assertArrayEquals(new String[]{"123"}, (String[])val);
    }
    
    @Test
    public void handlesControllerAndAction_OnePathParam_AnotherExists() {
        
        String pathInfo = "/cntrl/actn/123";
        when(req.getPathInfo()).thenReturn(pathInfo);
        when(routeMap.getRoute(pathInfo))
            .thenReturn(new Route("cntrl", "actn", ":id"));
        paramMap.put("name", new String[]{"tom"});
        
        RoutedRequest routed = newRouter().route();
        
        assertEquals("cntrl", routed.getController());
        assertEquals("actn", routed.getAction());
        Map<String, Object> paramMap = routed.getParameterMap();
        Object val = paramMap.get("id");
        assertNotNull(val);
        assertArrayEquals(new String[]{"123"}, (String[])val);
        val = paramMap.get("name");
        assertNotNull(val);
        assertArrayEquals(new String[]{"tom"}, (String[])val);
    }
    
    /*----------------------*/
    
    private HttpRequestRouter newRouter() {
        return new HttpRequestRouter(req, routeMap);
    }
}
