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

import static junit.framework.Assert.*;
import static org.mockito.Mockito.*;

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

import org.junit.Before;
import org.junit.Test;
import org.mojavemvc.core.ActionInvoker;
import org.mojavemvc.core.ActionSignature;
import org.mojavemvc.core.ControllerDatabase;
import org.mojavemvc.core.HttpActionInvoker;
import org.mojavemvc.core.MappedControllerDatabase;
import org.mojavemvc.core.RoutedRequest;
import org.mojavemvc.core.ServletResourceModule;
import org.mojavemvc.tests.controllers.InterceptedController1;
import org.mojavemvc.tests.controllers.InterceptedController2;
import org.mojavemvc.tests.controllers.InterceptedController3;
import org.mojavemvc.tests.controllers.InterceptedController4;
import org.mojavemvc.tests.controllers.InterceptedController5;
import org.mojavemvc.tests.controllers.InterceptedController6;
import org.mojavemvc.tests.controllers.InterceptedController7;
import org.mojavemvc.tests.controllers.InterceptedController8;
import org.mojavemvc.tests.controllers.InterceptedController9;
import org.mojavemvc.tests.controllers.SomeStatelessController;
import org.mojavemvc.tests.interceptors.Interceptor1;
import org.mojavemvc.tests.interceptors.Interceptor1b;
import org.mojavemvc.tests.interceptors.Interceptor1c;
import org.mojavemvc.tests.interceptors.Interceptor1d;
import org.mojavemvc.tests.modules.SomeModule;
import org.mojavemvc.views.JspView;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * @author Luis Antunes
 */
public class TestHttpActionInvoker {

    private HttpServletRequest req;

    private HttpServletResponse resp;

    private HttpSession sess;
    
    private RoutedRequest routed;

    private Injector injector;

    private Map<String, ?> parametersMap = new HashMap<String, Object>();

    @Before
    public void beforeEachTest() throws Exception {

        sess = mock(HttpSession.class);
        req = mock(HttpServletRequest.class);
        when(req.getSession()).thenReturn(sess);
        when(req.getParameterMap()).thenReturn(parametersMap);
        resp = mock(HttpServletResponse.class);

        injector = Guice.createInjector(new ServletResourceModule(), new SomeModule());
        ServletResourceModule.set(req, resp);

//        Field f = FrontController.class.getDeclaredField("ACTION_VARIABLE");
//        f.setAccessible(true);
//        f.set(null, "actn");
//        f = FrontController.class.getDeclaredField("CONTROLLER_VARIABLE");
//        f.setAccessible(true);
//        f.set(null, "cntrl");
        routed = new RoutedRequest(null, null);
    }

    @Test
    public void testInvokeAction() throws Exception {

        String methodName = "doSomething";

        SomeStatelessController cntrl = injector.getInstance(SomeStatelessController.class);
        Set<Class<?>> controllerClasses = new HashSet<Class<?>>();
        controllerClasses.add(cntrl.getClass());
        ControllerDatabase db = new MappedControllerDatabase(controllerClasses);

        FastClass fastClass = FastClass.create(SomeStatelessController.class);
        int fastIndex = fastClass.getIndex(methodName, new Class<?>[] {});
        ActionSignature sig = mock(ActionSignature.class);
        when(sig.fastIndex()).thenReturn(fastIndex);
        when(sig.parameterTypes()).thenReturn(new Class<?>[] {});
        when(sig.methodName()).thenReturn(methodName);
        when(sig.getArgs(parametersMap)).thenReturn(new Object[] {});
        when(sig.getInterceptorClasses(db, cntrl.getClass(), "")).thenReturn(
                db.getInterceptorsForDefaultAction(cntrl.getClass()));

        ActionInvoker invoker = new HttpActionInvoker(req, resp, db, routed, injector);

        JspView view = (JspView) invoker.invokeAction(cntrl, sig);

        assertNotNull(view);
        String var = (String) view.getAttribute("var");
        assertEquals("test", var);
        assertEquals(req, cntrl.getRequest());
        assertEquals(resp, cntrl.getResponse());
        assertEquals(sess, cntrl.getSession());
        assertEquals(1, cntrl.beforeInvokeCount);
        assertEquals(1, cntrl.afterInvokeCount);
    }

    @Test
    public void testInvokeDefaultAction() throws Exception {

        String methodName = "defaultAction";

        SomeStatelessController cntrl = injector.getInstance(SomeStatelessController.class);
        Set<Class<?>> controllerClasses = new HashSet<Class<?>>();
        controllerClasses.add(cntrl.getClass());
        ControllerDatabase db = new MappedControllerDatabase(controllerClasses);

        FastClass fastClass = FastClass.create(SomeStatelessController.class);
        int fastIndex = fastClass.getIndex(methodName, new Class<?>[] {});
        ActionSignature sig = mock(ActionSignature.class);
        when(sig.fastIndex()).thenReturn(fastIndex);
        when(sig.parameterTypes()).thenReturn(new Class<?>[] {});
        when(sig.methodName()).thenReturn(methodName);
        when(sig.getArgs(parametersMap)).thenReturn(new Object[] {});
        when(sig.getInterceptorClasses(db, cntrl.getClass(), "")).thenReturn(
                db.getInterceptorsForDefaultAction(cntrl.getClass()));

        ActionInvoker invoker = new HttpActionInvoker(req, resp, db, routed, injector);

        JspView view = (JspView) invoker.invokeAction(cntrl, sig);

        assertNotNull(view);
        String var = (String) view.getAttribute("var");
        assertEquals("default", var);
        assertEquals(req, cntrl.getRequest());
        assertEquals(resp, cntrl.getResponse());
        assertEquals(sess, cntrl.getSession());
        assertEquals(1, cntrl.beforeInvokeCount);
        assertEquals(1, cntrl.afterInvokeCount);
    }

    @Test
    public void testInterceptors1() throws Exception {

        String methodName = "someAction";

        List<String> invocationList = new ArrayList<String>();
        InterceptedController1.invocationList = invocationList;
        Interceptor1.invocationList = invocationList;

        InterceptedController1 cntrl = injector.getInstance(InterceptedController1.class);
        Set<Class<?>> controllerClasses = new HashSet<Class<?>>();
        controllerClasses.add(cntrl.getClass());
        ControllerDatabase db = new MappedControllerDatabase(controllerClasses);

        FastClass fastClass = FastClass.create(InterceptedController1.class);
        int fastIndex = fastClass.getIndex(methodName, new Class<?>[] {});
        ActionSignature sig = mock(ActionSignature.class);
        when(sig.fastIndex()).thenReturn(fastIndex);
        when(sig.parameterTypes()).thenReturn(new Class<?>[] {});
        when(sig.methodName()).thenReturn(methodName);
        when(sig.getArgs(parametersMap)).thenReturn(new Object[] {});
        when(sig.getInterceptorClasses(db, cntrl.getClass(), "")).thenReturn(
                db.getInterceptorsForAction(cntrl.getClass(), "some-action"));

        ActionInvoker invoker = new HttpActionInvoker(req, resp, db, routed, injector);

        JspView view = (JspView) invoker.invokeAction(cntrl, sig);

        assertNotNull(view);
        String var = (String) view.getAttribute("var");
        assertEquals("someAction", var);
        assertEquals(req, cntrl.getRequest());
        assertEquals(resp, cntrl.getResponse());
        assertEquals(sess, cntrl.getSession());
        assertNotNull(cntrl.getSomeService());
        assertEquals(3, invocationList.size());
        assertEquals("interceptor1-beforeAction:req:resp:sess:someService", invocationList.get(0));
        assertEquals("someAction", invocationList.get(1));
        assertEquals("interceptor1-afterAction:req:resp:sess:someService:ok", invocationList.get(2));
    }

    @Test
    public void testInterceptors2() throws Exception {

        String methodName = "someAction";
        String action = "some-action";

        routed = new RoutedRequest(null, action);

        List<String> invocationList = new ArrayList<String>();
        InterceptedController2.invocationList = invocationList;
        Interceptor1.invocationList = invocationList;

        InterceptedController2 cntrl = injector.getInstance(InterceptedController2.class);
        Set<Class<?>> controllerClasses = new HashSet<Class<?>>();
        controllerClasses.add(cntrl.getClass());
        ControllerDatabase db = new MappedControllerDatabase(controllerClasses);

        FastClass fastClass = FastClass.create(InterceptedController2.class);
        int fastIndex = fastClass.getIndex(methodName, new Class<?>[] {});
        ActionSignature sig = mock(ActionSignature.class);
        when(sig.fastIndex()).thenReturn(fastIndex);
        when(sig.parameterTypes()).thenReturn(new Class<?>[] {});
        when(sig.methodName()).thenReturn(methodName);
        when(sig.getArgs(parametersMap)).thenReturn(new Object[] {});
        when(sig.getInterceptorClasses(db, cntrl.getClass(), action)).thenReturn(
                db.getInterceptorsForAction(cntrl.getClass(), action));

        ActionInvoker invoker = new HttpActionInvoker(req, resp, db, routed, injector);

        JspView view = (JspView) invoker.invokeAction(cntrl, sig);

        assertNotNull(view);
        String var = (String) view.getAttribute("var");
        assertEquals("someAction", var);
        assertEquals(req, cntrl.getRequest());
        assertEquals(resp, cntrl.getResponse());
        assertEquals(sess, cntrl.getSession());
        assertNotNull(cntrl.getSomeService());
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

        InterceptedController2 cntrl = injector.getInstance(InterceptedController2.class);
        Set<Class<?>> controllerClasses = new HashSet<Class<?>>();
        controllerClasses.add(cntrl.getClass());
        ControllerDatabase db = new MappedControllerDatabase(controllerClasses);

        FastClass fastClass = FastClass.create(InterceptedController2.class);
        int fastIndex = fastClass.getIndex(methodName, new Class<?>[] {});
        ActionSignature sig = mock(ActionSignature.class);
        when(sig.fastIndex()).thenReturn(fastIndex);
        when(sig.parameterTypes()).thenReturn(new Class<?>[] {});
        when(sig.methodName()).thenReturn(methodName);
        when(sig.getArgs(parametersMap)).thenReturn(new Object[] {});
        when(sig.getInterceptorClasses(db, cntrl.getClass(), "")).thenReturn(
                db.getInterceptorsForDefaultAction(cntrl.getClass()));

        ActionInvoker invoker = new HttpActionInvoker(req, resp, db, routed, injector);

        JspView view = (JspView) invoker.invokeAction(cntrl, sig);

        assertNotNull(view);
        String var = (String) view.getAttribute("var");
        assertEquals("defaultAction", var);
        assertEquals(req, cntrl.getRequest());
        assertEquals(resp, cntrl.getResponse());
        assertEquals(sess, cntrl.getSession());
        assertNotNull(cntrl.getSomeService());
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

        InterceptedController3 cntrl = injector.getInstance(InterceptedController3.class);
        Set<Class<?>> controllerClasses = new HashSet<Class<?>>();
        controllerClasses.add(cntrl.getClass());
        ControllerDatabase db = new MappedControllerDatabase(controllerClasses);

        FastClass fastClass = FastClass.create(InterceptedController3.class);
        int fastIndex = fastClass.getIndex(methodName, new Class<?>[] {});
        ActionSignature sig = mock(ActionSignature.class);
        when(sig.fastIndex()).thenReturn(fastIndex);
        when(sig.parameterTypes()).thenReturn(new Class<?>[] {});
        when(sig.methodName()).thenReturn(methodName);
        when(sig.getArgs(parametersMap)).thenReturn(new Object[] {});
        when(sig.getInterceptorClasses(db, cntrl.getClass(), "")).thenReturn(
                db.getInterceptorsForAction(cntrl.getClass(), "some-action"));

        ActionInvoker invoker = new HttpActionInvoker(req, resp, db, routed, injector);

        JspView view = (JspView) invoker.invokeAction(cntrl, sig);

        assertNotNull(view);
        String var = (String) view.getAttribute("var");
        assertEquals("someAction", var);
        assertEquals(req, cntrl.getRequest());
        assertEquals(resp, cntrl.getResponse());
        assertEquals(sess, cntrl.getSession());
        assertNotNull(cntrl.getSomeService());
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

        routed = new RoutedRequest(null, action);

        List<String> invocationList = new ArrayList<String>();
        InterceptedController4.invocationList = invocationList;
        Interceptor1.invocationList = invocationList;
        Interceptor1b.invocationList = invocationList;

        InterceptedController4 cntrl = injector.getInstance(InterceptedController4.class);
        Set<Class<?>> controllerClasses = new HashSet<Class<?>>();
        controllerClasses.add(cntrl.getClass());
        ControllerDatabase db = new MappedControllerDatabase(controllerClasses);

        FastClass fastClass = FastClass.create(InterceptedController4.class);
        int fastIndex = fastClass.getIndex(methodName, new Class<?>[] {});
        ActionSignature sig = mock(ActionSignature.class);
        when(sig.fastIndex()).thenReturn(fastIndex);
        when(sig.parameterTypes()).thenReturn(new Class<?>[] {});
        when(sig.methodName()).thenReturn(methodName);
        when(sig.getArgs(parametersMap)).thenReturn(new Object[] {});
        when(sig.getInterceptorClasses(db, cntrl.getClass(), action)).thenReturn(
                db.getInterceptorsForAction(cntrl.getClass(), action));

        ActionInvoker invoker = new HttpActionInvoker(req, resp, db, routed, injector);

        JspView view = (JspView) invoker.invokeAction(cntrl, sig);

        assertNotNull(view);
        String var = (String) view.getAttribute("var");
        assertEquals("someAction", var);
        assertEquals(req, cntrl.getRequest());
        assertEquals(resp, cntrl.getResponse());
        assertEquals(sess, cntrl.getSession());
        assertNotNull(cntrl.getSomeService());
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

        routed = new RoutedRequest(null, action);

        List<String> invocationList = new ArrayList<String>();
        InterceptedController4.invocationList = invocationList;
        Interceptor1.invocationList = invocationList;
        Interceptor1b.invocationList = invocationList;

        InterceptedController4 cntrl = injector.getInstance(InterceptedController4.class);
        Set<Class<?>> controllerClasses = new HashSet<Class<?>>();
        controllerClasses.add(cntrl.getClass());
        ControllerDatabase db = new MappedControllerDatabase(controllerClasses);

        FastClass fastClass = FastClass.create(InterceptedController4.class);
        int fastIndex = fastClass.getIndex(methodName, new Class<?>[] {});
        ActionSignature sig = mock(ActionSignature.class);
        when(sig.fastIndex()).thenReturn(fastIndex);
        when(sig.parameterTypes()).thenReturn(new Class<?>[] {});
        when(sig.methodName()).thenReturn(methodName);
        when(sig.getArgs(parametersMap)).thenReturn(new Object[] {});
        when(sig.getInterceptorClasses(db, cntrl.getClass(), action)).thenReturn(
                db.getInterceptorsForDefaultAction(cntrl.getClass()));

        ActionInvoker invoker = new HttpActionInvoker(req, resp, db, routed, injector);

        JspView view = (JspView) invoker.invokeAction(cntrl, sig);

        assertNotNull(view);
        String var = (String) view.getAttribute("var");
        assertEquals("defaultAction", var);
        assertEquals(req, cntrl.getRequest());
        assertEquals(resp, cntrl.getResponse());
        assertEquals(sess, cntrl.getSession());
        assertNotNull(cntrl.getSomeService());
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

        InterceptedController1 cntrl = injector.getInstance(InterceptedController1.class);
        Set<Class<?>> controllerClasses = new HashSet<Class<?>>();
        controllerClasses.add(cntrl.getClass());
        ControllerDatabase db = new MappedControllerDatabase(controllerClasses);

        FastClass fastClass = FastClass.create(InterceptedController1.class);
        int fastIndex = fastClass.getIndex(methodName, new Class<?>[] {});
        ActionSignature sig = mock(ActionSignature.class);
        when(sig.fastIndex()).thenReturn(fastIndex);
        when(sig.parameterTypes()).thenReturn(new Class<?>[] {});
        when(sig.methodName()).thenReturn(methodName);
        when(sig.getArgs(parametersMap)).thenReturn(new Object[] {});
        when(sig.getInterceptorClasses(db, cntrl.getClass(), "")).thenReturn(
                db.getInterceptorsForDefaultAction(cntrl.getClass()));

        ActionInvoker invoker = new HttpActionInvoker(req, resp, db, routed, injector);

        JspView view = (JspView) invoker.invokeAction(cntrl, sig);

        assertNotNull(view);
        String var = (String) view.getAttribute("var");
        assertEquals("defaultAction", var);
        assertEquals(req, cntrl.getRequest());
        assertEquals(resp, cntrl.getResponse());
        assertEquals(sess, cntrl.getSession());
        assertNotNull(cntrl.getSomeService());
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

        InterceptedController3 cntrl = injector.getInstance(InterceptedController3.class);
        Set<Class<?>> controllerClasses = new HashSet<Class<?>>();
        controllerClasses.add(cntrl.getClass());
        ControllerDatabase db = new MappedControllerDatabase(controllerClasses);

        FastClass fastClass = FastClass.create(InterceptedController3.class);
        int fastIndex = fastClass.getIndex(methodName, new Class<?>[] {});
        ActionSignature sig = mock(ActionSignature.class);
        when(sig.fastIndex()).thenReturn(fastIndex);
        when(sig.parameterTypes()).thenReturn(new Class<?>[] {});
        when(sig.methodName()).thenReturn(methodName);
        when(sig.getArgs(parametersMap)).thenReturn(new Object[] {});
        when(sig.getInterceptorClasses(db, cntrl.getClass(), "")).thenReturn(
                db.getInterceptorsForDefaultAction(cntrl.getClass()));

        ActionInvoker invoker = new HttpActionInvoker(req, resp, db, routed, injector);

        JspView view = (JspView) invoker.invokeAction(cntrl, sig);

        assertNotNull(view);
        String var = (String) view.getAttribute("var");
        assertEquals("defaultAction", var);
        assertEquals(req, cntrl.getRequest());
        assertEquals(resp, cntrl.getResponse());
        assertEquals(sess, cntrl.getSession());
        assertNotNull(cntrl.getSomeService());
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

        InterceptedController5 cntrl = injector.getInstance(InterceptedController5.class);
        Set<Class<?>> controllerClasses = new HashSet<Class<?>>();
        controllerClasses.add(cntrl.getClass());
        ControllerDatabase db = new MappedControllerDatabase(controllerClasses);

        FastClass fastClass = FastClass.create(InterceptedController5.class);
        int fastIndex = fastClass.getIndex(methodName, new Class<?>[] {});
        ActionSignature sig = mock(ActionSignature.class);
        when(sig.fastIndex()).thenReturn(fastIndex);
        when(sig.parameterTypes()).thenReturn(new Class<?>[] {});
        when(sig.methodName()).thenReturn(methodName);
        when(sig.getArgs(parametersMap)).thenReturn(new Object[] {});
        when(sig.getInterceptorClasses(db, cntrl.getClass(), "")).thenReturn(
                db.getInterceptorsForDefaultAction(cntrl.getClass()));

        ActionInvoker invoker = new HttpActionInvoker(req, resp, db, routed, injector);

        JspView view = (JspView) invoker.invokeAction(cntrl, sig);

        assertNotNull(view);
        String var = (String) view.getAttribute("var");
        assertEquals("defaultAction", var);
        assertEquals(req, cntrl.getRequest());
        assertEquals(resp, cntrl.getResponse());
        assertEquals(sess, cntrl.getSession());
        assertNotNull(cntrl.getSomeService());
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

        routed = new RoutedRequest(null, action);

        List<String> invocationList = new ArrayList<String>();
        InterceptedController5.invocationList = invocationList;
        Interceptor1.invocationList = invocationList;
        Interceptor1b.invocationList = invocationList;

        InterceptedController5 cntrl = injector.getInstance(InterceptedController5.class);
        Set<Class<?>> controllerClasses = new HashSet<Class<?>>();
        controllerClasses.add(cntrl.getClass());
        ControllerDatabase db = new MappedControllerDatabase(controllerClasses);

        FastClass fastClass = FastClass.create(InterceptedController5.class);
        int fastIndex = fastClass.getIndex(methodName, new Class<?>[] {});
        ActionSignature sig = mock(ActionSignature.class);
        when(sig.fastIndex()).thenReturn(fastIndex);
        when(sig.parameterTypes()).thenReturn(new Class<?>[] {});
        when(sig.methodName()).thenReturn(methodName);
        when(sig.getArgs(parametersMap)).thenReturn(new Object[] {});
        when(sig.getInterceptorClasses(db, cntrl.getClass(), action)).thenReturn(
                db.getInterceptorsForAction(cntrl.getClass(), action));

        ActionInvoker invoker = new HttpActionInvoker(req, resp, db, routed, injector);

        JspView view = (JspView) invoker.invokeAction(cntrl, sig);

        assertNotNull(view);
        String var = (String) view.getAttribute("var");
        assertEquals("someAction", var);
        assertEquals(req, cntrl.getRequest());
        assertEquals(resp, cntrl.getResponse());
        assertEquals(sess, cntrl.getSession());
        assertNotNull(cntrl.getSomeService());
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

        InterceptedController6 cntrl = injector.getInstance(InterceptedController6.class);
        Set<Class<?>> controllerClasses = new HashSet<Class<?>>();
        controllerClasses.add(cntrl.getClass());
        ControllerDatabase db = new MappedControllerDatabase(controllerClasses);

        FastClass fastClass = FastClass.create(InterceptedController6.class);
        int fastIndex = fastClass.getIndex(methodName, new Class<?>[] {});
        ActionSignature sig = mock(ActionSignature.class);
        when(sig.fastIndex()).thenReturn(fastIndex);
        when(sig.parameterTypes()).thenReturn(new Class<?>[] {});
        when(sig.methodName()).thenReturn(methodName);
        when(sig.getArgs(parametersMap)).thenReturn(new Object[] {});
        when(sig.getInterceptorClasses(db, cntrl.getClass(), "")).thenReturn(
                db.getInterceptorsForDefaultAction(cntrl.getClass()));

        ActionInvoker invoker = new HttpActionInvoker(req, resp, db, routed, injector);

        JspView view = (JspView) invoker.invokeAction(cntrl, sig);

        assertNotNull(view);
        String var = (String) view.getAttribute("var");
        assertEquals("defaultAction", var);
        assertEquals(req, cntrl.getRequest());
        assertEquals(resp, cntrl.getResponse());
        assertEquals(sess, cntrl.getSession());
        assertNotNull(cntrl.getSomeService());
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

        InterceptedController7 cntrl = injector.getInstance(InterceptedController7.class);
        Set<Class<?>> controllerClasses = new HashSet<Class<?>>();
        controllerClasses.add(cntrl.getClass());
        ControllerDatabase db = new MappedControllerDatabase(controllerClasses);

        FastClass fastClass = FastClass.create(InterceptedController7.class);
        int fastIndex = fastClass.getIndex(methodName, new Class<?>[] {});
        ActionSignature sig = mock(ActionSignature.class);
        when(sig.fastIndex()).thenReturn(fastIndex);
        when(sig.parameterTypes()).thenReturn(new Class<?>[] {});
        when(sig.methodName()).thenReturn(methodName);
        when(sig.getArgs(parametersMap)).thenReturn(new Object[] {});
        when(sig.getInterceptorClasses(db, cntrl.getClass(), action)).thenReturn(
                db.getInterceptorsForAction(cntrl.getClass(), action));

        ActionInvoker invoker = new HttpActionInvoker(req, resp, db, routed, injector);

        JspView view = (JspView) invoker.invokeAction(cntrl, sig);

        assertNotNull(view);
        String var = (String) view.getAttribute("var");
        assertEquals("someAction", var);
        assertEquals(req, cntrl.getRequest());
        assertEquals(resp, cntrl.getResponse());
        assertEquals(sess, cntrl.getSession());
        assertNotNull(cntrl.getSomeService());
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

        InterceptedController8 cntrl = injector.getInstance(InterceptedController8.class);
        Set<Class<?>> controllerClasses = new HashSet<Class<?>>();
        controllerClasses.add(cntrl.getClass());
        ControllerDatabase db = new MappedControllerDatabase(controllerClasses);

        FastClass fastClass = FastClass.create(InterceptedController8.class);
        int fastIndex = fastClass.getIndex(methodName, new Class<?>[] {});
        ActionSignature sig = mock(ActionSignature.class);
        when(sig.fastIndex()).thenReturn(fastIndex);
        when(sig.parameterTypes()).thenReturn(new Class<?>[] {});
        when(sig.methodName()).thenReturn(methodName);
        when(sig.getArgs(parametersMap)).thenReturn(new Object[] {});
        when(sig.getInterceptorClasses(db, cntrl.getClass(), "")).thenReturn(
                db.getInterceptorsForDefaultAction(cntrl.getClass()));

        ActionInvoker invoker = new HttpActionInvoker(req, resp, db, routed, injector);

        JspView view = (JspView) invoker.invokeAction(cntrl, sig);

        assertNotNull(view);
        String var = (String) view.getAttribute("var");
        assertEquals("defaultAction", var);
        assertEquals(req, cntrl.getRequest());
        assertEquals(resp, cntrl.getResponse());
        assertEquals(sess, cntrl.getSession());
        assertNotNull(cntrl.getSomeService());
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

        routed = new RoutedRequest(null, action);

        List<String> invocationList = new ArrayList<String>();
        InterceptedController8.invocationList = invocationList;
        Interceptor1.invocationList = invocationList;
        Interceptor1b.invocationList = invocationList;
        Interceptor1c.invocationList = invocationList;
        Interceptor1d.invocationList = invocationList;

        InterceptedController8 cntrl = injector.getInstance(InterceptedController8.class);
        Set<Class<?>> controllerClasses = new HashSet<Class<?>>();
        controllerClasses.add(cntrl.getClass());
        ControllerDatabase db = new MappedControllerDatabase(controllerClasses);

        FastClass fastClass = FastClass.create(InterceptedController8.class);
        int fastIndex = fastClass.getIndex(methodName, new Class<?>[] {});
        ActionSignature sig = mock(ActionSignature.class);
        when(sig.fastIndex()).thenReturn(fastIndex);
        when(sig.parameterTypes()).thenReturn(new Class<?>[] {});
        when(sig.methodName()).thenReturn(methodName);
        when(sig.getArgs(parametersMap)).thenReturn(new Object[] {});
        when(sig.getInterceptorClasses(db, cntrl.getClass(), action)).thenReturn(
                db.getInterceptorsForAction(cntrl.getClass(), action));

        ActionInvoker invoker = new HttpActionInvoker(req, resp, db, routed, injector);

        JspView view = (JspView) invoker.invokeAction(cntrl, sig);

        assertNotNull(view);
        String var = (String) view.getAttribute("var");
        assertEquals("someAction", var);
        assertEquals(req, cntrl.getRequest());
        assertEquals(resp, cntrl.getResponse());
        assertEquals(sess, cntrl.getSession());
        assertNotNull(cntrl.getSomeService());
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

        InterceptedController9 cntrl = injector.getInstance(InterceptedController9.class);
        Set<Class<?>> controllerClasses = new HashSet<Class<?>>();
        controllerClasses.add(cntrl.getClass());
        ControllerDatabase db = new MappedControllerDatabase(controllerClasses);

        FastClass fastClass = FastClass.create(InterceptedController9.class);
        int fastIndex = fastClass.getIndex(methodName, new Class<?>[] {});
        ActionSignature sig = mock(ActionSignature.class);
        when(sig.fastIndex()).thenReturn(fastIndex);
        when(sig.parameterTypes()).thenReturn(new Class<?>[] {});
        when(sig.methodName()).thenReturn(methodName);
        when(sig.getArgs(parametersMap)).thenReturn(new Object[] {});
        when(sig.getInterceptorClasses(db, cntrl.getClass(), "")).thenReturn(
                db.getInterceptorsForDefaultAction(cntrl.getClass()));

        ActionInvoker invoker = new HttpActionInvoker(req, resp, db, routed, injector);

        JspView view = (JspView) invoker.invokeAction(cntrl, sig);

        assertNotNull(view);
        String var = (String) view.getAttribute("var");
        assertEquals("defaultAction", var);
        assertEquals(req, cntrl.getRequest());
        assertEquals(resp, cntrl.getResponse());
        assertEquals(sess, cntrl.getSession());
        assertNotNull(cntrl.getSomeService());
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

        routed = new RoutedRequest(null, action);

        List<String> invocationList = new ArrayList<String>();
        InterceptedController9.invocationList = invocationList;
        Interceptor1.invocationList = invocationList;
        Interceptor1b.invocationList = invocationList;
        Interceptor1c.invocationList = invocationList;
        Interceptor1d.invocationList = invocationList;

        InterceptedController9 cntrl = injector.getInstance(InterceptedController9.class);
        Set<Class<?>> controllerClasses = new HashSet<Class<?>>();
        controllerClasses.add(cntrl.getClass());
        ControllerDatabase db = new MappedControllerDatabase(controllerClasses);

        FastClass fastClass = FastClass.create(InterceptedController9.class);
        int fastIndex = fastClass.getIndex(methodName, new Class<?>[] {});
        ActionSignature sig = mock(ActionSignature.class);
        when(sig.fastIndex()).thenReturn(fastIndex);
        when(sig.parameterTypes()).thenReturn(new Class<?>[] {});
        when(sig.methodName()).thenReturn(methodName);
        when(sig.getArgs(parametersMap)).thenReturn(new Object[] {});
        when(sig.getInterceptorClasses(db, cntrl.getClass(), action)).thenReturn(
                db.getInterceptorsForAction(cntrl.getClass(), action));

        ActionInvoker invoker = new HttpActionInvoker(req, resp, db, routed, injector);

        JspView view = (JspView) invoker.invokeAction(cntrl, sig);

        assertNotNull(view);
        String var = (String) view.getAttribute("var");
        assertEquals("someAction", var);
        assertEquals(req, cntrl.getRequest());
        assertEquals(resp, cntrl.getResponse());
        assertEquals(sess, cntrl.getSession());
        assertNotNull(cntrl.getSomeService());
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
}
