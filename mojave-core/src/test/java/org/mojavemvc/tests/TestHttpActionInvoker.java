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

import static junit.framework.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.cglib.reflect.FastClass;

import org.bigtesting.routd.RegexRouteMap;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.mojavemvc.core.ActionInvoker;
import org.mojavemvc.core.ActionSignature;
import org.mojavemvc.core.ControllerDatabase;
import org.mojavemvc.core.HttpActionInvoker;
import org.mojavemvc.core.MappedControllerDatabase;
import org.mojavemvc.core.RoutedRequest;
import org.mojavemvc.core.ServletResourceModule;
import org.mojavemvc.initialization.AppProperties;
import org.mojavemvc.marshalling.EntityMarshaller;
import org.mojavemvc.tests.controllers.SomeStatelessController;
import org.mojavemvc.tests.interceptors.Interceptor1;
import org.mojavemvc.tests.interceptors.Interceptor1b;
import org.mojavemvc.tests.interceptors.Interceptor1c;
import org.mojavemvc.tests.interceptors.Interceptor1d;
import org.mojavemvc.tests.modules.SomeModule;
import org.mojavemvc.tests.othercontrollers.InterceptedController1;
import org.mojavemvc.tests.othercontrollers.InterceptedController2;
import org.mojavemvc.tests.othercontrollers.InterceptedController3;
import org.mojavemvc.tests.othercontrollers.InterceptedController4;
import org.mojavemvc.tests.othercontrollers.InterceptedController5;
import org.mojavemvc.tests.othercontrollers.InterceptedController6;
import org.mojavemvc.tests.othercontrollers.InterceptedController7;
import org.mojavemvc.tests.othercontrollers.InterceptedController8;
import org.mojavemvc.tests.othercontrollers.InterceptedController9;
import org.mojavemvc.tests.views.HTMLPage;
import org.mojavemvc.views.View;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * @author Luis Antunes
 */
public class TestHttpActionInvoker {

    private HttpServletRequest req;

    private HttpServletResponse res;

    private HttpSession sess;
    
    private RoutedRequest routed;

    private Injector injector;

    private Map<String, Object> parametersMap = new HashMap<String, Object>();
    
    private AppProperties appProperties;

    @Before
    public void beforeEachTest() throws Exception {

        sess = mock(HttpSession.class);
        req = mock(HttpServletRequest.class);
        when(req.getSession()).thenReturn(sess);
        when(req.getInputStream()).thenReturn(null);
        res = mock(HttpServletResponse.class);
        appProperties = mock(AppProperties.class);

        injector = Guice.createInjector(
                new ServletResourceModule(appProperties), new SomeModule());
        ServletResourceModule.set(req, res);

        routed = new RoutedRequest(null, null, parametersMap);
    }
    
    @After
    public void afterEachTest() {
        
        ServletResourceModule.unset();
    }
    
    @Test
    public void testInvokeAction() throws Exception {

        String methodName = "doSomething";

        SetUp<SomeStatelessController> setup = 
                setUp(SomeStatelessController.class, methodName);
        ActionInvoker invoker = new HttpActionInvoker(req, res, setup.db, routed, injector);

        HTMLPage view = (HTMLPage) invoker.invokeAction(setup.controller, setup.signature);

        assertNotNull(view);
        String h2 = view.getH2Content();
        assertEquals("test", h2);
        assertEquals(req, setup.controller.getRequest());
        assertEquals(res, setup.controller.getResponse());
        assertEquals(sess, setup.controller.getSession());
        assertEquals(1, setup.controller.beforeInvokeCount);
        assertEquals(1, setup.controller.afterInvokeCount);
    }
    
    @Test
    public void testInvokeDefaultAction() throws Exception {

        String methodName = "defaultAction";

        SetUp<SomeStatelessController> setup = 
                setUp(SomeStatelessController.class, methodName);
        ActionInvoker invoker = new HttpActionInvoker(req, res, setup.db, routed, injector);

        HTMLPage view = (HTMLPage) invoker.invokeAction(setup.controller, setup.signature);

        assertNotNull(view);
        String h2 = view.getH2Content();
        assertEquals("default", h2);
        assertEquals(req, setup.controller.getRequest());
        assertEquals(res, setup.controller.getResponse());
        assertEquals(sess, setup.controller.getSession());
        assertEquals(1, setup.controller.beforeInvokeCount);
        assertEquals(1, setup.controller.afterInvokeCount);
    }

    @Test
    public void testInterceptors1() throws Exception {

        String methodName = "someAction";

        List<String> invocationList = new ArrayList<String>();
        InterceptedController1.invocationList = invocationList;
        Interceptor1.invocationList = invocationList;

        SetUp<InterceptedController1> setup = 
                setUp(InterceptedController1.class, methodName);
        ActionInvoker invoker = new HttpActionInvoker(req, res, setup.db, routed, injector);

        HTMLPage view = (HTMLPage) invoker.invokeAction(setup.controller, setup.signature);

        assertNotNull(view);
        String h2 = view.getH2Content();
        assertEquals("someAction", h2);
        assertEquals(req, setup.controller.getRequest());
        assertEquals(res, setup.controller.getResponse());
        assertEquals(sess, setup.controller.getSession());
        assertNotNull(setup.controller.getSomeService());
        assertEquals(3, invocationList.size());
        assertEquals("interceptor1-beforeAction:req:resp:sess:someService", invocationList.get(0));
        assertEquals("someAction", invocationList.get(1));
        assertEquals("interceptor1-afterAction:req:resp:sess:someService:ok", invocationList.get(2));
    }

    @Test
    public void testInterceptors2() throws Exception {

        String methodName = "someAction";
        String action = "some-action";

        routed = new RoutedRequest(null, action, parametersMap);

        List<String> invocationList = new ArrayList<String>();
        InterceptedController2.invocationList = invocationList;
        Interceptor1.invocationList = invocationList;

        SetUp<InterceptedController2> setup = 
                setUp(InterceptedController2.class, methodName, action);
        ActionInvoker invoker = new HttpActionInvoker(req, res, setup.db, routed, injector);

        HTMLPage view = (HTMLPage) invoker.invokeAction(setup.controller, setup.signature);

        assertNotNull(view);
        String h2 = view.getH2Content();
        assertEquals("someAction", h2);
        assertEquals(req, setup.controller.getRequest());
        assertEquals(res, setup.controller.getResponse());
        assertEquals(sess, setup.controller.getSession());
        assertNotNull(setup.controller.getSomeService());
        assertEquals(3, invocationList.size());
        assertEquals("interceptor1-beforeAction:req:resp:sess:someService", invocationList.get(0));
        assertEquals("someAction", invocationList.get(1));
        assertEquals("interceptor1-afterAction:req:resp:sess:someService:ok", invocationList.get(2));
    }

    @Test
    public void testInterceptors3() throws Exception {

        String methodName = "defaultAction";

        List<String> invocationList = new ArrayList<String>();
        InterceptedController2.invocationList = invocationList;
        Interceptor1.invocationList = invocationList;

        SetUp<InterceptedController2> setup = 
                setUp(InterceptedController2.class, methodName);
        ActionInvoker invoker = new HttpActionInvoker(req, res, setup.db, routed, injector);

        HTMLPage view = (HTMLPage) invoker.invokeAction(setup.controller, setup.signature);

        assertNotNull(view);
        String h2 = view.getH2Content();
        assertEquals("defaultAction", h2);
        assertEquals(req, setup.controller.getRequest());
        assertEquals(res, setup.controller.getResponse());
        assertEquals(sess, setup.controller.getSession());
        assertNotNull(setup.controller.getSomeService());
        assertEquals(3, invocationList.size());
        assertEquals("interceptor1-beforeAction:req:resp:sess:someService", invocationList.get(0));
        assertEquals("defaultAction", invocationList.get(1));
        assertEquals("interceptor1-afterAction:req:resp:sess:someService:ok", invocationList.get(2));
    }

    @Test
    public void testInterceptors4() throws Exception {

        String methodName = "someAction";

        List<String> invocationList = new ArrayList<String>();
        InterceptedController3.invocationList = invocationList;
        Interceptor1.invocationList = invocationList;
        Interceptor1b.invocationList = invocationList;

        SetUp<InterceptedController3> setup = 
                setUp(InterceptedController3.class, methodName);
        ActionInvoker invoker = new HttpActionInvoker(req, res, setup.db, routed, injector);

        HTMLPage view = (HTMLPage) invoker.invokeAction(setup.controller, setup.signature);

        assertNotNull(view);
        String h2 = view.getH2Content();
        assertEquals("someAction", h2);
        assertEquals(req, setup.controller.getRequest());
        assertEquals(res, setup.controller.getResponse());
        assertEquals(sess, setup.controller.getSession());
        assertNotNull(setup.controller.getSomeService());
        assertEquals(5, invocationList.size());
        assertEquals("interceptor1-beforeAction:req:resp:sess:someService", invocationList.get(0));
        assertEquals("interceptor1b-beforeAction:req:resp:sess:someService", invocationList.get(1));
        assertEquals("someAction", invocationList.get(2));
        assertEquals("interceptor1-afterAction:req:resp:sess:someService:ok", invocationList.get(3));
        assertEquals("interceptor1b-afterAction:req:resp:sess:someService:ok", invocationList.get(4));
    }

    @Test
    public void testInterceptors5() throws Exception {

        String methodName = "someAction";
        String action = "some-action";

        routed = new RoutedRequest(null, action, parametersMap);

        List<String> invocationList = new ArrayList<String>();
        InterceptedController4.invocationList = invocationList;
        Interceptor1.invocationList = invocationList;
        Interceptor1b.invocationList = invocationList;

        SetUp<InterceptedController4> setup = 
                setUp(InterceptedController4.class, methodName, action);
        ActionInvoker invoker = new HttpActionInvoker(req, res, setup.db, routed, injector);

        HTMLPage view = (HTMLPage) invoker.invokeAction(setup.controller, setup.signature);

        assertNotNull(view);
        String h2 = view.getH2Content();
        assertEquals("someAction", h2);
        assertEquals(req, setup.controller.getRequest());
        assertEquals(res, setup.controller.getResponse());
        assertEquals(sess, setup.controller.getSession());
        assertNotNull(setup.controller.getSomeService());
        assertEquals(5, invocationList.size());
        assertEquals("interceptor1-beforeAction:req:resp:sess:someService", invocationList.get(0));
        assertEquals("interceptor1b-beforeAction:req:resp:sess:someService", invocationList.get(1));
        assertEquals("someAction", invocationList.get(2));
        assertEquals("interceptor1-afterAction:req:resp:sess:someService:ok", invocationList.get(3));
        assertEquals("interceptor1b-afterAction:req:resp:sess:someService:ok", invocationList.get(4));
    }

    @Test
    public void testInterceptors6() throws Exception {

        String methodName = "defaultAction";
        String action = "some-action";

        routed = new RoutedRequest(null, action, parametersMap);

        List<String> invocationList = new ArrayList<String>();
        InterceptedController4.invocationList = invocationList;
        Interceptor1.invocationList = invocationList;
        Interceptor1b.invocationList = invocationList;

        SetUp<InterceptedController4> setup = 
                setUp(InterceptedController4.class, methodName, action);
        ActionInvoker invoker = new HttpActionInvoker(req, res, setup.db, routed, injector);

        HTMLPage view = (HTMLPage) invoker.invokeAction(setup.controller, setup.signature);

        assertNotNull(view);
        String h2 = view.getH2Content();
        assertEquals("defaultAction", h2);
        assertEquals(req, setup.controller.getRequest());
        assertEquals(res, setup.controller.getResponse());
        assertEquals(sess, setup.controller.getSession());
        assertNotNull(setup.controller.getSomeService());
        assertEquals(5, invocationList.size());
        assertEquals("interceptor1-beforeAction:req:resp:sess:someService", invocationList.get(0));
        assertEquals("interceptor1b-beforeAction:req:resp:sess:someService", invocationList.get(1));
        assertEquals("defaultAction", invocationList.get(2));
        assertEquals("interceptor1-afterAction:req:resp:sess:someService:ok", invocationList.get(3));
        assertEquals("interceptor1b-afterAction:req:resp:sess:someService:ok", invocationList.get(4));
    }

    @Test
    public void testInterceptors7() throws Exception {

        String methodName = "defaultAction";

        List<String> invocationList = new ArrayList<String>();
        InterceptedController1.invocationList = invocationList;
        Interceptor1.invocationList = invocationList;

        SetUp<InterceptedController1> setup = 
                setUp(InterceptedController1.class, methodName);
        ActionInvoker invoker = new HttpActionInvoker(req, res, setup.db, routed, injector);

        HTMLPage view = (HTMLPage) invoker.invokeAction(setup.controller, setup.signature);

        assertNotNull(view);
        String h2 = view.getH2Content();
        assertEquals("defaultAction", h2);
        assertEquals(req, setup.controller.getRequest());
        assertEquals(res, setup.controller.getResponse());
        assertEquals(sess, setup.controller.getSession());
        assertNotNull(setup.controller.getSomeService());
        assertEquals(3, invocationList.size());
        assertEquals("interceptor1-beforeAction:req:resp:sess:someService", invocationList.get(0));
        assertEquals("defaultAction", invocationList.get(1));
        assertEquals("interceptor1-afterAction:req:resp:sess:someService:ok", invocationList.get(2));
    }

    @Test
    public void testInterceptors8() throws Exception {

        String methodName = "defaultAction";

        List<String> invocationList = new ArrayList<String>();
        InterceptedController3.invocationList = invocationList;
        Interceptor1.invocationList = invocationList;
        Interceptor1b.invocationList = invocationList;

        SetUp<InterceptedController3> setup = 
                setUp(InterceptedController3.class, methodName);
        ActionInvoker invoker = new HttpActionInvoker(req, res, setup.db, routed, injector);

        HTMLPage view = (HTMLPage) invoker.invokeAction(setup.controller, setup.signature);

        assertNotNull(view);
        String h2 = view.getH2Content();
        assertEquals("defaultAction", h2);
        assertEquals(req, setup.controller.getRequest());
        assertEquals(res, setup.controller.getResponse());
        assertEquals(sess, setup.controller.getSession());
        assertNotNull(setup.controller.getSomeService());
        assertEquals(5, invocationList.size());
        assertEquals("interceptor1-beforeAction:req:resp:sess:someService", invocationList.get(0));
        assertEquals("interceptor1b-beforeAction:req:resp:sess:someService", invocationList.get(1));
        assertEquals("defaultAction", invocationList.get(2));
        assertEquals("interceptor1-afterAction:req:resp:sess:someService:ok", invocationList.get(3));
        assertEquals("interceptor1b-afterAction:req:resp:sess:someService:ok", invocationList.get(4));
    }

    @Test
    public void testInterceptors9() throws Exception {

        String methodName = "defaultAction";

        List<String> invocationList = new ArrayList<String>();
        InterceptedController5.invocationList = invocationList;
        Interceptor1.invocationList = invocationList;
        Interceptor1b.invocationList = invocationList;

        SetUp<InterceptedController5> setup = 
                setUp(InterceptedController5.class, methodName);
        ActionInvoker invoker = new HttpActionInvoker(req, res, setup.db, routed, injector);

        HTMLPage view = (HTMLPage) invoker.invokeAction(setup.controller, setup.signature);

        assertNotNull(view);
        String h2 = view.getH2Content();
        assertEquals("defaultAction", h2);
        assertEquals(req, setup.controller.getRequest());
        assertEquals(res, setup.controller.getResponse());
        assertEquals(sess, setup.controller.getSession());
        assertNotNull(setup.controller.getSomeService());
        assertEquals(5, invocationList.size());
        assertEquals("interceptor1-beforeAction:req:resp:sess:someService", invocationList.get(0));
        assertEquals("interceptor1b-beforeAction:req:resp:sess:someService", invocationList.get(1));
        assertEquals("defaultAction", invocationList.get(2));
        assertEquals("interceptor1b-afterAction:req:resp:sess:someService:ok", invocationList.get(3));
        assertEquals("interceptor1-afterAction:req:resp:sess:someService:ok", invocationList.get(4));
    }

    @Test
    public void testInterceptors10() throws Exception {

        String methodName = "someAction";
        String action = "some-action";

        routed = new RoutedRequest(null, action, parametersMap);

        List<String> invocationList = new ArrayList<String>();
        InterceptedController5.invocationList = invocationList;
        Interceptor1.invocationList = invocationList;
        Interceptor1b.invocationList = invocationList;

        SetUp<InterceptedController5> setup = 
                setUp(InterceptedController5.class, methodName, action);
        ActionInvoker invoker = new HttpActionInvoker(req, res, setup.db, routed, injector);

        HTMLPage view = (HTMLPage) invoker.invokeAction(setup.controller, setup.signature);

        assertNotNull(view);
        String h2 = view.getH2Content();
        assertEquals("someAction", h2);
        assertEquals(req, setup.controller.getRequest());
        assertEquals(res, setup.controller.getResponse());
        assertEquals(sess, setup.controller.getSession());
        assertNotNull(setup.controller.getSomeService());
        assertEquals(5, invocationList.size());
        assertEquals("interceptor1-beforeAction:req:resp:sess:someService", invocationList.get(0));
        assertEquals("interceptor1b-beforeAction:req:resp:sess:someService", invocationList.get(1));
        assertEquals("someAction", invocationList.get(2));
        assertEquals("interceptor1b-afterAction:req:resp:sess:someService:ok", invocationList.get(3));
        assertEquals("interceptor1-afterAction:req:resp:sess:someService:ok", invocationList.get(4));
    }

    @Test
    public void testInterceptors11() throws Exception {

        String methodName = "defaultAction";

        List<String> invocationList = new ArrayList<String>();
        InterceptedController6.invocationList = invocationList;
        Interceptor1.invocationList = invocationList;

        SetUp<InterceptedController6> setup = 
                setUp(InterceptedController6.class, methodName);
        ActionInvoker invoker = new HttpActionInvoker(req, res, setup.db, routed, injector);

        HTMLPage view = (HTMLPage) invoker.invokeAction(setup.controller, setup.signature);

        assertNotNull(view);
        String h2 = view.getH2Content();
        assertEquals("defaultAction", h2);
        assertEquals(req, setup.controller.getRequest());
        assertEquals(res, setup.controller.getResponse());
        assertEquals(sess, setup.controller.getSession());
        assertNotNull(setup.controller.getSomeService());
        assertEquals(1, invocationList.size());
        assertEquals("defaultAction", invocationList.get(0));
    }

    @Test
    public void testInterceptors12() throws Exception {

        String methodName = "someAction";
        String action = "some-action";

        when(req.getParameter("actn")).thenReturn("some-action");

        List<String> invocationList = new ArrayList<String>();
        InterceptedController7.invocationList = invocationList;
        Interceptor1.invocationList = invocationList;

        SetUp<InterceptedController7> setup = 
                setUp(InterceptedController7.class, methodName, action);
        ActionInvoker invoker = new HttpActionInvoker(req, res, setup.db, routed, injector);

        HTMLPage view = (HTMLPage) invoker.invokeAction(setup.controller, setup.signature);

        assertNotNull(view);
        String h2 = view.getH2Content();
        assertEquals("someAction", h2);
        assertEquals(req, setup.controller.getRequest());
        assertEquals(res, setup.controller.getResponse());
        assertEquals(sess, setup.controller.getSession());
        assertNotNull(setup.controller.getSomeService());
        assertEquals(1, invocationList.size());
        assertEquals("someAction", invocationList.get(0));
    }

    @Test
    public void testInterceptors13() throws Exception {

        String methodName = "defaultAction";

        List<String> invocationList = new ArrayList<String>();
        InterceptedController8.invocationList = invocationList;
        Interceptor1.invocationList = invocationList;
        Interceptor1b.invocationList = invocationList;
        Interceptor1c.invocationList = invocationList;
        Interceptor1d.invocationList = invocationList;

        SetUp<InterceptedController8> setup = 
                setUp(InterceptedController8.class, methodName);
        ActionInvoker invoker = new HttpActionInvoker(req, res, setup.db, routed, injector);

        HTMLPage view = (HTMLPage) invoker.invokeAction(setup.controller, setup.signature);

        assertNotNull(view);
        String h2 = view.getH2Content();
        assertEquals("defaultAction", h2);
        assertEquals(req, setup.controller.getRequest());
        assertEquals(res, setup.controller.getResponse());
        assertEquals(sess, setup.controller.getSession());
        assertNotNull(setup.controller.getSomeService());
        assertEquals(9, invocationList.size());
        assertEquals("interceptor1-beforeAction:req:resp:sess:someService", invocationList.get(0));
        assertEquals("interceptor1b-beforeAction:req:resp:sess:someService", invocationList.get(1));
        assertEquals("interceptor1c-beforeAction:req:resp:sess:someService", invocationList.get(2));
        assertEquals("interceptor1d-beforeAction:req:resp:sess:someService", invocationList.get(3));
        assertEquals("defaultAction", invocationList.get(4));
        assertEquals("interceptor1c-afterAction:req:resp:sess:someService:ok", invocationList.get(5));
        assertEquals("interceptor1d-afterAction:req:resp:sess:someService:ok", invocationList.get(6));
        assertEquals("interceptor1-afterAction:req:resp:sess:someService:ok", invocationList.get(7));
        assertEquals("interceptor1b-afterAction:req:resp:sess:someService:ok", invocationList.get(8));
    }

    @Test
    public void testInterceptors14() throws Exception {

        String methodName = "someAction";
        String action = "some-action";

        routed = new RoutedRequest(null, action, parametersMap);

        List<String> invocationList = new ArrayList<String>();
        InterceptedController8.invocationList = invocationList;
        Interceptor1.invocationList = invocationList;
        Interceptor1b.invocationList = invocationList;
        Interceptor1c.invocationList = invocationList;
        Interceptor1d.invocationList = invocationList;

        SetUp<InterceptedController8> setup = 
                setUp(InterceptedController8.class, methodName, action);
        ActionInvoker invoker = new HttpActionInvoker(req, res, setup.db, routed, injector);

        HTMLPage view = (HTMLPage) invoker.invokeAction(setup.controller, setup.signature);

        assertNotNull(view);
        String h2 = view.getH2Content();
        assertEquals("someAction", h2);
        assertEquals(req, setup.controller.getRequest());
        assertEquals(res, setup.controller.getResponse());
        assertEquals(sess, setup.controller.getSession());
        assertNotNull(setup.controller.getSomeService());
        assertEquals(9, invocationList.size());
        assertEquals("interceptor1-beforeAction:req:resp:sess:someService", invocationList.get(0));
        assertEquals("interceptor1b-beforeAction:req:resp:sess:someService", invocationList.get(1));
        assertEquals("interceptor1c-beforeAction:req:resp:sess:someService", invocationList.get(2));
        assertEquals("interceptor1d-beforeAction:req:resp:sess:someService", invocationList.get(3));
        assertEquals("someAction", invocationList.get(4));
        assertEquals("interceptor1c-afterAction:req:resp:sess:someService:ok", invocationList.get(5));
        assertEquals("interceptor1d-afterAction:req:resp:sess:someService:ok", invocationList.get(6));
        assertEquals("interceptor1-afterAction:req:resp:sess:someService:ok", invocationList.get(7));
        assertEquals("interceptor1b-afterAction:req:resp:sess:someService:ok", invocationList.get(8));
    }

    @Test
    public void testInterceptors15() throws Exception {

        String methodName = "defaultAction";

        List<String> invocationList = new ArrayList<String>();
        InterceptedController9.invocationList = invocationList;
        Interceptor1.invocationList = invocationList;
        Interceptor1b.invocationList = invocationList;
        Interceptor1c.invocationList = invocationList;
        Interceptor1d.invocationList = invocationList;

        SetUp<InterceptedController9> setup = 
                setUp(InterceptedController9.class, methodName);
        ActionInvoker invoker = new HttpActionInvoker(req, res, setup.db, routed, injector);

        HTMLPage view = (HTMLPage) invoker.invokeAction(setup.controller, setup.signature);

        assertNotNull(view);
        String h2 = view.getH2Content();
        assertEquals("defaultAction", h2);
        assertEquals(req, setup.controller.getRequest());
        assertEquals(res, setup.controller.getResponse());
        assertEquals(sess, setup.controller.getSession());
        assertNotNull(setup.controller.getSomeService());
        assertEquals(11, invocationList.size());
        assertEquals("interceptor1-beforeAction:req:resp:sess:someService", invocationList.get(0));
        assertEquals("interceptor1b-beforeAction:req:resp:sess:someService", invocationList.get(1));
        assertEquals("interceptor1c-beforeAction:req:resp:sess:someService", invocationList.get(2));
        assertEquals("interceptor1d-beforeAction:req:resp:sess:someService", invocationList.get(3));
        assertEquals("beforeAction", invocationList.get(4));
        assertEquals("defaultAction", invocationList.get(5));
        assertEquals("afterAction", invocationList.get(6));
        assertEquals("interceptor1c-afterAction:req:resp:sess:someService:ok", invocationList.get(7));
        assertEquals("interceptor1d-afterAction:req:resp:sess:someService:ok", invocationList.get(8));
        assertEquals("interceptor1-afterAction:req:resp:sess:someService:ok", invocationList.get(9));
        assertEquals("interceptor1b-afterAction:req:resp:sess:someService:ok", invocationList.get(10));
    }

    @Test
    public void testInterceptors16() throws Exception {

        String methodName = "someAction";
        String action = "some-action";

        routed = new RoutedRequest(null, action, parametersMap);

        List<String> invocationList = new ArrayList<String>();
        InterceptedController9.invocationList = invocationList;
        Interceptor1.invocationList = invocationList;
        Interceptor1b.invocationList = invocationList;
        Interceptor1c.invocationList = invocationList;
        Interceptor1d.invocationList = invocationList;

        SetUp<InterceptedController9> setup = 
                setUp(InterceptedController9.class, methodName, action);
        ActionInvoker invoker = new HttpActionInvoker(req, res, setup.db, routed, injector);

        HTMLPage view = (HTMLPage) invoker.invokeAction(setup.controller, setup.signature);

        assertNotNull(view);
        String h2 = view.getH2Content();
        assertEquals("someAction", h2);
        assertEquals(req, setup.controller.getRequest());
        assertEquals(res, setup.controller.getResponse());
        assertEquals(sess, setup.controller.getSession());
        assertNotNull(setup.controller.getSomeService());
        assertEquals(11, invocationList.size());
        assertEquals("interceptor1-beforeAction:req:resp:sess:someService", invocationList.get(0));
        assertEquals("interceptor1b-beforeAction:req:resp:sess:someService", invocationList.get(1));
        assertEquals("interceptor1c-beforeAction:req:resp:sess:someService", invocationList.get(2));
        assertEquals("interceptor1d-beforeAction:req:resp:sess:someService", invocationList.get(3));
        assertEquals("beforeAction", invocationList.get(4));
        assertEquals("someAction", invocationList.get(5));
        assertEquals("afterAction", invocationList.get(6));
        assertEquals("interceptor1c-afterAction:req:resp:sess:someService:ok", invocationList.get(7));
        assertEquals("interceptor1d-afterAction:req:resp:sess:someService:ok", invocationList.get(8));
        assertEquals("interceptor1-afterAction:req:resp:sess:someService:ok", invocationList.get(9));
        assertEquals("interceptor1b-afterAction:req:resp:sess:someService:ok", invocationList.get(10));
    }
    
    /*----------------------------------*/
    
    private <T> SetUp<T> setUp(Class<T> clazz, String methodName) throws IOException {
        return setUp(clazz, methodName, null);
    }
    
    private <T> SetUp<T> setUp(Class<T> clazz, String methodName, String action) throws IOException {
        
        T cntrl = injector.getInstance(clazz);
        Set<Class<?>> controllerClasses = new HashSet<Class<?>>();
        controllerClasses.add(cntrl.getClass());
        ControllerDatabase db = newControllerDatabase(controllerClasses);

        FastClass fastClass = FastClass.create(clazz);
        int fastIndex = fastClass.getIndex(methodName, new Class<?>[] {});
        ActionSignature sig = mock(ActionSignature.class);
        when(sig.fastIndex()).thenReturn(fastIndex);
        when(sig.parameterTypes()).thenReturn(new Class<?>[] {});
        when(sig.methodName()).thenReturn(methodName);
        when(sig.getArgs(parametersMap, req.getInputStream())).thenReturn(new Object[] {});
        
        if (action == null) {
            when(sig.getInterceptorClasses(db, cntrl.getClass(), "")).thenReturn(
                    db.getInterceptorsForDefaultAction(cntrl.getClass()));
        } else {
            when(sig.getInterceptorClasses(db, cntrl.getClass(), action)).thenReturn(
                    db.getInterceptorsForAction(cntrl.getClass(), action));
        }
        
        when(sig.marshall(anyObject())).thenAnswer(new Answer<View>() {
            @Override
            public View answer(InvocationOnMock invocation) throws Throwable {
                return (View)invocation.getArguments()[0];
            }
        });
        
        SetUp<T> result = new SetUp<T>();
        result.controller = cntrl;
        result.db = db;
        result.signature = sig;
        return result;
    }
    
    private ControllerDatabase newControllerDatabase(Set<Class<?>> controllerClasses) {
        return new MappedControllerDatabase(controllerClasses, new RegexRouteMap(), 
                new HashMap<String, EntityMarshaller>());
    }
    
    private static class SetUp<T> {
        public T controller;
        public ControllerDatabase db;
        public ActionSignature signature;
    }
}
