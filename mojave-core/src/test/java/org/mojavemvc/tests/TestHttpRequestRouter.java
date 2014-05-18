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
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;

import org.bigtesting.routd.RouteMap;
import org.junit.Before;
import org.junit.Test;
import org.mojavemvc.core.HttpRequestRouter;
import org.mojavemvc.core.MojaveRoute;
import org.mojavemvc.core.ParameterMapSource;
import org.mojavemvc.core.RoutedRequest;
import org.mojavemvc.exception.NoMatchingRouteException;

/**
 * @author Luis Antunes
 */
public class TestHttpRequestRouter {

    private Map<String, Object> paramMap;
    private ParameterMapSource paramMapSource;
    private RouteMap routeMap;
    
    @Before
    public void beforeEachTest() {
        
        paramMap = new HashMap<String, Object>();
        routeMap = mock(RouteMap.class);
        paramMapSource = mock(ParameterMapSource.class);        
        when(paramMapSource.getParameterMap()).thenReturn(paramMap);
    }
    
    @Test
    public void handlesNullPath() {
        
        RoutedRequest routed = newRouter(null).route();
        
        assertNull(routed.getController());
        assertNull(routed.getAction());
        assertTrue(routed.getParameterMap().isEmpty());
    }
    
    @Test
    public void handlesEmptyPath() {
        
        RoutedRequest routed = newRouter("").route();
        
        assertNull(routed.getController());
        assertNull(routed.getAction());
        assertTrue(routed.getParameterMap().isEmpty());
    }
    
    @Test
    public void handlesRootPath() {
        
        String pathInfo = "/";
        when(routeMap.getRoute(pathInfo))
            .thenReturn(new MojaveRoute(null, null, null));
        
        RoutedRequest routed = newRouter(pathInfo).route();
        
        assertNull(routed.getController());
        assertNull(routed.getAction());
        assertTrue(routed.getParameterMap().isEmpty());
    }
    
    @Test
    public void handlesControllerOnly() {
        
        String pathInfo = "/cntrl";
        when(routeMap.getRoute(pathInfo))
            .thenReturn(new MojaveRoute("cntrl", null, null));
        
        
        RoutedRequest routed = newRouter(pathInfo).route();
        
        assertEquals("cntrl", routed.getController());
        assertNull(routed.getAction());
        assertTrue(routed.getParameterMap().isEmpty());
    }
    
    @Test
    public void handlesControllerAndAction_NoPathParams() {
        
        String pathInfo = "/cntrl/actn";
        when(routeMap.getRoute(pathInfo))
            .thenReturn(new MojaveRoute("cntrl", "actn", null));
        
        RoutedRequest routed = newRouter(pathInfo).route();
        
        assertEquals("cntrl", routed.getController());
        assertEquals("actn", routed.getAction());
        assertTrue(routed.getParameterMap().isEmpty());
    }
    
    @Test
    public void handlesRouteNotFound() {
        
        String pathInfo = "/cntrl/actn/unknown";
        when(routeMap.getRoute(pathInfo)).thenReturn(null);
        
        try {
            newRouter(pathInfo).route();
            fail("should have thrown exception");
        } catch (Exception e) {
            assertTrue(e instanceof NoMatchingRouteException);
        }
    }
    
    @Test
    public void handlesControllerAndAction_OnePathParam() {
        
        String pathInfo = "/cntrl/actn/123";
        when(routeMap.getRoute(pathInfo))
            .thenReturn(new MojaveRoute("cntrl", "actn", ":id"));
        
        RoutedRequest routed = newRouter(pathInfo).route();
        
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
        when(routeMap.getRoute(pathInfo))
            .thenReturn(new MojaveRoute("cntrl", "actn", ":id/:name"));
        
        RoutedRequest routed = newRouter(pathInfo).route();
        
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
        when(routeMap.getRoute(pathInfo))
            .thenReturn(new MojaveRoute("cntrl", "actn", ":id"));
        paramMap.put("id", new String[]{"456"});
        
        RoutedRequest routed = newRouter(pathInfo).route();
        
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
        when(routeMap.getRoute(pathInfo))
            .thenReturn(new MojaveRoute("cntrl", "actn", ":id"));
        paramMap.put("name", new String[]{"tom"});
        
        RoutedRequest routed = newRouter(pathInfo).route();
        
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
    
    private HttpRequestRouter newRouter(String pathInfo) {
        return new HttpRequestRouter(pathInfo, paramMapSource, routeMap);
    }
}
