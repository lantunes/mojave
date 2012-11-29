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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sf.cglib.reflect.FastClass;

import org.junit.Test;
import org.mojavemvc.annotations.Action;
import org.mojavemvc.annotations.AfterAction;
import org.mojavemvc.annotations.AfterConstruct;
import org.mojavemvc.annotations.BeforeAction;
import org.mojavemvc.annotations.DELETEAction;
import org.mojavemvc.annotations.DefaultAction;
import org.mojavemvc.annotations.DefaultController;
import org.mojavemvc.annotations.GETAction;
import org.mojavemvc.annotations.HEADAction;
import org.mojavemvc.annotations.Init;
import org.mojavemvc.annotations.InterceptedBy;
import org.mojavemvc.annotations.OPTIONSAction;
import org.mojavemvc.annotations.POSTAction;
import org.mojavemvc.annotations.PUTAction;
import org.mojavemvc.annotations.Param;
import org.mojavemvc.annotations.ParamPath;
import org.mojavemvc.annotations.SingletonController;
import org.mojavemvc.annotations.StatefulController;
import org.mojavemvc.annotations.StatelessController;
import org.mojavemvc.annotations.TRACEAction;
import org.mojavemvc.core.ActionSignature;
import org.mojavemvc.core.ControllerDatabase;
import org.mojavemvc.core.DefaultActionSignature;
import org.mojavemvc.core.HttpMethod;
import org.mojavemvc.core.HttpMethodActionSignature;
import org.mojavemvc.core.MappedControllerDatabase;
import org.mojavemvc.core.Route;
import org.mojavemvc.core.RouteMap;
import org.mojavemvc.exception.ConfigurationException;
import org.mojavemvc.views.JSP;
import org.mojavemvc.views.View;
import org.mojavemvc.views.XML;

/**
 * 
 * @author Luis Antunes
 */
@SuppressWarnings("unused")
public class TestMappedControllerDatabase {

    @Test
    public void testConstruct() {

        Set<Class<?>> controllerClasses = new HashSet<Class<?>>();
        controllerClasses.add(TestController.class);
        controllerClasses.add(TestStartupController.class);
        FakeRouteMap rm = new FakeRouteMap();
        ControllerDatabase db = new MappedControllerDatabase(controllerClasses, rm);

        FastClass fc = db.getFastClass(TestController.class);
        assertNotNull(fc);
        assertEquals(TestController.class, fc.getJavaClass());
        fc = db.getFastClass(TestStartupController.class);
        assertNotNull(fc);
        assertEquals(TestStartupController.class, fc.getJavaClass());

        assertEquals(TestController.class, db.getControllerClass("test"));
        assertEquals(TestStartupController.class, db.getControllerClass("startup"));
        assertEquals(TestController.class, db.getDefaultControllerClass());

        ActionSignature sig = db.getActionMethodSignature(TestController.class, "test1");
        assertEquals("testAction", sig.methodName());
        sig = db.getActionMethodSignature(TestController.class, "with-param");
        assertEquals("withParamAction", sig.methodName());
        sig = db.getActionMethodSignature(TestController.class, "another-param");
        assertEquals("anotherParamAction", sig.methodName());
        sig = db.getActionMethodSignature(TestController.class, "some-service");
        assertEquals("someServiceAction", sig.methodName());
        sig = db.getActionMethodSignature(TestController.class, "test-annotation");
        assertEquals("doAnnotationTest", sig.methodName());

        ActionSignature beforeActionMethod = db.getBeforeActionMethodFor(TestController.class);
        assertNotNull(beforeActionMethod);
        assertEquals("doSomethingBefore", beforeActionMethod.methodName());

        ActionSignature afterActionMethod = db.getAfterActionMethodFor(TestController.class);
        assertNotNull(afterActionMethod);
        assertEquals("doSomethingAfter", afterActionMethod.methodName());

        ActionSignature defaultActionSignature = db.getDefaultActionMethodFor(TestController.class);
        assertNotNull(defaultActionSignature);
        assertTrue(defaultActionSignature instanceof DefaultActionSignature);
        assertEquals("defaultAction", defaultActionSignature.methodName());

        ActionSignature afterConstructMethod = db.getAfterConstructMethodFor(TestController.class);
        assertNotNull(afterConstructMethod);
        assertEquals("init", afterConstructMethod.methodName());

        afterConstructMethod = db.getAfterConstructMethodFor(TestStartupController.class);
        assertNotNull(afterConstructMethod);
        assertEquals("init2", afterConstructMethod.methodName());

        Set<Class<?>> initControllers = db.getInitControllers();
        assertNotNull(initControllers);
        assertEquals(1, initControllers.size());
        assertEquals(TestStartupController.class, initControllers.iterator().next());
        
        assertEquals(12, rm.size());
        assertTrue(rm.contains(new Route(null, null, null)));
        assertTrue(rm.contains(new Route("test", null, null)));
        assertTrue(rm.contains(new Route("test", "test1", null)));
        assertTrue(rm.contains(new Route("test", "with-param", null)));
        assertTrue(rm.contains(new Route("test", "another-param", null)));
        assertTrue(rm.contains(new Route("test", "some-service", null)));
        assertTrue(rm.contains(new Route("test", "test-annotation", null)));
        assertTrue(rm.contains(new Route(null, "test1", null)));
        assertTrue(rm.contains(new Route(null, "with-param", null)));
        assertTrue(rm.contains(new Route(null, "another-param", null)));
        assertTrue(rm.contains(new Route(null, "some-service", null)));
        assertTrue(rm.contains(new Route(null, "test-annotation", null)));
    }

    @Test
    public void testConstructWithInheritance() {

        // TODO add support for controller inheritance
        // Set<Class<?>> controllerClasses = new HashSet<Class<?>>( );
        // controllerClasses.add( TestChildClass.class );
        // ControllerDatabase db = new MappedControllerDatabase(
        // controllerClasses );
        //
        // assertEquals( TestChildClass.class, db.getControllerClass("child"));
        // assertEquals( TestChildClass.class, db.getDefaultControllerClass( ));
        //
        // ActionSignature sig =
        // db.getActionMethodSignature(TestChildClass.class, "test");
        // assertEquals( "testAction", sig.getMethodName() );
        //
        // Method defaultActionMethod = db.getDefaultActionMethodFor(
        // TestChildClass.class );
        // assertNotNull( defaultActionMethod );
        // assertEquals( "defaultAction", defaultActionMethod.getName( ) );
        //
        // Method beforeActionMethod = db.getBeforeActionMethodFor(
        // TestChildClass.class );
        // assertNotNull( beforeActionMethod );
        // assertEquals( "doSomethingBefore", beforeActionMethod.getName( ) );
    }

    @Test
    public void testInvalidInterceptors1() {

        try {
            Set<Class<?>> controllerClasses = new HashSet<Class<?>>();
            controllerClasses.add(TestInvalidInterceptorController1.class);
            new MappedControllerDatabase(controllerClasses, new FakeRouteMap());
            fail("should have thrown exception");
        } catch (Exception e) {
            if (!(e instanceof ConfigurationException)) {
                fail("wrong exception type");
            }
        }
    }

    @Test
    public void testInvalidInterceptors2() {

        try {
            Set<Class<?>> controllerClasses = new HashSet<Class<?>>();
            controllerClasses.add(TestInvalidInterceptorController2.class);
            new MappedControllerDatabase(controllerClasses, new FakeRouteMap());
            fail("should have thrown exception");
        } catch (Exception e) {
            if (!(e instanceof ConfigurationException)) {
                fail("wrong exception type");
            }
        }
    }

    @Test
    public void testInvalidInterceptors3() {

        try {
            Set<Class<?>> controllerClasses = new HashSet<Class<?>>();
            controllerClasses.add(TestInvalidInterceptorController3.class);
            new MappedControllerDatabase(controllerClasses, new FakeRouteMap());
            fail("should have thrown exception");
        } catch (Exception e) {
            if (!(e instanceof ConfigurationException)) {
                fail("wrong exception type");
            }
        }
    }

    @Test
    public void testInvalidInterceptors4() {

        try {
            Set<Class<?>> controllerClasses = new HashSet<Class<?>>();
            controllerClasses.add(TestInvalidInterceptorController4.class);
            new MappedControllerDatabase(controllerClasses, new FakeRouteMap());
            fail("should have thrown exception");
        } catch (Exception e) {
            if (!(e instanceof ConfigurationException)) {
                fail("wrong exception type");
            }
        }
    }

    @Test
    public void testInvalidInterceptors5() {

        try {
            Set<Class<?>> controllerClasses = new HashSet<Class<?>>();
            controllerClasses.add(TestInvalidInterceptorController5.class);
            new MappedControllerDatabase(controllerClasses, new FakeRouteMap());
            fail("should have thrown exception");
        } catch (Exception e) {
            if (!(e instanceof ConfigurationException)) {
                fail("wrong exception type");
            }
        }
    }

    @Test
    public void testInterceptor1() {

        Set<Class<?>> controllerClasses = new HashSet<Class<?>>();
        controllerClasses.add(TestInterceptorController1.class);
        FakeRouteMap rm = new FakeRouteMap();
        ControllerDatabase db = new MappedControllerDatabase(controllerClasses, rm);

        FastClass fc = db.getFastClass(TestInterceptorController1.class);
        assertNotNull(fc);
        assertEquals(TestInterceptorController1.class, fc.getJavaClass());
        fc = db.getFastClass(Interceptor1.class);
        assertNotNull(fc);
        assertEquals(Interceptor1.class, fc.getJavaClass());

        List<Class<?>> interceptors = db.getInterceptorsFor(TestInterceptorController1.class);

        assertNotNull(interceptors);
        assertEquals(1, interceptors.size());
        assertEquals(Interceptor1.class, interceptors.get(0));

        ActionSignature beforeActionMethod = db.getBeforeActionMethodForInterceptor(Interceptor1.class);
        assertNotNull(beforeActionMethod);
        assertEquals("before", beforeActionMethod.methodName());

        ActionSignature afterActionMethod = db.getAfterActionMethodForInterceptor(Interceptor1.class);
        assertNotNull(afterActionMethod);
        assertEquals("after", afterActionMethod.methodName());
        
        assertEquals(1, rm.size());
        assertTrue(rm.contains(new Route("interceptor1", "someAction", null)));
    }

    @Test
    public void testInterceptor2() {

        Set<Class<?>> controllerClasses = new HashSet<Class<?>>();
        controllerClasses.add(TestInterceptorController2.class);
        FakeRouteMap rm = new FakeRouteMap();
        ControllerDatabase db = new MappedControllerDatabase(controllerClasses, rm);

        FastClass fc = db.getFastClass(TestInterceptorController2.class);
        assertNotNull(fc);
        assertEquals(TestInterceptorController2.class, fc.getJavaClass());
        fc = db.getFastClass(Interceptor1.class);
        assertNotNull(fc);
        assertEquals(Interceptor1.class, fc.getJavaClass());
        fc = db.getFastClass(Interceptor2.class);
        assertNotNull(fc);
        assertEquals(Interceptor2.class, fc.getJavaClass());

        List<Class<?>> interceptors = db.getInterceptorsFor(TestInterceptorController2.class);

        assertNotNull(interceptors);
        assertEquals(2, interceptors.size());
        assertEquals(Interceptor1.class, interceptors.get(0));
        assertEquals(Interceptor2.class, interceptors.get(1));

        ActionSignature beforeActionMethod = db.getBeforeActionMethodForInterceptor(Interceptor1.class);
        assertNotNull(beforeActionMethod);
        assertEquals("before", beforeActionMethod.methodName());

        beforeActionMethod = db.getBeforeActionMethodForInterceptor(Interceptor2.class);
        assertNotNull(beforeActionMethod);
        assertEquals("before2", beforeActionMethod.methodName());

        ActionSignature afterActionMethod = db.getAfterActionMethodForInterceptor(Interceptor1.class);
        assertNotNull(afterActionMethod);
        assertEquals("after", afterActionMethod.methodName());

        afterActionMethod = db.getAfterActionMethodForInterceptor(Interceptor2.class);
        assertNotNull(afterActionMethod);
        assertEquals("after2", afterActionMethod.methodName());
        
        assertEquals(1, rm.size());
        assertTrue(rm.contains(new Route("interceptor2", "someAction", null)));
    }

    @Test
    public void testInterceptors1and2() {

        Set<Class<?>> controllerClasses = new HashSet<Class<?>>();
        controllerClasses.add(TestInterceptorController1.class);
        controllerClasses.add(TestInterceptorController2.class);
        FakeRouteMap rm = new FakeRouteMap();
        ControllerDatabase db = new MappedControllerDatabase(controllerClasses, rm);

        FastClass fc = db.getFastClass(TestInterceptorController1.class);
        assertNotNull(fc);
        assertEquals(TestInterceptorController1.class, fc.getJavaClass());
        fc = db.getFastClass(TestInterceptorController2.class);
        assertNotNull(fc);
        assertEquals(TestInterceptorController2.class, fc.getJavaClass());
        fc = db.getFastClass(Interceptor1.class);
        assertNotNull(fc);
        assertEquals(Interceptor1.class, fc.getJavaClass());
        fc = db.getFastClass(Interceptor2.class);
        assertNotNull(fc);
        assertEquals(Interceptor2.class, fc.getJavaClass());

        List<Class<?>> interceptors = db.getInterceptorsFor(TestInterceptorController1.class);

        assertNotNull(interceptors);
        assertEquals(1, interceptors.size());
        assertEquals(Interceptor1.class, interceptors.get(0));

        interceptors = db.getInterceptorsFor(TestInterceptorController2.class);

        assertNotNull(interceptors);
        assertEquals(2, interceptors.size());
        assertEquals(Interceptor1.class, interceptors.get(0));
        assertEquals(Interceptor2.class, interceptors.get(1));

        ActionSignature beforeActionMethod = db.getBeforeActionMethodForInterceptor(Interceptor1.class);
        assertNotNull(beforeActionMethod);
        assertEquals("before", beforeActionMethod.methodName());

        beforeActionMethod = db.getBeforeActionMethodForInterceptor(Interceptor2.class);
        assertNotNull(beforeActionMethod);
        assertEquals("before2", beforeActionMethod.methodName());

        ActionSignature afterActionMethod = db.getAfterActionMethodForInterceptor(Interceptor1.class);
        assertNotNull(afterActionMethod);
        assertEquals("after", afterActionMethod.methodName());

        afterActionMethod = db.getAfterActionMethodForInterceptor(Interceptor2.class);
        assertNotNull(afterActionMethod);
        assertEquals("after2", afterActionMethod.methodName());
        
        assertEquals(2, rm.size());
        assertTrue(rm.contains(new Route("interceptor1", "someAction", null)));
        assertTrue(rm.contains(new Route("interceptor2", "someAction", null)));
    }

    @Test
    public void testInterceptor3() {

        try {
            Set<Class<?>> controllerClasses = new HashSet<Class<?>>();
            controllerClasses.add(TestInterceptorController3.class);
            new MappedControllerDatabase(controllerClasses, new FakeRouteMap());
            fail("should have thrown exception");
        } catch (Exception e) {
            if (!(e instanceof ConfigurationException)) {
                fail("wrong exception type");
            }
        }
    }

    @Test
    public void testMethodInterceptorController1() {

        Set<Class<?>> controllerClasses = new HashSet<Class<?>>();
        controllerClasses.add(TestMethodInterceptorController1.class);
        FakeRouteMap rm = new FakeRouteMap();
        ControllerDatabase db = new MappedControllerDatabase(controllerClasses, rm);

        FastClass fc = db.getFastClass(TestMethodInterceptorController1.class);
        assertNotNull(fc);
        assertEquals(TestMethodInterceptorController1.class, fc.getJavaClass());
        fc = db.getFastClass(Interceptor1.class);
        assertNotNull(fc);
        assertEquals(Interceptor1.class, fc.getJavaClass());

        List<Class<?>> interceptors = db.getInterceptorsForAction(TestMethodInterceptorController1.class, "someAction");

        assertNotNull(interceptors);
        assertEquals(1, interceptors.size());
        assertEquals(Interceptor1.class, interceptors.get(0));

        interceptors = db.getInterceptorsForDefaultAction(TestMethodInterceptorController1.class);

        assertNotNull(interceptors);
        assertEquals(1, interceptors.size());
        assertEquals(Interceptor1.class, interceptors.get(0));

        ActionSignature beforeActionMethod = db.getBeforeActionMethodForInterceptor(Interceptor1.class);
        assertNotNull(beforeActionMethod);
        assertEquals("before", beforeActionMethod.methodName());

        ActionSignature afterActionMethod = db.getAfterActionMethodForInterceptor(Interceptor1.class);
        assertNotNull(afterActionMethod);
        assertEquals("after", afterActionMethod.methodName());
        
        assertEquals(2, rm.size());
        assertTrue(rm.contains(new Route("method-interceptor1", null, null)));
        assertTrue(rm.contains(new Route("method-interceptor1", "someAction", null)));
    }

    @Test
    public void testMethodInterceptorController2() {

        Set<Class<?>> controllerClasses = new HashSet<Class<?>>();
        controllerClasses.add(TestMethodInterceptorController2.class);
        FakeRouteMap rm = new FakeRouteMap();
        ControllerDatabase db = new MappedControllerDatabase(controllerClasses, rm);

        FastClass fc = db.getFastClass(TestMethodInterceptorController2.class);
        assertNotNull(fc);
        assertEquals(TestMethodInterceptorController2.class, fc.getJavaClass());
        fc = db.getFastClass(Interceptor1.class);
        assertNotNull(fc);
        assertEquals(Interceptor1.class, fc.getJavaClass());
        fc = db.getFastClass(Interceptor2.class);
        assertNotNull(fc);
        assertEquals(Interceptor2.class, fc.getJavaClass());

        List<Class<?>> interceptors = db.getInterceptorsForAction(TestMethodInterceptorController2.class, "someAction");

        assertNotNull(interceptors);
        assertEquals(2, interceptors.size());
        assertEquals(Interceptor1.class, interceptors.get(0));
        assertEquals(Interceptor2.class, interceptors.get(1));

        ActionSignature beforeActionMethod = db.getBeforeActionMethodForInterceptor(Interceptor1.class);
        assertNotNull(beforeActionMethod);
        assertEquals("before", beforeActionMethod.methodName());

        beforeActionMethod = db.getBeforeActionMethodForInterceptor(Interceptor2.class);
        assertNotNull(beforeActionMethod);
        assertEquals("before2", beforeActionMethod.methodName());

        ActionSignature afterActionMethod = db.getAfterActionMethodForInterceptor(Interceptor1.class);
        assertNotNull(afterActionMethod);
        assertEquals("after", afterActionMethod.methodName());

        afterActionMethod = db.getAfterActionMethodForInterceptor(Interceptor2.class);
        assertNotNull(afterActionMethod);
        assertEquals("after2", afterActionMethod.methodName());
        
        assertEquals(1, rm.size());
        assertTrue(rm.contains(new Route("method-interceptor2", "someAction", null)));
    }

    @Test
    public void testMethodInterceptorController3() {

        try {
            Set<Class<?>> controllerClasses = new HashSet<Class<?>>();
            controllerClasses.add(TestMethodInterceptorController3.class);
            new MappedControllerDatabase(controllerClasses, new FakeRouteMap());
            fail("should have thrown exception");
        } catch (Exception e) {
            if (!(e instanceof ConfigurationException)) {
                fail("wrong exception type");
            }
        }
    }

    @Test
    public void testMethodInterceptorController4() {

        Set<Class<?>> controllerClasses = new HashSet<Class<?>>();
        controllerClasses.add(TestMethodInterceptorController4.class);
        FakeRouteMap rm = new FakeRouteMap();
        ControllerDatabase db = new MappedControllerDatabase(controllerClasses, rm);

        FastClass fc = db.getFastClass(TestMethodInterceptorController4.class);
        assertNotNull(fc);
        assertEquals(TestMethodInterceptorController4.class, fc.getJavaClass());
        fc = db.getFastClass(Interceptor1.class);
        assertNotNull(fc);
        assertEquals(Interceptor1.class, fc.getJavaClass());
        fc = db.getFastClass(Interceptor2.class);
        assertNotNull(fc);
        assertEquals(Interceptor2.class, fc.getJavaClass());

        List<Class<?>> interceptors = db.getInterceptorsForAction(TestMethodInterceptorController4.class, "someAction");

        assertNotNull(interceptors);
        assertEquals(2, interceptors.size());
        assertEquals(Interceptor1.class, interceptors.get(0));
        assertEquals(Interceptor2.class, interceptors.get(1));

        interceptors = db.getInterceptorsForDefaultAction(TestMethodInterceptorController4.class);

        assertNotNull(interceptors);
        assertEquals(1, interceptors.size());
        assertEquals(Interceptor1.class, interceptors.get(0));

        ActionSignature beforeActionMethod = db.getBeforeActionMethodForInterceptor(Interceptor1.class);
        assertNotNull(beforeActionMethod);
        assertEquals("before", beforeActionMethod.methodName());

        beforeActionMethod = db.getBeforeActionMethodForInterceptor(Interceptor2.class);
        assertNotNull(beforeActionMethod);
        assertEquals("before2", beforeActionMethod.methodName());

        ActionSignature afterActionMethod = db.getAfterActionMethodForInterceptor(Interceptor1.class);
        assertNotNull(afterActionMethod);
        assertEquals("after", afterActionMethod.methodName());

        afterActionMethod = db.getAfterActionMethodForInterceptor(Interceptor2.class);
        assertNotNull(afterActionMethod);
        assertEquals("after2", afterActionMethod.methodName());
        
        assertEquals(2, rm.size());
        assertTrue(rm.contains(new Route("method-interceptor4", null, null)));
        assertTrue(rm.contains(new Route("method-interceptor4", "someAction", null)));
    }

    @Test
    public void testMethodInterceptorController5() {

        Set<Class<?>> controllerClasses = new HashSet<Class<?>>();
        controllerClasses.add(TestMethodInterceptorController5.class);
        FakeRouteMap rm = new FakeRouteMap();
        ControllerDatabase db = new MappedControllerDatabase(controllerClasses, rm);

        FastClass fc = db.getFastClass(TestMethodInterceptorController5.class);
        assertNotNull(fc);
        assertEquals(TestMethodInterceptorController5.class, fc.getJavaClass());
        fc = db.getFastClass(Interceptor1.class);
        assertNotNull(fc);
        assertEquals(Interceptor1.class, fc.getJavaClass());
        fc = db.getFastClass(Interceptor2.class);
        assertNotNull(fc);
        assertEquals(Interceptor2.class, fc.getJavaClass());

        List<Class<?>> interceptors = db.getInterceptorsFor(TestMethodInterceptorController5.class);

        assertNotNull(interceptors);
        assertEquals(1, interceptors.size());
        assertEquals(Interceptor1.class, interceptors.get(0));

        interceptors = db.getInterceptorsForAction(TestMethodInterceptorController5.class, "someAction");

        assertNotNull(interceptors);
        assertEquals(2, interceptors.size());
        assertEquals(Interceptor1.class, interceptors.get(0));
        assertEquals(Interceptor2.class, interceptors.get(1));

        interceptors = db.getInterceptorsForDefaultAction(TestMethodInterceptorController5.class);

        assertNotNull(interceptors);
        assertEquals(1, interceptors.size());
        assertEquals(Interceptor1.class, interceptors.get(0));

        ActionSignature beforeActionMethod = db.getBeforeActionMethodForInterceptor(Interceptor1.class);
        assertNotNull(beforeActionMethod);
        assertEquals("before", beforeActionMethod.methodName());

        beforeActionMethod = db.getBeforeActionMethodForInterceptor(Interceptor2.class);
        assertNotNull(beforeActionMethod);
        assertEquals("before2", beforeActionMethod.methodName());

        ActionSignature afterActionMethod = db.getAfterActionMethodForInterceptor(Interceptor1.class);
        assertNotNull(afterActionMethod);
        assertEquals("after", afterActionMethod.methodName());

        afterActionMethod = db.getAfterActionMethodForInterceptor(Interceptor2.class);
        assertNotNull(afterActionMethod);
        assertEquals("after2", afterActionMethod.methodName());
        
        assertEquals(2, rm.size());
        assertTrue(rm.contains(new Route("method-interceptor5", null, null)));
        assertTrue(rm.contains(new Route("method-interceptor5", "someAction", null)));
    }

    @Test
    public void testMethodInterceptorController6() {

        Set<Class<?>> controllerClasses = new HashSet<Class<?>>();
        controllerClasses.add(TestMethodInterceptorController6.class);
        FakeRouteMap rm = new FakeRouteMap();
        ControllerDatabase db = new MappedControllerDatabase(controllerClasses, rm);

        FastClass fc = db.getFastClass(TestMethodInterceptorController6.class);
        assertNotNull(fc);
        assertEquals(TestMethodInterceptorController6.class, fc.getJavaClass());
        fc = db.getFastClass(Interceptor1.class);
        assertNotNull(fc);
        assertEquals(Interceptor1.class, fc.getJavaClass());
        fc = db.getFastClass(Interceptor2.class);
        assertNotNull(fc);
        assertEquals(Interceptor2.class, fc.getJavaClass());
        fc = db.getFastClass(Interceptor3.class);
        assertNotNull(fc);
        assertEquals(Interceptor3.class, fc.getJavaClass());

        List<Class<?>> interceptors = db.getInterceptorsFor(TestMethodInterceptorController6.class);

        assertNotNull(interceptors);
        assertEquals(1, interceptors.size());
        assertEquals(Interceptor3.class, interceptors.get(0));

        interceptors = db.getInterceptorsForAction(TestMethodInterceptorController6.class, "someAction");

        assertNotNull(interceptors);
        assertEquals(2, interceptors.size());
        assertEquals(Interceptor1.class, interceptors.get(0));
        assertEquals(Interceptor2.class, interceptors.get(1));

        interceptors = db.getInterceptorsForDefaultAction(TestMethodInterceptorController6.class);

        assertNotNull(interceptors);
        assertEquals(1, interceptors.size());
        assertEquals(Interceptor1.class, interceptors.get(0));

        ActionSignature beforeActionMethod = db.getBeforeActionMethodForInterceptor(Interceptor1.class);
        assertNotNull(beforeActionMethod);
        assertEquals("before", beforeActionMethod.methodName());

        beforeActionMethod = db.getBeforeActionMethodForInterceptor(Interceptor2.class);
        assertNotNull(beforeActionMethod);
        assertEquals("before2", beforeActionMethod.methodName());

        beforeActionMethod = db.getBeforeActionMethodForInterceptor(Interceptor3.class);
        assertNotNull(beforeActionMethod);
        assertEquals("before3", beforeActionMethod.methodName());

        ActionSignature afterActionMethod = db.getAfterActionMethodForInterceptor(Interceptor1.class);
        assertNotNull(afterActionMethod);
        assertEquals("after", afterActionMethod.methodName());

        afterActionMethod = db.getAfterActionMethodForInterceptor(Interceptor2.class);
        assertNotNull(afterActionMethod);
        assertEquals("after2", afterActionMethod.methodName());

        afterActionMethod = db.getAfterActionMethodForInterceptor(Interceptor3.class);
        assertNotNull(afterActionMethod);
        assertEquals("after3", afterActionMethod.methodName());
        
        assertEquals(2, rm.size());
        assertTrue(rm.contains(new Route("method-interceptor6", null, null)));
        assertTrue(rm.contains(new Route("method-interceptor6", "someAction", null)));
    }

    @Test
    public void testAllValidInterceptorControllers() {

        Set<Class<?>> controllerClasses = new HashSet<Class<?>>();
        controllerClasses.add(TestInterceptorController1.class);
        controllerClasses.add(TestInterceptorController2.class);
        controllerClasses.add(TestMethodInterceptorController1.class);
        controllerClasses.add(TestMethodInterceptorController2.class);
        controllerClasses.add(TestMethodInterceptorController4.class);
        controllerClasses.add(TestMethodInterceptorController5.class);
        controllerClasses.add(TestMethodInterceptorController6.class);
        FakeRouteMap rm = new FakeRouteMap();
        ControllerDatabase db = new MappedControllerDatabase(controllerClasses, rm);

        FastClass fc = db.getFastClass(TestInterceptorController1.class);
        assertNotNull(fc);
        assertEquals(TestInterceptorController1.class, fc.getJavaClass());
        fc = db.getFastClass(TestInterceptorController2.class);
        assertNotNull(fc);
        assertEquals(TestInterceptorController2.class, fc.getJavaClass());
        fc = db.getFastClass(TestMethodInterceptorController1.class);
        assertNotNull(fc);
        assertEquals(TestMethodInterceptorController1.class, fc.getJavaClass());
        fc = db.getFastClass(TestMethodInterceptorController2.class);
        assertNotNull(fc);
        assertEquals(TestMethodInterceptorController2.class, fc.getJavaClass());
        fc = db.getFastClass(TestMethodInterceptorController4.class);
        assertNotNull(fc);
        assertEquals(TestMethodInterceptorController4.class, fc.getJavaClass());
        fc = db.getFastClass(TestMethodInterceptorController5.class);
        assertNotNull(fc);
        assertEquals(TestMethodInterceptorController5.class, fc.getJavaClass());
        fc = db.getFastClass(TestMethodInterceptorController6.class);
        assertNotNull(fc);
        assertEquals(TestMethodInterceptorController6.class, fc.getJavaClass());
        fc = db.getFastClass(Interceptor1.class);
        assertNotNull(fc);
        assertEquals(Interceptor1.class, fc.getJavaClass());
        fc = db.getFastClass(Interceptor2.class);
        assertNotNull(fc);
        assertEquals(Interceptor2.class, fc.getJavaClass());
        fc = db.getFastClass(Interceptor3.class);
        assertNotNull(fc);
        assertEquals(Interceptor3.class, fc.getJavaClass());

        List<Class<?>> interceptors = db.getInterceptorsFor(TestInterceptorController1.class);

        assertNotNull(interceptors);
        assertEquals(1, interceptors.size());
        assertEquals(Interceptor1.class, interceptors.get(0));

        interceptors = db.getInterceptorsFor(TestInterceptorController2.class);

        assertNotNull(interceptors);
        assertEquals(2, interceptors.size());
        assertEquals(Interceptor1.class, interceptors.get(0));
        assertEquals(Interceptor2.class, interceptors.get(1));

        interceptors = db.getInterceptorsForAction(TestMethodInterceptorController1.class, "someAction");

        assertNotNull(interceptors);
        assertEquals(1, interceptors.size());
        assertEquals(Interceptor1.class, interceptors.get(0));

        interceptors = db.getInterceptorsForDefaultAction(TestMethodInterceptorController1.class);

        assertNotNull(interceptors);
        assertEquals(1, interceptors.size());
        assertEquals(Interceptor1.class, interceptors.get(0));

        interceptors = db.getInterceptorsForAction(TestMethodInterceptorController2.class, "someAction");

        assertNotNull(interceptors);
        assertEquals(2, interceptors.size());
        assertEquals(Interceptor1.class, interceptors.get(0));
        assertEquals(Interceptor2.class, interceptors.get(1));

        interceptors = db.getInterceptorsForAction(TestMethodInterceptorController4.class, "someAction");

        assertNotNull(interceptors);
        assertEquals(2, interceptors.size());
        assertEquals(Interceptor1.class, interceptors.get(0));
        assertEquals(Interceptor2.class, interceptors.get(1));

        interceptors = db.getInterceptorsForDefaultAction(TestMethodInterceptorController4.class);

        assertNotNull(interceptors);
        assertEquals(1, interceptors.size());
        assertEquals(Interceptor1.class, interceptors.get(0));

        interceptors = db.getInterceptorsFor(TestMethodInterceptorController5.class);

        assertNotNull(interceptors);
        assertEquals(1, interceptors.size());
        assertEquals(Interceptor1.class, interceptors.get(0));

        interceptors = db.getInterceptorsForAction(TestMethodInterceptorController5.class, "someAction");

        assertNotNull(interceptors);
        assertEquals(2, interceptors.size());
        assertEquals(Interceptor1.class, interceptors.get(0));
        assertEquals(Interceptor2.class, interceptors.get(1));

        interceptors = db.getInterceptorsForDefaultAction(TestMethodInterceptorController5.class);

        assertNotNull(interceptors);
        assertEquals(1, interceptors.size());
        assertEquals(Interceptor1.class, interceptors.get(0));

        interceptors = db.getInterceptorsFor(TestMethodInterceptorController6.class);

        assertNotNull(interceptors);
        assertEquals(1, interceptors.size());
        assertEquals(Interceptor3.class, interceptors.get(0));

        interceptors = db.getInterceptorsForAction(TestMethodInterceptorController6.class, "someAction");

        assertNotNull(interceptors);
        assertEquals(2, interceptors.size());
        assertEquals(Interceptor1.class, interceptors.get(0));
        assertEquals(Interceptor2.class, interceptors.get(1));

        interceptors = db.getInterceptorsForDefaultAction(TestMethodInterceptorController6.class);

        assertNotNull(interceptors);
        assertEquals(1, interceptors.size());
        assertEquals(Interceptor1.class, interceptors.get(0));

        ActionSignature beforeActionMethod = db.getBeforeActionMethodForInterceptor(Interceptor1.class);
        assertNotNull(beforeActionMethod);
        assertEquals("before", beforeActionMethod.methodName());

        beforeActionMethod = db.getBeforeActionMethodForInterceptor(Interceptor2.class);
        assertNotNull(beforeActionMethod);
        assertEquals("before2", beforeActionMethod.methodName());

        beforeActionMethod = db.getBeforeActionMethodForInterceptor(Interceptor3.class);
        assertNotNull(beforeActionMethod);
        assertEquals("before3", beforeActionMethod.methodName());

        ActionSignature afterActionMethod = db.getAfterActionMethodForInterceptor(Interceptor1.class);
        assertNotNull(afterActionMethod);
        assertEquals("after", afterActionMethod.methodName());

        afterActionMethod = db.getAfterActionMethodForInterceptor(Interceptor2.class);
        assertNotNull(afterActionMethod);
        assertEquals("after2", afterActionMethod.methodName());

        afterActionMethod = db.getAfterActionMethodForInterceptor(Interceptor3.class);
        assertNotNull(afterActionMethod);
        assertEquals("after3", afterActionMethod.methodName());
        
        assertEquals(11, rm.size());
        assertTrue(rm.contains(new Route("interceptor1", "someAction", null)));
        assertTrue(rm.contains(new Route("interceptor2", "someAction", null)));
        assertTrue(rm.contains(new Route("method-interceptor1", null, null)));
        assertTrue(rm.contains(new Route("method-interceptor1", "someAction", null)));
        assertTrue(rm.contains(new Route("method-interceptor2", "someAction", null)));
        assertTrue(rm.contains(new Route("method-interceptor4", null, null)));
        assertTrue(rm.contains(new Route("method-interceptor4", "someAction", null)));
        assertTrue(rm.contains(new Route("method-interceptor5", null, null)));
        assertTrue(rm.contains(new Route("method-interceptor5", "someAction", null)));
        assertTrue(rm.contains(new Route("method-interceptor6", null, null)));
        assertTrue(rm.contains(new Route("method-interceptor6", "someAction", null)));
    }

    @Test
    public void testInvalidActionController1() {

        try {
            Set<Class<?>> controllerClasses = new HashSet<Class<?>>();
            controllerClasses.add(TestInvalidActionController1.class);
            new MappedControllerDatabase(controllerClasses, new FakeRouteMap());
            fail("should have thrown exception");
        } catch (Exception e) {
            if (!(e instanceof ConfigurationException)) {
                fail("wrong exception type");
            }
        }
    }

    @Test
    public void testInvalidActionController2() {

        try {
            Set<Class<?>> controllerClasses = new HashSet<Class<?>>();
            controllerClasses.add(TestInvalidActionController2.class);
            new MappedControllerDatabase(controllerClasses, new FakeRouteMap());
            fail("should have thrown exception");
        } catch (Exception e) {
            if (!(e instanceof ConfigurationException)) {
                fail("wrong exception type");
            }
        }
    }

    @Test
    public void testInvalidActionController3() {

        try {
            Set<Class<?>> controllerClasses = new HashSet<Class<?>>();
            controllerClasses.add(TestInvalidActionController3.class);
            new MappedControllerDatabase(controllerClasses, new FakeRouteMap());
            fail("should have thrown exception");
        } catch (Exception e) {
            if (!(e instanceof ConfigurationException)) {
                fail("wrong exception type");
            }
        }
    }

    @Test
    public void testInvalidActionController4() {

        try {
            Set<Class<?>> controllerClasses = new HashSet<Class<?>>();
            controllerClasses.add(TestInvalidActionController4.class);
            new MappedControllerDatabase(controllerClasses, new FakeRouteMap());
            fail("should have thrown exception");
        } catch (Exception e) {
            if (!(e instanceof ConfigurationException)) {
                fail("wrong exception type");
            }
        }
    }

    @Test
    public void testValidActionController1() {

        try {
            Set<Class<?>> controllerClasses = new HashSet<Class<?>>();
            controllerClasses.add(TestValidActionController1.class);
            new MappedControllerDatabase(controllerClasses, new FakeRouteMap());
        } catch (Exception e) {
            fail("should not have thrown exception");
        }
    }

    @Test
    public void testValidActionController2() {

        try {
            Set<Class<?>> controllerClasses = new HashSet<Class<?>>();
            controllerClasses.add(TestValidActionController2.class);
            new MappedControllerDatabase(controllerClasses, new FakeRouteMap());
        } catch (Exception e) {
            fail("should not have thrown exception");
        }
    }

    @Test
    public void testInvalidController1() {

        try {
            Set<Class<?>> controllerClasses = new HashSet<Class<?>>();
            controllerClasses.add(TestInvalidController1.class);
            new MappedControllerDatabase(controllerClasses, new FakeRouteMap());
            fail("should have thrown exception");
        } catch (Exception e) {
            if (!(e instanceof ConfigurationException)) {
                fail("wrong exception type");
            }
        }
    }

    @Test
    public void testInvalidController2() {

        try {
            Set<Class<?>> controllerClasses = new HashSet<Class<?>>();
            controllerClasses.add(TestInvalidController2.class);
            new MappedControllerDatabase(controllerClasses, new FakeRouteMap());
            fail("should have thrown exception");
        } catch (Exception e) {
            if (!(e instanceof ConfigurationException)) {
                fail("wrong exception type");
            }
        }
    }

    @Test
    public void testInvalidController3() {

        try {
            Set<Class<?>> controllerClasses = new HashSet<Class<?>>();
            controllerClasses.add(TestInvalidController3.class);
            new MappedControllerDatabase(controllerClasses, new FakeRouteMap());
            fail("should have thrown exception");
        } catch (Exception e) {
            if (!(e instanceof ConfigurationException)) {
                fail("wrong exception type");
            }
        }
    }

    @Test
    public void testInvalidController4() {

        try {
            Set<Class<?>> controllerClasses = new HashSet<Class<?>>();
            controllerClasses.add(TestInvalidController4.class);
            new MappedControllerDatabase(controllerClasses, new FakeRouteMap());
            fail("should have thrown exception");
        } catch (Exception e) {
            if (!(e instanceof ConfigurationException)) {
                fail("wrong exception type");
            }
        }
    }

    @Test
    public void testHttpMethodController() {

        Set<Class<?>> controllerClasses = new HashSet<Class<?>>();
        controllerClasses.add(TestHttpMethodController.class);
        FakeRouteMap rm = new FakeRouteMap();
        ControllerDatabase db = new MappedControllerDatabase(controllerClasses, rm);

        assertEquals(TestHttpMethodController.class, db.getControllerClass("TestHttpMethodController"));

        ActionSignature sig = db.getHttpMethodActionSignature(TestHttpMethodController.class, HttpMethod.GET);
        assertNotNull(sig);
        assertTrue(sig instanceof HttpMethodActionSignature);
        assertEquals("getAction", sig.methodName());

        sig = db.getHttpMethodActionSignature(TestHttpMethodController.class, HttpMethod.POST);
        assertNotNull(sig);
        assertTrue(sig instanceof HttpMethodActionSignature);
        assertEquals("postAction", sig.methodName());

        sig = db.getHttpMethodActionSignature(TestHttpMethodController.class, HttpMethod.PUT);
        assertNotNull(sig);
        assertTrue(sig instanceof HttpMethodActionSignature);
        assertEquals("putAction", sig.methodName());

        sig = db.getHttpMethodActionSignature(TestHttpMethodController.class, HttpMethod.DELETE);
        assertNotNull(sig);
        assertTrue(sig instanceof HttpMethodActionSignature);
        assertEquals("deleteAction", sig.methodName());

        sig = db.getHttpMethodActionSignature(TestHttpMethodController.class, HttpMethod.OPTIONS);
        assertNotNull(sig);
        assertTrue(sig instanceof HttpMethodActionSignature);
        assertEquals("optionsAction", sig.methodName());

        sig = db.getHttpMethodActionSignature(TestHttpMethodController.class, HttpMethod.HEAD);
        assertNotNull(sig);
        assertTrue(sig instanceof HttpMethodActionSignature);
        assertEquals("headAction", sig.methodName());

        sig = db.getHttpMethodActionSignature(TestHttpMethodController.class, HttpMethod.TRACE);
        assertNotNull(sig);
        assertTrue(sig instanceof HttpMethodActionSignature);
        assertEquals("traceAction", sig.methodName());
        
        assertEquals(1, rm.size());
        assertTrue(rm.contains(new Route("TestHttpMethodController", null, null)));
    }

    @Test
    public void testHttpMethodController2() {

        Set<Class<?>> controllerClasses = new HashSet<Class<?>>();
        controllerClasses.add(TestHttpMethodController2.class);
        FakeRouteMap rm = new FakeRouteMap();
        ControllerDatabase db = new MappedControllerDatabase(controllerClasses, rm);

        assertEquals(TestHttpMethodController2.class, db.getControllerClass("TestHttpMethodController2"));

        ActionSignature sig = db.getHttpMethodActionSignature(TestHttpMethodController2.class, HttpMethod.GET);
        assertNotNull(sig);
        assertTrue(sig instanceof HttpMethodActionSignature);
        assertEquals("getAction", sig.methodName());

        List<Class<?>> interceptors = db.getInterceptorsForHttpMethodAction(TestHttpMethodController2.class,
                HttpMethod.GET);
        assertNotNull(interceptors);
        assertEquals(1, interceptors.size());
        assertEquals(Interceptor1.class, interceptors.get(0));

        sig = db.getHttpMethodActionSignature(TestHttpMethodController2.class, HttpMethod.POST);
        assertNotNull(sig);
        assertEquals("postAction", sig.methodName());

        interceptors = db.getInterceptorsForHttpMethodAction(TestHttpMethodController2.class, HttpMethod.POST);
        assertNotNull(interceptors);
        assertEquals(2, interceptors.size());
        assertEquals(Interceptor1.class, interceptors.get(0));
        assertEquals(Interceptor2.class, interceptors.get(1));
        
        assertEquals(1, rm.size());
        assertTrue(rm.contains(new Route("TestHttpMethodController2", null, null)));
    }

    @Test
    public void testHttpMethodController3() {

        Set<Class<?>> controllerClasses = new HashSet<Class<?>>();
        controllerClasses.add(TestHttpMethodController3.class);
        FakeRouteMap rm = new FakeRouteMap();
        ControllerDatabase db = new MappedControllerDatabase(controllerClasses, rm);

        assertEquals(TestHttpMethodController3.class, db.getControllerClass("TestHttpMethodController3"));

        ActionSignature sig = db.getHttpMethodActionSignature(TestHttpMethodController3.class, HttpMethod.GET);
        assertNotNull(sig);
        assertTrue(sig instanceof HttpMethodActionSignature);
        assertEquals("multiAction", sig.methodName());

        List<Class<?>> interceptors = db.getInterceptorsForHttpMethodAction(TestHttpMethodController3.class,
                HttpMethod.GET);
        assertNotNull(interceptors);
        assertEquals(2, interceptors.size());
        assertEquals(Interceptor1.class, interceptors.get(0));
        assertEquals(Interceptor2.class, interceptors.get(1));

        sig = db.getHttpMethodActionSignature(TestHttpMethodController3.class, HttpMethod.POST);
        assertNotNull(sig);
        assertTrue(sig instanceof HttpMethodActionSignature);
        assertEquals("multiAction", sig.methodName());

        interceptors = db.getInterceptorsForHttpMethodAction(TestHttpMethodController3.class, HttpMethod.POST);
        assertNotNull(interceptors);
        assertEquals(2, interceptors.size());
        assertEquals(Interceptor1.class, interceptors.get(0));
        assertEquals(Interceptor2.class, interceptors.get(1));

        sig = db.getHttpMethodActionSignature(TestHttpMethodController3.class, HttpMethod.PUT);
        assertNotNull(sig);
        assertTrue(sig instanceof HttpMethodActionSignature);
        assertEquals("multiAction", sig.methodName());

        interceptors = db.getInterceptorsForHttpMethodAction(TestHttpMethodController3.class, HttpMethod.PUT);
        assertNotNull(interceptors);
        assertEquals(2, interceptors.size());
        assertEquals(Interceptor1.class, interceptors.get(0));
        assertEquals(Interceptor2.class, interceptors.get(1));
        
        assertEquals(1, rm.size());
        assertTrue(rm.contains(new Route("TestHttpMethodController3", null, null)));
    }

    @Test
    public void testInvalidHttpMethodController() {

        try {
            Set<Class<?>> controllerClasses = new HashSet<Class<?>>();
            controllerClasses.add(TestInvalidHttpMethodController.class);
            new MappedControllerDatabase(controllerClasses, new FakeRouteMap());
            fail("should have thrown exception");
        } catch (Exception e) {
            if (!(e instanceof ConfigurationException)) {
                fail("wrong exception type");
            }
        }
    }

    @Test
    public void testParamPathController1() {
        
        Set<Class<?>> controllerClasses = new HashSet<Class<?>>();
        controllerClasses.add(TestParamPathController1.class);
        FakeRouteMap rm = new FakeRouteMap();
        ControllerDatabase db = new MappedControllerDatabase(controllerClasses, rm);
        
        assertEquals(TestParamPathController1.class, db.getControllerClass("TestParamPathController1"));
        assertEquals(1, rm.size());
        assertTrue(rm.contains(new Route("TestParamPathController1", "getAction", "to/:name")));
    }
    
    @Test
    public void testParamPathController2() {
        
        Set<Class<?>> controllerClasses = new HashSet<Class<?>>();
        controllerClasses.add(TestParamPathController2.class);
        FakeRouteMap rm = new FakeRouteMap();
        ControllerDatabase db = new MappedControllerDatabase(controllerClasses, rm);
        
        assertEquals(TestParamPathController2.class, db.getControllerClass("TestParamPathController2"));
        assertEquals(1, rm.size());
        assertTrue(rm.contains(new Route("TestParamPathController2", "getAction", ":name/:location<[a-z]+>")));
    }
    
    @Test
    public void testParamPathController3() {
        
        Set<Class<?>> controllerClasses = new HashSet<Class<?>>();
        controllerClasses.add(TestParamPathController3.class);
        FakeRouteMap rm = new FakeRouteMap();
        ControllerDatabase db = new MappedControllerDatabase(controllerClasses, rm);
        
        assertEquals(TestParamPathController3.class, db.getControllerClass("TestParamPathController3"));
        assertEquals(1, rm.size());
        assertTrue(rm.contains(new Route("TestParamPathController3", null, ":name")));
    }
    
    @Test
    public void testParamPathController4() {
        
        Set<Class<?>> controllerClasses = new HashSet<Class<?>>();
        controllerClasses.add(TestParamPathController4.class);
        FakeRouteMap rm = new FakeRouteMap();
        ControllerDatabase db = new MappedControllerDatabase(controllerClasses, rm);
        
        assertEquals(TestParamPathController4.class, db.getControllerClass("TestParamPathController4"));
        assertEquals(1, rm.size());
        assertTrue(rm.contains(new Route("TestParamPathController4", null, ":name")));
    }
    
    @Test
    public void testParamPathController_Invalid1() {
        
        try {
            Set<Class<?>> controllerClasses = new HashSet<Class<?>>();
            controllerClasses.add(TestParamPathController_Invalid1.class);
            new MappedControllerDatabase(controllerClasses, new FakeRouteMap());
            fail("should have thrown exception");
        } catch (Exception e) {
            if (!(e instanceof ConfigurationException)) {
                fail("wrong exception type");
            }
        }
    }
    
    @Test
    public void testParamPathController_Invalid2() {
        
        try {
            Set<Class<?>> controllerClasses = new HashSet<Class<?>>();
            controllerClasses.add(TestParamPathController_Invalid2.class);
            new MappedControllerDatabase(controllerClasses, new FakeRouteMap());
            fail("should have thrown exception");
        } catch (Exception e) {
            if (!(e instanceof ConfigurationException)) {
                fail("wrong exception type");
            }
        }
    }
    
    @Test
    public void testParamPathController_Invalid3() {
        
        try {
            Set<Class<?>> controllerClasses = new HashSet<Class<?>>();
            controllerClasses.add(TestParamPathController_Invalid3.class);
            new MappedControllerDatabase(controllerClasses, new FakeRouteMap());
            fail("should have thrown exception");
        } catch (Exception e) {
            if (!(e instanceof ConfigurationException)) {
                fail("wrong exception type");
            }
        }
    }
    
    @Test
    public void testParamPathController_Invalid4() {
        
        try {
            Set<Class<?>> controllerClasses = new HashSet<Class<?>>();
            controllerClasses.add(TestParamPathController_Invalid4.class);
            new MappedControllerDatabase(controllerClasses, new FakeRouteMap());
            fail("should have thrown exception");
        } catch (Exception e) {
            if (!(e instanceof ConfigurationException)) {
                fail("wrong exception type");
            }
        }
    }
    
    @Test
    public void testParamPathController_Invalid5() {
        
        try {
            Set<Class<?>> controllerClasses = new HashSet<Class<?>>();
            controllerClasses.add(TestParamPathController_Invalid5.class);
            new MappedControllerDatabase(controllerClasses, new FakeRouteMap());
            fail("should have thrown exception");
        } catch (Exception e) {
            if (!(e instanceof ConfigurationException)) {
                fail("wrong exception type");
            }
        }
    }
    
    @Test
    public void testParamPathAndHttpMethodController() {
        
        Set<Class<?>> controllerClasses = new HashSet<Class<?>>();
        controllerClasses.add(TestParamPathAndHttpMethodController.class);
        FakeRouteMap rm = new FakeRouteMap();
        ControllerDatabase db = new MappedControllerDatabase(controllerClasses, rm);
        
        assertEquals(TestParamPathAndHttpMethodController.class, db.getControllerClass("param-and-http"));
        assertEquals(2, rm.size());
        assertTrue(rm.contains(new Route("param-and-http", null, "client/all")));
        assertTrue(rm.contains(new Route("param-and-http", null, "client/:id")));
    }
    
    /*-----------------------------------------------------------------*/

    @StatelessController("param-and-http")
    private static class TestParamPathAndHttpMethodController {
        
        @DefaultAction
        @ParamPath("client/all")
        public View defaultAction() {
            return null;
        }
        
        @GETAction
        @ParamPath("client/:id")
        public View getAction(@Param("id") String id) {
            return null;
        }
    }
    
    @StatelessController
    private static class TestParamPathController_Invalid1 {
        
        @Action
        @ParamPath("")
        public View getAction(@Param("name") String name) {
            return null;
        }
    }
    
    @StatelessController
    private static class TestParamPathController_Invalid2 {
        
        @Action
        @ParamPath(":location")
        public View getAction(@Param("name") String name) {
            return null;
        }
    }
    
    @StatelessController
    private static class TestParamPathController_Invalid3 {
        
        @Action
        @ParamPath(":name")
        public View getAction() {
            return null;
        }
    }
    
    @StatelessController
    private static class TestParamPathController_Invalid4 {
        
        @Action
        @ParamPath(":name")
        public View getAction(@Param("name") String name, 
                @Param("location") String location) {
            return null;
        }
    }
    
    @StatelessController
    private static class TestParamPathController_Invalid5 {
        
        @Action
        @ParamPath(":name/:location")
        public View getAction(@Param("name") String name) {
            return null;
        }
    }
    
    @StatelessController
    private static class TestParamPathController1 {
        
        @Action
        @ParamPath("to/:name")
        public View getAction(@Param("name") String name) {
            return null;
        }
    }
    
    @StatelessController
    private static class TestParamPathController2 {
        
        @Action
        @ParamPath(":name/:location<[a-z]+>")
        public View getAction(@Param("name") String name, 
                @Param("location") String location) {
            return null;
        }
    }
    
    @StatelessController
    private static class TestParamPathController3 {
        
        @DefaultAction
        @ParamPath(":name")
        public View getAction(@Param("name") String name) {
            return null;
        }
    }
    
    @StatelessController
    private static class TestParamPathController4 {
        
        @GETAction
        @ParamPath(":name")
        public View getAction(@Param("name") String name) {
            return null;
        }
    }
    
    @StatelessController
    private static class TestInvalidHttpMethodController {

        @GETAction
        public View getAction() {
            return null;
        }

        @GETAction
        public View getAction2() {
            return null;
        }
    }

    @StatelessController
    private static class TestHttpMethodController3 {

        @GETAction
        @POSTAction
        @PUTAction
        @InterceptedBy({ Interceptor1.class, Interceptor2.class })
        public View multiAction() {
            return null;
        }
    }

    @StatelessController
    private static class TestHttpMethodController2 {

        @GETAction
        @InterceptedBy(Interceptor1.class)
        public View getAction() {
            return null;
        }

        @POSTAction
        @InterceptedBy({ Interceptor1.class, Interceptor2.class })
        public View postAction() {
            return null;
        }
    }

    @StatelessController
    private static class TestHttpMethodController {

        @GETAction
        public View getAction() {
            return new JSP("test");
        }

        @POSTAction
        public View postAction() {
            return new JSP("test");
        }

        @PUTAction
        public View putAction() {
            return new JSP("test");
        }

        @DELETEAction
        public View deleteAction() {
            return new JSP("test");
        }

        @OPTIONSAction
        public View optionsAction() {
            return new JSP("test");
        }

        @TRACEAction
        public View traceAction() {
            return new JSP("test");
        }

        @HEADAction
        public View headAction() {
            return new JSP("test");
        }
    }

    @StatefulController
    @StatelessController
    private static class TestInvalidController1 {

        @Action("test")
        public View testAction() {
            return new JSP("test");
        }
    }

    @StatelessController
    @SingletonController
    @DefaultController
    @InterceptedBy(Interceptor1.class)
    private static class TestInvalidController2 {

        @Action("test")
        public View testAction() {
            return new JSP("test");
        }
    }

    @StatelessController
    private static class TestInvalidController3 {

    }

    private static class TestInvalidController4 {

    }

    @StatelessController("invalid-action1")
    private static class TestInvalidActionController1 {

        @Action("someAction")
        public void someAction() {

        }
    }

    @StatelessController("invalid-action2")
    private static class TestInvalidActionController2 {

        @Action("someAction")
        public String someAction() {

            return "";
        }
    }

    @StatelessController("invalid-action3")
    private static class TestInvalidActionController3 {

        @DefaultAction
        public String defaultAction() {

            return "";
        }
    }

    @StatelessController("invalid-action4")
    private static class TestInvalidActionController4 {

        @DefaultAction
        public void defaultAction() {

        }
    }

    @StatelessController("valid-action1")
    private static class TestValidActionController1 {

        @Action("someAction")
        public XML someAction() {

            return new XML("<test/>");
        }
    }

    @StatelessController("valid-action2")
    private static class TestValidActionController2 {

        @DefaultAction
        public XML defaultAction() {

            return new XML("<test/>");
        }
    }

    @StatelessController("interceptor1")
    @InterceptedBy(Interceptor1.class)
    private static class TestInterceptorController1 {

        @Action("someAction")
        public View someAction() {
            return null;
        }
    }

    @StatelessController("interceptor2")
    @InterceptedBy({ Interceptor1.class, Interceptor2.class })
    private static class TestInterceptorController2 {

        @Action("someAction")
        public View someAction() {
            return null;
        }
    }

    @StatelessController("interceptor3")
    @InterceptedBy({ Interceptor1.class, Interceptor1.class })
    private static class TestInterceptorController3 {

        @Action("someAction")
        public View someAction() {
            return null;
        }
    }

    @StatelessController("method-interceptor1")
    private static class TestMethodInterceptorController1 {

        @Action("someAction")
        @InterceptedBy(Interceptor1.class)
        public View someAction() {
            return null;
        }

        @DefaultAction
        @InterceptedBy(Interceptor1.class)
        public View defaultAction() {
            return null;
        }
    }

    @StatelessController("method-interceptor2")
    private static class TestMethodInterceptorController2 {

        @Action("someAction")
        @InterceptedBy({ Interceptor1.class, Interceptor2.class })
        public View someAction() {
            return null;
        }
    }

    @StatelessController("method-interceptor3")
    private static class TestMethodInterceptorController3 {

        @Action("someAction")
        @InterceptedBy({ Interceptor1.class, Interceptor1.class })
        public View someAction() {
            return null;
        }
    }

    @StatelessController("method-interceptor4")
    private static class TestMethodInterceptorController4 {

        @Action("someAction")
        @InterceptedBy({ Interceptor1.class, Interceptor2.class })
        public View someAction() {
            return null;
        }

        @DefaultAction
        @InterceptedBy(Interceptor1.class)
        public View defaultAction() {
            return null;
        }
    }

    @StatelessController("method-interceptor5")
    @InterceptedBy(Interceptor1.class)
    private static class TestMethodInterceptorController5 {

        @Action("someAction")
        @InterceptedBy({ Interceptor1.class, Interceptor2.class })
        public View someAction() {
            return null;
        }

        @DefaultAction
        @InterceptedBy(Interceptor1.class)
        public View defaultAction() {
            return null;
        }
    }

    @StatelessController("method-interceptor6")
    @InterceptedBy(Interceptor3.class)
    private static class TestMethodInterceptorController6 {

        @Action("someAction")
        @InterceptedBy({ Interceptor1.class, Interceptor2.class })
        public View someAction() {
            return null;
        }

        @DefaultAction
        @InterceptedBy(Interceptor1.class)
        public View defaultAction() {
            return null;
        }
    }

    private static class Interceptor1 {

        @BeforeAction
        public View before() {
            return null;
        }

        @AfterAction
        public View after() {
            return null;
        }
    }

    private static class Interceptor2 {

        @BeforeAction
        public View before2() {
            return null;
        }

        @AfterAction
        public View after2() {
            return null;
        }
    }

    private static class Interceptor3 {

        @BeforeAction
        public View before3() {
            return null;
        }

        @AfterAction
        public View after3() {
            return null;
        }
    }

    @StatelessController("invalid1")
    @InterceptedBy(InvalidInterceptor.class)
    private static class TestInvalidInterceptorController1 {

        @Action("someAction")
        public View someAction() {
            return null;
        }
    }

    private static class InvalidInterceptor {
    }

    @StatelessController("invalid2")
    @InterceptedBy(InvalidInterceptor2.class)
    private static class TestInvalidInterceptorController2 {

        @Action("someAction")
        public View someAction() {
            return null;
        }
    }

    private static class InvalidInterceptor2 {

        @BeforeAction
        public View wrongBefore(String s) {
            return null;
        }
    }

    @StatelessController("invalid3")
    @InterceptedBy(InvalidInterceptor3.class)
    private static class TestInvalidInterceptorController3 {

        @Action("someAction")
        public View someAction() {
            return null;
        }
    }

    private static class InvalidInterceptor3 {

        @AfterAction
        public View wrongAfter(String s) {
            return null;
        }
    }

    @StatelessController("invalid4")
    @InterceptedBy(InvalidInterceptor4.class)
    private static class TestInvalidInterceptorController4 {

        @Action("someAction")
        public View someAction() {
            return null;
        }
    }

    private static class InvalidInterceptor4 {

        @BeforeAction
        public View before() {
            return null;
        }

        @AfterAction
        public View after() {
            return null;
        }

        @AfterAction
        public View after2() {
            return null;
        }
    }

    @StatelessController("invalid5")
    @InterceptedBy(InvalidInterceptor5.class)
    private static class TestInvalidInterceptorController5 {

        @Action("someAction")
        public View someAction() {
            return null;
        }
    }

    private static class InvalidInterceptor5 {

        @BeforeAction
        public View before() {
            return null;
        }

        @BeforeAction
        public View before2() {
            return null;
        }

        @AfterAction
        public View after() {
            return null;
        }
    }

    private static abstract class TestAbstractController {

        @BeforeAction
        private void doSomethingBefore() {
        }
    }

    @DefaultController
    @StatelessController("child")
    private static class TestChildClass extends TestAbstractController {

        @DefaultAction
        public View defaultAction() {
            return new JSP("index");
        }

        @Action("test")
        public View testAction() {
            return new JSP("test");
        }
    }

    @Init
    @SingletonController("startup")
    private static class TestStartupController {

        @AfterConstruct
        private void init2() {
        }
    }

    @DefaultController
    @StatelessController("test")
    private static class TestController {

        public TestController() {
        }

        @AfterConstruct
        private void init() {
        }

        @DefaultAction
        public View defaultAction() {
            return new JSP("index");
        }

        @Action("test1")
        public View testAction() {
            return new JSP("test");
        }

        public void mockMethod() {
        }

        @Action("with-param")
        public View withParamAction() {
            return new JSP("param");
        }

        @Action("another-param")
        public View anotherParamAction() {
            return new JSP("param");
        }

        @Action("some-service")
        public View someServiceAction() {
            return new JSP("some-service");
        }

        @Action("test-annotation")
        public View doAnnotationTest() {
            return new JSP("param");
        }

        @BeforeAction
        private void doSomethingBefore() {
        }

        @AfterAction
        private void doSomethingAfter() {
        }
    }
    
    /*-----------------------------------------------------------------*/
    
    private static class FakeRouteMap implements RouteMap {

        private final Set<Route> routes = new HashSet<Route>();
        
        @Override
        public void add(Route route) {
            routes.add(route);
        }

        public boolean contains(Route route) {
            return routes.contains(route);
        }
        
        public int size() {
            return routes.size();
        }
        
        @Override
        public Route getRoute(String path) {
            return null;
        }
    }
}
