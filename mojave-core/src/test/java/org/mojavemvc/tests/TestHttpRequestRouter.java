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

import javax.servlet.http.HttpServletRequest;

import org.junit.Test;
import org.mojavemvc.core.HttpRequestRouter;
import org.mojavemvc.core.RoutedRequest;

/**
 * @author Luis Antunes
 */
public class TestHttpRequestRouter {

    @Test
    public void handlesNullPath() {
        
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getPathInfo()).thenReturn(null);
        
        HttpRequestRouter router = new HttpRequestRouter(req);
        
        RoutedRequest routed = router.route();
        
        assertNull(routed.getController());
        assertNull(routed.getAction());
    }
    
    @Test
    public void handlesEmptyPath() {
        
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getPathInfo()).thenReturn("");
        
        HttpRequestRouter router = new HttpRequestRouter(req);
        
        RoutedRequest routed = router.route();
        
        assertNull(routed.getController());
        assertNull(routed.getAction());
    }
    
    @Test
    public void handlesRootPath() {
        
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getPathInfo()).thenReturn("/");
        
        HttpRequestRouter router = new HttpRequestRouter(req);
        
        RoutedRequest routed = router.route();
        
        assertNull(routed.getController());
        assertNull(routed.getAction());
    }
    
    @Test
    public void handlesControllerOnly() {
        
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getPathInfo()).thenReturn("/cntrl");
        
        HttpRequestRouter router = new HttpRequestRouter(req);
        
        RoutedRequest routed = router.route();
        
        assertEquals("cntrl", routed.getController());
        assertNull(routed.getAction());
    }
    
    @Test
    public void handlesControllerAndAction() {
        
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getPathInfo()).thenReturn("/cntrl/actn");
        
        HttpRequestRouter router = new HttpRequestRouter(req);
        
        RoutedRequest routed = router.route();
        
        assertEquals("cntrl", routed.getController());
        assertEquals("actn", routed.getAction());
    }
    
    @Test
    public void handlesControllerAndAction2() {
        
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getPathInfo()).thenReturn("/cntrl/actn/unknown");
        
        HttpRequestRouter router = new HttpRequestRouter(req);
        
        RoutedRequest routed = router.route();
        
        assertEquals("cntrl", routed.getController());
        assertEquals("actn", routed.getAction());
    }
}
