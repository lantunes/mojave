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

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.mojavemvc.tests.controllers.InterceptedController1;
import org.mojavemvc.tests.controllers.InterceptedController10;
import org.mojavemvc.tests.controllers.InterceptedController11;
import org.mojavemvc.tests.controllers.InterceptedController12;
import org.mojavemvc.tests.controllers.InterceptedController13;
import org.mojavemvc.tests.controllers.InterceptedController14;
import org.mojavemvc.tests.controllers.InterceptedController15;
import org.mojavemvc.tests.controllers.InterceptedController17;
import org.mojavemvc.tests.controllers.InterceptedController18;
import org.mojavemvc.tests.controllers.InterceptedController19;
import org.mojavemvc.tests.controllers.InterceptedController2;
import org.mojavemvc.tests.controllers.InterceptedController3;
import org.mojavemvc.tests.controllers.InterceptedController4;
import org.mojavemvc.tests.controllers.InterceptedController5;
import org.mojavemvc.tests.controllers.InterceptedController6;
import org.mojavemvc.tests.controllers.InterceptedController7;
import org.mojavemvc.tests.controllers.InterceptedController8;
import org.mojavemvc.tests.controllers.InterceptedController9;
import org.mojavemvc.tests.controllers.StartupController;
import org.mojavemvc.tests.interceptors.Interceptor1;
import org.mojavemvc.tests.interceptors.Interceptor10;
import org.mojavemvc.tests.interceptors.Interceptor1b;
import org.mojavemvc.tests.interceptors.Interceptor1c;
import org.mojavemvc.tests.interceptors.Interceptor1d;
import org.mojavemvc.tests.interceptors.Interceptor2;
import org.mojavemvc.tests.interceptors.Interceptor3;
import org.mojavemvc.tests.interceptors.Interceptor4;
import org.mojavemvc.tests.interceptors.Interceptor6;
import org.mojavemvc.tests.interceptors.Interceptor8;
import org.mojavemvc.tests.interceptors.Interceptor9;

/**
 * 
 * @author Luis Antunes
 */
public class TestFrontController extends AbstractWebTest {

    @Test
    public void indexControllerDefaultAction_WithError() throws Exception {

        /*
         * there is no action called 'default' in the controller; this should
         * throw an IllegalArgumentException internally
         */
        assertThatRequestFor("/index/default").producesErrorPage();
    }

    @Test
    public void indexControllerDefaultAction2() throws Exception {

        assertThatRequestFor("/index")
            .producesPage()
            .withH2Tag(withContent("Hello from index "));
    }

    @Test
    public void indexControllerDefaultAction3() throws Exception {

        assertThatRequestFor("/")
            .producesPage()
            .withH2Tag(withContent("Hello from  "));
    }

    @Test
    public void indexControllerTestAction() throws Exception {

        assertThatRequestFor("/index/test")
            .producesPage()
            .withH1Tag(withContent("This is the test.jsp file of the IndexController testAction!"));
    }

    @Test
    public void indexControllerWithParamAction() throws Exception {

        assertThatRequestFor("/index/with-param?var=hello")
            .producesPage()
            .withH1Tag(withContent("This is the param.jsp file of the IndexController withParamAction!"))
            .withH2Tag(withContent("Hello from hello"));
    }

    @Test
    public void indexControllerAnotherParamAction() throws Exception {

        assertThatRequestFor("/index/another-param?var=hello")
            .producesPage()
            .withH1Tag(withContent("This is the param.jsp file of the IndexController withParamAction!"))
            .withH2Tag(withContent("Hello from hello"));
    }

    @Test
    public void indexControllerSomeServiceAction() throws Exception {

        assertThatRequestFor("/index/some-service?var=hello")
            .producesPage()
            .withH1Tag(withContent("This is the some-service.jsp file of the IndexController someServiceAction!"))
            .withH2Tag(withContent("answered hello"));
    }

    @Test
    public void indexControllerActionAnnotation() throws Exception {

        assertThatRequestFor("/index/test-annotation?var=annotationTest")
            .producesPage()
            .withH1Tag(withContent("This is the param.jsp file of the IndexController withParamAction!"))
            .withH2Tag(withContent("Hello from annotationTest"));
    }

    @Test
    public void someControllerClassControllerAnnotation() throws Exception {

        assertThatRequestFor("/annot/some-action?var=contollerAnnotationTest")
            .producesPage()
            .withH1Tag(withContent("This is the some-controller.jsp file of the SomeControllerClass doSomething action!"))
            .withH2Tag(withContent("contollerAnnotationTest"));
    }

    @Test
    public void indexControllerParamAnnotationTest1() throws Exception {

        assertThatRequestFor("/index/param-annotation-string?p1=param1")
            .producesPage()
            .withH1Tag(withContent("This is the params.jsp file of the IndexController @Param test action!"))
            .withH2Tag(withContent("Hello from param1"));
    }

    @Test
    public void indexControllerParamAnnotationTest1_WithNull() throws Exception {

        assertThatRequestFor("/index/param-annotation-string")
            .producesPage()
            .withH1Tag(withContent("This is the params.jsp file of the IndexController @Param test action!"))
            .withH2Tag(withContent("Hello from null"));
    }

    @Test
    public void indexControllerParamAnnotationTest2() throws Exception {

        assertThatRequestFor("/index/param-annotation-string2?p1=param1&p2=param2")
            .producesPage()
            .withH1Tag(withContent("This is the params2.jsp file of the IndexController @Param test action!"))
            .withH2Tag(withContent("Hello from param1, param2"));
    }

    @Test
    public void indexControllerParamAnnotationTest2_WithNull() throws Exception {

        assertThatRequestFor("/index/param-annotation-string2?p2=param2")
            .producesPage()
            .withH1Tag(withContent("This is the params2.jsp file of the IndexController @Param test action!"))
            .withH2Tag(withContent("Hello from null, param2"));
    }

    @Test
    public void indexControllerParamAnnotationTest3() throws Exception {

        assertThatRequestFor("/index/param-annotation-int?p1=123456")
            .producesPage()
            .withH1Tag(withContent("This is the params.jsp file of the IndexController @Param test action!"))
            .withH2Tag(withContent("Hello from 123456"));
    }

    @Test
    public void indexControllerParamAnnotationTest3_WithNull() throws Exception {

        assertThatRequestFor("/index/param-annotation-int")
            .producesPage()
                .withH1Tag(withContent("This is the params.jsp file of the IndexController @Param test action!"))
                .withH2Tag(withContent("Hello from 0"));
    }

    @Test
    public void indexControllerParamAnnotationTest3_WithError() throws Exception {

        assertThatRequestFor("/index/param-annotation-int?p1=hello").producesErrorPage();
    }

    @Test
    public void indexControllerParamAnnotationTest4() throws Exception {

        assertThatRequestFor("/index/param-annotation-double?p1=123.456")
            .producesPage()
            .withH1Tag(withContent("This is the params.jsp file of the IndexController @Param test action!"))
            .withH2Tag(withContent("Hello from 123.456"));
    }

    @Test
    public void indexControllerParamAnnotationTest4_WithNull() throws Exception {

        assertThatRequestFor("/index/param-annotation-double")
            .producesPage()
            .withH1Tag(withContent("This is the params.jsp file of the IndexController @Param test action!"))
            .withH2Tag(withContent("Hello from 0.0"));
    }

    @Test
    public void indexControllerParamAnnotationTest4_WithError() throws Exception {

        assertThatRequestFor("/index/param-annotation-double?p1=hello").producesErrorPage();
    }

    @Test
    public void indexControllerParamAnnotationTest5() throws Exception {

        assertThatRequestFor("/index/param-annotation-date?p1=2011-03-01")
            .producesPage()
            .withH1Tag(withContent("This is the params.jsp file of the IndexController @Param test action!"))
            .withH2Tag(withContent("Hello from 2011-03-01"));
    }

    @Test
    public void indexControllerParamAnnotationTest5_WithNull() throws Exception {

        assertThatRequestFor("/index/param-annotation-date")
            .producesPage()
            .withH1Tag(withContent("This is the params.jsp file of the IndexController @Param test action!"))
            .withH2Tag(withContent("Hello from null"));
    }

    @Test
    public void indexControllerParamAnnotationTest5_WithError() throws Exception {

        assertThatRequestFor("/index/param-annotation-date?p1=hello").producesErrorPage();
    }

    @Test
    public void indexControllerParamAnnotationTestAll() throws Exception {

        assertThatRequestFor("/index/param-annotation-all?p1=2011-03-01&p2=hello&p3=123&p4=1.45")
            .producesPage()
            .withH1Tag(withContent("This is the params3.jsp file of the IndexController @Param test action!"))
            .withH2Tag(withContent("Hello from 2011-03-01, hello, 123, 1.45"));
    }

    @Test
    public void indexControllerParamAnnotationTest6() throws Exception {

        assertThatRequestFor("/index/param-annotation-bool?p1=true")
            .producesPage()
            .withH1Tag(withContent("This is the params.jsp file of the IndexController @Param test action!"))
            .withH2Tag(withContent("Hello from true"));
    }

    @Test
    public void indexControllerParamAnnotationTest6b() throws Exception {

        assertThatRequestFor("/index/param-annotation-bool?p1=false")
            .producesPage()
            .withH1Tag(withContent("This is the params.jsp file of the IndexController @Param test action!"))
            .withH2Tag(withContent("Hello from false"));
    }

    @Test
    public void indexControllerParamAnnotationTest6c() throws Exception {

        assertThatRequestFor("/index/param-annotation-bool?p1=t")
            .producesPage()
            .withH1Tag(withContent("This is the params.jsp file of the IndexController @Param test action!"))
            .withH2Tag(withContent("Hello from true"));
    }

    @Test
    public void indexControllerParamAnnotationTest6d() throws Exception {

        assertThatRequestFor("/index/param-annotation-bool?p1=f")
            .producesPage()
            .withH1Tag(withContent("This is the params.jsp file of the IndexController @Param test action!"))
            .withH2Tag(withContent("Hello from false"));
    }

    @Test
    public void indexControllerParamAnnotationTest6e() throws Exception {

        assertThatRequestFor("/index/param-annotation-bool?p1=1")
            .producesPage()
            .withH1Tag(withContent("This is the params.jsp file of the IndexController @Param test action!"))
            .withH2Tag(withContent("Hello from true"));
    }

    @Test
    public void indexControllerParamAnnotationTest6f() throws Exception {

        assertThatRequestFor("/index/param-annotation-bool?p1=0")
            .producesPage()
            .withH1Tag(withContent("This is the params.jsp file of the IndexController @Param test action!"))
            .withH2Tag(withContent("Hello from false"));
    }

    @Test
    public void indexControllerParamAnnotationTest6_WithError() throws Exception {

        assertThatRequestFor("/index/param-annotation-bool?p1=hello").producesErrorPage();
    }

    @Test
    public void indexControllerParamAnnotationTest6_WithError2() throws Exception {

        assertThatRequestFor("/index/param-annotation-bool")
            .producesPage()
            .withH1Tag(withContent("This is the params.jsp file of the IndexController @Param test action!"))
            .withH2Tag(withContent("Hello from false"));
    }

    @Test
    public void indexControllerParamAnnotationTestIntCollection() throws Exception {

        assertThatRequestFor("/index/param-annotation-ints?p1=123&p1=456&p1=789&p1=321")
            .producesPage()
            .withH1Tag(withContent("This is the params3.jsp file of the IndexController @Param test action!"))
            .withH2Tag(withContent("Hello from 123, 456, 789, 321"));
    }

    @Test
    public void indexControllerParamAnnotationTestStringCollection() throws Exception {

        assertThatRequestFor("/index/param-annotation-strings?p1=abc&p1=def&p1=ghi&p1=jkl")
            .producesPage()
            .withH1Tag(withContent("This is the params3.jsp file of the IndexController @Param test action!"))
            .withH2Tag(withContent("Hello from abc, def, ghi, jkl"));
    }

    @Test
    public void indexControllerParamAnnotationTestDoubleCollection() throws Exception {

        assertThatRequestFor("/index/param-annotation-doubles?p1=1.1&p1=2.2&p1=3.3&p1=4.4")
            .producesPage()
            .withH1Tag(withContent("This is the params3.jsp file of the IndexController @Param test action!"))
            .withH2Tag(withContent("Hello from 1.1, 2.2, 3.3, 4.4"));
    }

    @Test
    public void indexControllerParamAnnotationTestDateCollection() throws Exception {

        assertThatRequestFor("/index/param-annotation-dates?"
                + "p1=2011-03-01&p1=2010-02-09&p1=2009-11-23&p1=2008-05-03")
            .producesPage()
            .withH1Tag(withContent("This is the params3.jsp file of the IndexController @Param test action!"))
            .withH2Tag(withContent("Hello from 2011-03-01, 2010-02-09, 2009-11-23, 2008-05-03"));
    }

    @Test
    public void indexControllerParamAnnotationTestBooleanCollection() throws Exception {

        assertThatRequestFor("/index/param-annotation-bools?p1=t&p1=false&p1=f&p1=1")
            .producesPage()
            .withH1Tag(withContent("This is the params3.jsp file of the IndexController @Param test action!"))
            .withH2Tag(withContent("Hello from true, false, false, true"));
    }

    @Test
    public void indexControllerInjectedAction() throws Exception {

        assertThatRequestFor("/index/injected")
            .producesPage()
            .withH1Tag(withContent("This is the param.jsp file of the IndexController withParamAction!"))
            .withH2Tag(withContent("Hello from injected-index"));
    }

    @Test
    public void indexControllerTestInitAction() throws Exception {

        assertThatRequestFor("/index/test-init")
            .producesPage()
            .withH1Tag(withContent("This is the param.jsp file of the IndexController withParamAction!"))
            .withH2Tag(withContent("Hello from init-called"));
    }

    @Test
    public void formController() throws Exception {

        assertThatRequestFor("/form-controller")
            .afterSubmittingForm("test-form", 
                    withValueFor("userName").setTo("john"),
                    withValueFor("password").setTo("doe"))
            .producesPage()
            .withElement("userName", withContent("john"))
            .withElement("password", withContent("doe"));
    }

    @Test
    public void formControllerWithRegularParam() throws Exception {

        assertThatRequestFor("/form-controller/form2")
            .afterSubmittingForm("test-form", 
                    withValueFor("userName").setTo("john"),
                    withValueFor("password").setTo("doe"))
            .producesPage()
            .withElement("userName", withContent("john"))
            .withElement("password", withContent("doe"))
            .withElement("p1", withContent("hello"));
    }

    @Test
    public void formControllerSubmittable() throws Exception {

        assertThatRequestFor("/form-controller/form3")
            .afterSubmittingForm("test-form", 
                    withValueFor("userName").setTo("john"),
                    withValueFor("password").setTo("doe"))
            .producesPage()
            .withElement("userName", withContent("JOHN"))
            .withElement("password", withContent("DOE"));
    }

    @Test
    public void formControllerPopulated() throws Exception {

        assertThatRequestFor("/form-controller/populated")
            .producesPage()
            .withElement("userName", withAttribute("value").setTo("uname"))
            .withElement("password", withAttribute("value").setTo("pswd"));
    }

    @Test
    public void formControllerWithBooleanTrue() throws Exception {

        assertThatRequestFor("/form-controller/form4")
            .afterSubmittingForm("test-form", withCheckBox("someFlag").checked())
            .producesPage()
            .withFlagElement(withContent("true"));
    }

    @Test
    public void formControllerWithBooleanFalse() throws Exception {

        assertThatRequestFor("/form-controller/form4")
            .afterSubmittingForm("test-form")
            .producesPage()
            .withFlagElement(withContent("false"));
    }

    @Test
    public void redirectingController() throws Exception {

        assertThatRequestFor("/redirecting/redirect")
            .producesPage()
            .withH1Tag(withContent("redirected!"));
    }

    @Test
    public void redirectingController2() throws Exception {

        assertThatRequestFor("/redirecting2/doSomething")
            .producesPage()
            .withH1Tag(withContent("redirected!"));
    }

    @Test
    public void streamingControllerXML() throws Exception {

        assertThatRequestFor("/stream/xml")
            .producesPage()
            .withContentType("application/xml")
            .withContent("<?xml version=\"1.0\" encoding=\"UTF-8\"?><Test><hello/></Test>");
    }

    @Test
    public void streamingControllerJSON() throws Exception {

        assertThatRequestFor("/stream/json")
            .producesPage()
            .withContentType("application/json")
            .withContent("{\"Test\":{\"hello\": 1}}");
    }

    @Test
    public void streamingController2() throws Exception {

        assertThatRequestFor("/stream2/doSomething")
            .producesPage()
            .withContentType("application/xml")
            .withContent("<?xml version=\"1.0\" encoding=\"UTF-8\"?><Test><hello/></Test>");
    }

    @Test
    public void streamingController3() throws Exception {

        assertThatRequestFor("/stream3/doSomething")
            .producesPage()
            .withContentType("application/xml")
            .withContent("<?xml version=\"1.0\" encoding=\"UTF-8\"?><Test><hello/></Test>");
    }

    @Test
    public void dispatchingController() throws Exception {

        assertThatRequestFor("/dispatching/doSomething")
            .producesPage()
            .withH2Tag(withContent("Hello from dispatched"));
    }

    @Test
    public void someStatefulControllerDefaultAction() throws Exception {

        assertThatRequestFor("/some-stateful")
            .producesPage()
            .withH1Tag(withContent("This is the index.jsp file of the IndexController defaultAction!"));
    }

    @Test
    public void someStatefulControllerSomeAction() throws Exception {

        assertThatRequestFor("/some-stateful/some-action")
            .producesPage()
            .withH1Tag(withContent("This is the stateful.jsp file of the SomeStatefulController someAction!"));
    }

    @Test
    public void someStatefulControllerVarAction() throws Exception {

        assertThatRequestFor("/some-stateful/set-var?var=statetest")
            .producesPage()
            .withH1Tag(withContent("This is the stateful.jsp file of the SomeStatefulController someAction!"));

        /* make a second request */
        assertThatRequestFor("/some-stateful/get-var")
            .producesPage()
            .withH2Tag(withContent("Hello from statetest"));
    }

    @Test
    public void someStatefulControllerVarAction2() throws Exception {

        assertThatRequestFor("/some-stateful/set-var?var=statetest")
            .producesPage()
            .withH1Tag(withContent("This is the stateful.jsp file of the SomeStatefulController someAction!"));

        /* make a second request with a new client, creating a new session */
        newWebClient();
        assertThatRequestFor("/some-stateful/get-var")
            .producesPage()
            .withH2Tag(withContent("Hello from null")); /* no state stored */
    }

    @Test
    public void someStatefulControllerReqAction() throws Exception {

        /*
         * Test that the injected HttpServletRequests are different, by
         * examining the hex hashcode that is part of the object name obtained
         * by calling toString() in the request instance.
         */

        String hexHashcode1 = makeRequestFor("/some-stateful/get-req").andGetH2TagContent();

        /* make a second request */
        String hexHashcode2 = makeRequestFor("/some-stateful/get-req").andGetH2TagContent();

        /* check that a new servlet resource was injected */
        assertFalse(hexHashcode1.equals(hexHashcode2));
    }

    @Test
    public void someStatefulControllerInjAction() throws Exception {

        /*
         * Test that the @Injected resources are different, by examining the hex
         * hashcode that is part of the object name obtained by calling
         * toString() in the request instance.
         */

        String hexHashcode1 = makeRequestFor("/some-stateful/get-inj").andGetH2TagContent();

        /* make a second request */
        String hexHashcode2 = makeRequestFor("/some-stateful/get-inj").andGetH2TagContent();

        /* check that a new @Inject dependency was injected */
        assertFalse(hexHashcode1.equals(hexHashcode2));
    }

    @Test
    public void someStatefulControllerTestInitAction() throws Exception {

        assertThatRequestFor("/some-stateful/test-init")
            .producesPage()
            .withH2Tag(withContent("Hello from init-called: 1"));

        /*
         * test that init() is called only once after construction, and not per
         * request
         */
        assertThatRequestFor("/some-stateful/test-init")
            .producesPage()
            .withH2Tag(withContent("Hello from init-called: 1"));
    }

    @Test
    public void injectableControllerTestAction() throws Exception {

        assertThatRequestFor("/injectable/test?var=inj")
            .producesPage()
            .withH1Tag(withContent("This is the injectable.jsp file of the InjectableController testAction!"))
            .withH2Tag(withContent("injected-inj"));
    }

    @Test
    public void someSingletonControllerSomeAction() throws Exception {

        assertThatRequestFor("/some-singleton/some-action")
            .producesPage()
            .withH1Tag(withContent("This is the singleton.jsp file of the SomeSingletonController someAction!"));
    }

    @Test
    public void someSingletonControllerVarAction() throws Exception {

        assertThatRequestFor("/some-singleton/set-var?var=statetest")
            .producesPage()
            .withH1Tag(withContent("This is the singleton.jsp file of the SomeSingletonController someAction!"));

        /* make a second request */
        assertThatRequestFor("/some-singleton/get-var")
            .producesPage()
            .withH2Tag(withContent("Hello from statetest"));
    }

    @Test
    public void someSingletonControllerVarAction2() throws Exception {

        assertThatRequestFor("/some-singleton/set-var?var=statetest")
            .producesPage()
            .withH1Tag(withContent("This is the singleton.jsp file of the SomeSingletonController someAction!"));

        /* make a second request with a new client */
        newWebClient();
        assertThatRequestFor("/some-singleton/get-var")
            .producesPage()
            .withH2Tag(withContent("Hello from statetest"));
    }

    @Test
    public void someSingletonControllerReqAction() throws Exception {

        /*
         * Test that the injected HttpServletRequests are different, by
         * examining the hex hashcode that is part of the object name obtained
         * by calling toString() in the request instance.
         */

        String hexHashcode1 = makeRequestFor("/some-singleton/get-req").andGetH2TagContent();

        /* make a second request */
        String hexHashcode2 = makeRequestFor("/some-singleton/get-req").andGetH2TagContent();

        /* check that a new servlet resource was injected */
        assertFalse(hexHashcode1.equals(hexHashcode2));
    }

    @Test
    public void someSingletonControllerInjAction() throws Exception {

        /*
         * Test that the @Injected resources are different, by examining the hex
         * hashcode that is part of the object name obtained by calling
         * toString() in the request instance.
         */

        String hexHashcode1 = makeRequestFor("/some-singleton/get-inj").andGetH2TagContent();

        /* make a second request */
        String hexHashcode2 = makeRequestFor("/some-singleton/get-inj").andGetH2TagContent();

        /* check that a new @Inject dependency was injected */
        assertFalse(hexHashcode1.equals(hexHashcode2));
    }

    @Test
    public void startupController() throws Exception {

        /*
         * When the servlet runner is created, an instance of this controller
         * should be created and its after construct method called. This will
         * set the static field value. We are trying to confirm that an instance
         * of this class was created at startup without making a request.
         */
        assertEquals("init-called", StartupController.initVal);
    }

    @Test
    public void beforeControllerIndexAction() throws Exception {

        assertThatRequestFor("/before/index")
            .producesPage()
            .withH1Tag(withContent("This is the before.jsp file of the BeforeController beforeAction!"));
    }

    @Test
    public void afterControllerIndexAction() throws Exception {

        assertThatRequestFor("/after/index")
            .producesPage()
            .withH1Tag(withContent("This is the after.jsp file of the AfterController afterAction!"));
    }

    @Test
    public void beforeWithContextControllerIndexAction() throws Exception {

        assertThatRequestFor("/beforectx/index?p1=testctx")
            .producesPage()
            .withH2Tag(withContent("Hello from testctx"));
    }

    @Test
    public void afterWithContextControllerIndexAction() throws Exception {

        assertThatRequestFor("/afterctx/index?p1=testctx")
            .producesPage()
            .withH2Tag(withContent("Hello from testctx"));
    }

    @Test
    public void afterWithContextControllerDefaultAction() throws Exception {

        assertThatRequestFor("/afterctx2")
            .producesPage()
            .withH2Tag(withContent("Hello from default"));
    }

    @Test
    public void beforeWithModelControllerIndexAction() throws Exception {

        assertThatRequestFor("/beforectx2/index?userName=john&password=doe")
            .producesPage()
            .withH2Tag(withContent("Hello from john doe"));
    }

    @Test
    public void interceptedController1SomeAction() throws Exception {

        List<String> invocationList = new ArrayList<String>();
        InterceptedController1.invocationList = invocationList;
        Interceptor1.invocationList = invocationList;

        assertThatRequestFor("/intercepted1/some-action")
            .producesPage()
            .withH2Tag(withContent("Hello from someAction"));

        assertEquals(3, invocationList.size());
        assertEquals("interceptor1-beforeAction:req:resp:sess:someService", invocationList.get(0));
        assertEquals("someAction", invocationList.get(1));
        assertEquals("interceptor1-afterAction:req:resp:sess:someService:ok", invocationList.get(2));
    }

    @Test
    public void interceptedController2SomeAction() throws Exception {

        List<String> invocationList = new ArrayList<String>();
        InterceptedController2.invocationList = invocationList;
        Interceptor1.invocationList = invocationList;

        assertThatRequestFor("/intercepted2/some-action")
            .producesPage()
            .withH2Tag(withContent("Hello from someAction"));

        assertEquals(3, invocationList.size());
        assertEquals("interceptor1-beforeAction:req:resp:sess:someService", invocationList.get(0));
        assertEquals("someAction", invocationList.get(1));
        assertEquals("interceptor1-afterAction:req:resp:sess:someService:ok", invocationList.get(2));
    }

    @Test
    public void interceptedController2DefaultAction() throws Exception {

        List<String> invocationList = new ArrayList<String>();
        InterceptedController2.invocationList = invocationList;
        Interceptor1.invocationList = invocationList;

        assertThatRequestFor("/intercepted2")
            .producesPage()
            .withH2Tag(withContent("Hello from defaultAction"));

        assertEquals(3, invocationList.size());
        assertEquals("interceptor1-beforeAction:req:resp:sess:someService", invocationList.get(0));
        assertEquals("defaultAction", invocationList.get(1));
        assertEquals("interceptor1-afterAction:req:resp:sess:someService:ok", invocationList.get(2));
    }

    @Test
    public void interceptedController3SomeAction() throws Exception {

        List<String> invocationList = new ArrayList<String>();
        InterceptedController3.invocationList = invocationList;
        Interceptor1.invocationList = invocationList;
        Interceptor1b.invocationList = invocationList;

        assertThatRequestFor("/intercepted3/some-action")
            .producesPage()
            .withH2Tag(withContent("Hello from someAction"));

        assertEquals(5, invocationList.size());
        assertEquals("interceptor1-beforeAction:req:resp:sess:someService", invocationList.get(0));
        assertEquals("interceptor1b-beforeAction:req:resp:sess:someService", invocationList.get(1));
        assertEquals("someAction", invocationList.get(2));
        assertEquals("interceptor1-afterAction:req:resp:sess:someService:ok", invocationList.get(3));
        assertEquals("interceptor1b-afterAction:req:resp:sess:someService:ok", invocationList.get(4));
    }

    @Test
    public void interceptedController4SomeAction() throws Exception {

        List<String> invocationList = new ArrayList<String>();
        InterceptedController4.invocationList = invocationList;
        Interceptor1.invocationList = invocationList;
        Interceptor1b.invocationList = invocationList;

        assertThatRequestFor("/intercepted4/some-action")
            .producesPage()
            .withH2Tag(withContent("Hello from someAction"));

        assertEquals(5, invocationList.size());
        assertEquals("interceptor1-beforeAction:req:resp:sess:someService", invocationList.get(0));
        assertEquals("interceptor1b-beforeAction:req:resp:sess:someService", invocationList.get(1));
        assertEquals("someAction", invocationList.get(2));
        assertEquals("interceptor1-afterAction:req:resp:sess:someService:ok", invocationList.get(3));
        assertEquals("interceptor1b-afterAction:req:resp:sess:someService:ok", invocationList.get(4));
    }

    @Test
    public void interceptedController4DefaultAction() throws Exception {

        List<String> invocationList = new ArrayList<String>();
        InterceptedController4.invocationList = invocationList;
        Interceptor1.invocationList = invocationList;
        Interceptor1b.invocationList = invocationList;

        assertThatRequestFor("/intercepted4")
            .producesPage()
            .withH2Tag(withContent("Hello from defaultAction"));

        assertEquals(5, invocationList.size());
        assertEquals("interceptor1-beforeAction:req:resp:sess:someService", invocationList.get(0));
        assertEquals("interceptor1b-beforeAction:req:resp:sess:someService", invocationList.get(1));
        assertEquals("defaultAction", invocationList.get(2));
        assertEquals("interceptor1-afterAction:req:resp:sess:someService:ok", invocationList.get(3));
        assertEquals("interceptor1b-afterAction:req:resp:sess:someService:ok", invocationList.get(4));
    }

    @Test
    public void interceptedController1DefaultAction() throws Exception {

        List<String> invocationList = new ArrayList<String>();
        InterceptedController1.invocationList = invocationList;
        Interceptor1.invocationList = invocationList;

        assertThatRequestFor("/intercepted1")
            .producesPage()
            .withH2Tag(withContent("Hello from defaultAction"));

        assertEquals(3, invocationList.size());
        assertEquals("interceptor1-beforeAction:req:resp:sess:someService", invocationList.get(0));
        assertEquals("defaultAction", invocationList.get(1));
        assertEquals("interceptor1-afterAction:req:resp:sess:someService:ok", invocationList.get(2));
    }

    @Test
    public void interceptedController3DefaultAction() throws Exception {

        List<String> invocationList = new ArrayList<String>();
        InterceptedController3.invocationList = invocationList;
        Interceptor1.invocationList = invocationList;
        Interceptor1b.invocationList = invocationList;

        assertThatRequestFor("/intercepted3")
            .producesPage()
            .withH2Tag(withContent("Hello from defaultAction"));

        assertEquals(5, invocationList.size());
        assertEquals("interceptor1-beforeAction:req:resp:sess:someService", invocationList.get(0));
        assertEquals("interceptor1b-beforeAction:req:resp:sess:someService", invocationList.get(1));
        assertEquals("defaultAction", invocationList.get(2));
        assertEquals("interceptor1-afterAction:req:resp:sess:someService:ok", invocationList.get(3));
        assertEquals("interceptor1b-afterAction:req:resp:sess:someService:ok", invocationList.get(4));
    }

    @Test
    public void interceptedController5DefaultAction() throws Exception {

        List<String> invocationList = new ArrayList<String>();
        InterceptedController5.invocationList = invocationList;
        Interceptor1.invocationList = invocationList;
        Interceptor1b.invocationList = invocationList;

        assertThatRequestFor("/intercepted5")
            .producesPage()
            .withH2Tag(withContent("Hello from defaultAction"));

        assertEquals(5, invocationList.size());
        assertEquals("interceptor1-beforeAction:req:resp:sess:someService", invocationList.get(0));
        assertEquals("interceptor1b-beforeAction:req:resp:sess:someService", invocationList.get(1));
        assertEquals("defaultAction", invocationList.get(2));
        assertEquals("interceptor1b-afterAction:req:resp:sess:someService:ok", invocationList.get(3));
        assertEquals("interceptor1-afterAction:req:resp:sess:someService:ok", invocationList.get(4));
    }

    @Test
    public void interceptedController5SomeAction() throws Exception {

        List<String> invocationList = new ArrayList<String>();
        InterceptedController5.invocationList = invocationList;
        Interceptor1.invocationList = invocationList;
        Interceptor1b.invocationList = invocationList;

        assertThatRequestFor("/intercepted5/some-action")
            .producesPage()
            .withH2Tag(withContent("Hello from someAction"));

        assertEquals(5, invocationList.size());
        assertEquals("interceptor1-beforeAction:req:resp:sess:someService", invocationList.get(0));
        assertEquals("interceptor1b-beforeAction:req:resp:sess:someService", invocationList.get(1));
        assertEquals("someAction", invocationList.get(2));
        assertEquals("interceptor1b-afterAction:req:resp:sess:someService:ok", invocationList.get(3));
        assertEquals("interceptor1-afterAction:req:resp:sess:someService:ok", invocationList.get(4));
    }

    @Test
    public void interceptedController6DefaultAction() throws Exception {

        List<String> invocationList = new ArrayList<String>();
        InterceptedController6.invocationList = invocationList;
        Interceptor1.invocationList = invocationList;
        
        assertThatRequestFor("/intercepted6")
            .producesPage()
            .withH2Tag(withContent("Hello from defaultAction"));

        assertEquals(1, invocationList.size());
        assertEquals("defaultAction", invocationList.get(0));
    }

    @Test
    public void interceptedController7SomeAction() throws Exception {

        List<String> invocationList = new ArrayList<String>();
        InterceptedController7.invocationList = invocationList;
        Interceptor1.invocationList = invocationList;

        assertThatRequestFor("/intercepted7/some-action")
            .producesPage()
            .withH2Tag(withContent("Hello from someAction"));

        assertEquals(1, invocationList.size());
        assertEquals("someAction", invocationList.get(0));
    }

    @Test
    public void interceptedController8DefaultAction() throws Exception {

        List<String> invocationList = new ArrayList<String>();
        InterceptedController8.invocationList = invocationList;
        Interceptor1.invocationList = invocationList;
        Interceptor1b.invocationList = invocationList;
        Interceptor1c.invocationList = invocationList;
        Interceptor1d.invocationList = invocationList;

        assertThatRequestFor("/intercepted8")
            .producesPage()
            .withH2Tag(withContent("Hello from defaultAction"));

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
    public void interceptedController8SomeAction() throws Exception {

        List<String> invocationList = new ArrayList<String>();
        InterceptedController8.invocationList = invocationList;
        Interceptor1.invocationList = invocationList;
        Interceptor1b.invocationList = invocationList;
        Interceptor1c.invocationList = invocationList;
        Interceptor1d.invocationList = invocationList;

        assertThatRequestFor("/intercepted8/some-action")
            .producesPage()
            .withH2Tag(withContent("Hello from someAction"));

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
    public void interceptedController9DefaultAction() throws Exception {

        List<String> invocationList = new ArrayList<String>();
        InterceptedController9.invocationList = invocationList;
        Interceptor1.invocationList = invocationList;
        Interceptor1b.invocationList = invocationList;
        Interceptor1c.invocationList = invocationList;
        Interceptor1d.invocationList = invocationList;

        assertThatRequestFor("/intercepted9")
            .producesPage()
            .withH2Tag(withContent("Hello from defaultAction"));

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
    public void interceptedController9SomeAction() throws Exception {

        List<String> invocationList = new ArrayList<String>();
        InterceptedController9.invocationList = invocationList;
        Interceptor1.invocationList = invocationList;
        Interceptor1b.invocationList = invocationList;
        Interceptor1c.invocationList = invocationList;
        Interceptor1d.invocationList = invocationList;

        assertThatRequestFor("/intercepted9/some-action")
            .producesPage()
            .withH2Tag(withContent("Hello from someAction"));

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

    @Test
    public void interceptedController10SomeAction() throws Exception {

        List<String> invocationList = new ArrayList<String>();
        InterceptedController10.invocationList = invocationList;
        Interceptor4.invocationList = invocationList;

        assertThatRequestFor("/intercepted10/some-action")
            .producesPage()
            .withH2Tag(withContent("Hello from someAction"));

        assertEquals(2, invocationList.size());
        assertEquals("interceptor4-beforeAction:req:resp:sess:someService", invocationList.get(0));
        assertEquals("someAction", invocationList.get(1));
    }

    @Test
    public void interceptedController11SomeAction() throws Exception {

        List<String> invocationList = new ArrayList<String>();
        InterceptedController11.invocationList = invocationList;
        Interceptor9.invocationList = invocationList;

        assertThatRequestFor("/intercepted11/some-action")
            .producesPage()
            .withH2Tag(withContent("Hello from someAction"));

        assertEquals(2, invocationList.size());
        assertEquals("someAction", invocationList.get(0));
        assertEquals("interceptor9-afterAction:req:resp:sess:someService", invocationList.get(1));
    }

    @Test
    public void interceptedController12InterceptedBeforeAction() throws Exception {

        assertThatRequestFor("/intercepted12/intercepted-before")
            .producesPage()
                .withH2Tag(withContent("Hello from interceptor5-beforeAction:req:resp:sess:someService"));
    }

    @Test
    public void interceptedController12InterceptedAfterAction() throws Exception {

        assertThatRequestFor("/intercepted12/intercepted-after")
            .producesPage()
            .withH2Tag(withContent("Hello from interceptor7-afterAction:req:resp:sess:someService"));
    }

    @Test
    public void interceptedController12InterceptedBefore2Action() throws Exception {

        List<String> invocationList = new ArrayList<String>();
        InterceptedController12.invocationList = invocationList;
        Interceptor6.invocationList = invocationList;

        assertThatRequestFor("/intercepted12/intercepted-before2")
            .producesPage()
            .withH2Tag(withContent("Hello from interceptedBefore2"));

        assertEquals(2, invocationList.size());
        assertEquals("interceptor6-beforeAction:req:resp:sess:someService", invocationList.get(0));
        assertEquals("interceptedBefore2", invocationList.get(1));
    }

    @Test
    public void interceptedController12InterceptedAfter2Action() throws Exception {

        List<String> invocationList = new ArrayList<String>();
        InterceptedController12.invocationList = invocationList;
        Interceptor8.invocationList = invocationList;

        assertThatRequestFor("/intercepted12/intercepted-after2")
            .producesPage()
            .withH2Tag(withContent("Hello from interceptedAfter2"));

        assertEquals(2, invocationList.size());
        assertEquals("interceptedAfter2", invocationList.get(0));
        assertEquals("interceptor8-afterAction:req:resp:sess:someService", invocationList.get(1));
    }

    @Test
    public void interceptedController12SomeAction() throws Exception {

        List<String> invocationList = new ArrayList<String>();
        InterceptedController12.invocationList = invocationList;
        Interceptor2.invocationList = invocationList;

        assertThatRequestFor("/intercepted12/some-action")
            .producesPage()
            .withH2Tag(withContent("Hello from interceptor2-beforeAction:req:resp:sess:someService"));

        assertEquals(0, invocationList.size());
    }

    @Test
    public void interceptedController12SomeAction2() throws Exception {

        List<String> invocationList = new ArrayList<String>();
        InterceptedController12.invocationList = invocationList;
        Interceptor3.invocationList = invocationList;

        assertThatRequestFor("/intercepted12/some-action2")
            .producesPage()
            .withH2Tag(withContent("Hello from interceptor3-afterAction:req:resp:sess:someService"));

        assertEquals(2, invocationList.size());
        assertEquals("interceptor3-beforeAction:req:resp:sess:someService", invocationList.get(0));
        assertEquals("someAction2", invocationList.get(1));
    }

    @Test
    public void interceptedController12ParamAction() throws Exception {

        List<String> invocationList = new ArrayList<String>();
        InterceptedController12.invocationList = invocationList;
        Interceptor10.invocationList = invocationList;

        assertThatRequestFor("/intercepted12/param?p1=test")
            .producesPage()
            .withH2Tag(withContent("Hello from test"));

        assertEquals(3, invocationList.size());
        assertEquals("interceptor10-beforeAction", invocationList.get(0));
        assertEquals("param", invocationList.get(1));
        assertEquals("interceptor10-afterAction", invocationList.get(2));
    }

    @Test
    public void defaultActionWithArgs() throws Exception {

        assertThatRequestFor("/default-args?p1=test")
            .producesPage()
            .withH2Tag(withContent("Hello from test"));
    }

    @Test
    public void classNameController() throws Exception {

        assertThatRequestFor("/ClassNameController/sayHello?name=John")
            .producesPage()
            .withH2Tag(withContent("Hello from classNameControllerAction:John"));
    }

    @Test
    public void httpMethodGET() throws Exception {

        assertThatPOSTRequestFor("/httpmethod1")
            .producesPage()
            .withH2Tag(withContent("Hello from default"));
    }

    @Test
    public void httpMethodGET2() throws Exception {

        assertThatGETRequestFor("/httpmethod1")
            .producesPage()
            .withH2Tag(withContent("Hello from get"));
    }

    @Test
    public void httpMethodGET3() throws Exception {

        assertThatRequestFor("/httpmethod1/sayHello").producesErrorPage();
    }
    
    @Test
    public void httpMethodGET4() throws Exception {

        assertThatRequestFor("/httpmethod1/doGetAction").producesErrorPage();
    }

    @Test
    public void httpMethodPOST() throws Exception {

        assertThatGETRequestFor("/httpmethod2")
            .producesPage()
            .withH2Tag(withContent("Hello from default"));
    }

    @Test
    public void httpMethodPOST2() throws Exception {

        assertThatPOSTRequestFor("/httpmethod2")
            .producesPage()
            .withH2Tag(withContent("Hello from post"));
    }

    @Test
    public void httpMethodPOST3() throws Exception {

        assertThatRequestFor("/httpmethod2/sayHello").producesErrorPage();
    }

    @Test
    public void httpMethodPUT() throws Exception {

        assertThatRequestFor("/httpmethod3")
            .producesPage()
            .withH2Tag(withContent("Hello from default"));
    }

    @Test
    public void httpMethodPUT2() throws Exception {

        assertThatPUTRequestFor("/httpmethod3")
            .producesPage()
            .withH2Tag(withContent("Hello from put"));
    }

    @Test
    public void httpMethodPUT3() throws Exception {

        assertThatRequestFor("/httpmethod3/doPutAction").producesErrorPage();
    }

    @Test
    public void httpMethodHEAD() throws Exception {

        assertThatRequestFor("/httpmethod4")
            .producesPage()
            .withH2Tag(withContent("Hello from default"));
    }

    @Test
    public void httpMethodHEAD2() throws Exception {
        
        assertThatHEADRequestFor("/httpmethod4").succeeds();
    }

    @Test
    public void httpMethodHEAD3() throws Exception {

        assertThatRequestFor("/httpmethod4/doHeadAction").producesErrorPage();
    }

    @Test
    public void httpMethodTRACE() throws Exception {

        assertThatRequestFor("/httpmethod5")
            .producesPage()
            .withH2Tag(withContent("Hello from default"));
    }

    @Test
    public void httpMethodTRACE2() throws Exception {

        assertThatRequestFor("/httpmethod5/doTraceAction").producesErrorPage();
    }

    @Test
    public void httpMethodDELETE() throws Exception {

        assertThatRequestFor("/httpmethod6")
            .producesPage()
            .withH2Tag(withContent("Hello from default"));
    }

    @Test
    public void httpMethodDELETE2() throws Exception {

        assertThatDELETERequestFor("/httpmethod6")
            .producesPage()
            .withH2Tag(withContent("Hello from delete"));
    }

    @Test
    public void httpMethodDELETE3() throws Exception {

        assertThatRequestFor("/httpmethod6/doDeleteAction").producesErrorPage();
    }

    @Test
    public void httpMethodOPTIONS() throws Exception {

        assertThatRequestFor("/httpmethod7")
            .producesPage()
            .withH2Tag(withContent("Hello from default"));
    }

    @Test
    public void httpMethodOPTIONS2() throws Exception {

        assertThatOPTIONSRequestFor("/httpmethod7")
            .producesPage()
            .withH2Tag(withContent("Hello from options"));
    }

    @Test
    public void httpMethodOPTIONS3() throws Exception {

        assertThatRequestFor("/httpmethod7/doOptionsAction").producesErrorPage();
    }

    @Test
    public void httpMethodMulti() throws Exception {

        assertThatGETRequestFor("/httpmethod8")
            .producesPage()
            .withH2Tag(withContent("Hello from multi"));
    }

    @Test
    public void httpMethodMulti2() throws Exception {

        assertThatPOSTRequestFor("/httpmethod8")
            .producesPage()
            .withH2Tag(withContent("Hello from multi"));
    }

    @Test
    public void httpMethodMulti3() throws Exception {

        assertThatGETRequestFor("/httpmethod8/sayHello")
            .producesPage()
            .withH2Tag(withContent("Hello from sayHello"));
    }

    @Test
    public void httpMethodMulti4() throws Exception {
        
        assertThatPOSTRequestFor("/httpmethod8/sayHello")
            .producesPage()
            .withH2Tag(withContent("Hello from sayHello"));
    }

    @Test
    public void interceptedController13NoAction() throws Exception {

        assertThatRequestFor("/intercepted13").producesErrorPage();
    }

    @Test
    public void interceptedController13POSTAction() throws Exception {

        List<String> invocationList = new ArrayList<String>();
        InterceptedController13.invocationList = invocationList;
        Interceptor1.invocationList = invocationList;
        Interceptor1b.invocationList = invocationList;

        assertThatPOSTRequestFor("/intercepted13")
            .producesPage()
            .withH2Tag(withContent("Hello from postAction"));

        assertEquals(5, invocationList.size());
        assertEquals("interceptor1-beforeAction:req:resp:sess:someService", invocationList.get(0));
        assertEquals("interceptor1b-beforeAction:req:resp:sess:someService", invocationList.get(1));
        assertEquals("postAction", invocationList.get(2));
        assertEquals("interceptor1b-afterAction:req:resp:sess:someService:ok", invocationList.get(3));
        assertEquals("interceptor1-afterAction:req:resp:sess:someService:ok", invocationList.get(4));
    }

    @Test
    public void interceptedController14NoAction() throws Exception {

        assertThatPOSTRequestFor("/intercepted14").producesErrorPage();
    }

    @Test
    public void interceptedController14GETAction() throws Exception {

        List<String> invocationList = new ArrayList<String>();
        InterceptedController14.invocationList = invocationList;
        Interceptor1.invocationList = invocationList;
        Interceptor1b.invocationList = invocationList;

        assertThatRequestFor("/intercepted14")
            .producesPage()
            .withH2Tag(withContent("Hello from getAction"));

        assertEquals(5, invocationList.size());
        assertEquals("interceptor1-beforeAction:req:resp:sess:someService", invocationList.get(0));
        assertEquals("interceptor1b-beforeAction:req:resp:sess:someService", invocationList.get(1));
        assertEquals("getAction", invocationList.get(2));
        assertEquals("interceptor1b-afterAction:req:resp:sess:someService:ok", invocationList.get(3));
        assertEquals("interceptor1-afterAction:req:resp:sess:someService:ok", invocationList.get(4));
    }

    @Test
    public void interceptedController15NoAction() throws Exception {

        assertThatPOSTRequestFor("/intercepted15").producesErrorPage();
    }

    @Test
    public void interceptedController15PUTAction() throws Exception {

        List<String> invocationList = new ArrayList<String>();
        InterceptedController15.invocationList = invocationList;
        Interceptor1.invocationList = invocationList;
        Interceptor1b.invocationList = invocationList;

        assertThatPUTRequestFor("/intercepted15")
            .producesPage()
            .withH2Tag(withContent("Hello from putAction"));

        assertEquals(5, invocationList.size());
        assertEquals("interceptor1-beforeAction:req:resp:sess:someService", invocationList.get(0));
        assertEquals("interceptor1b-beforeAction:req:resp:sess:someService", invocationList.get(1));
        assertEquals("putAction", invocationList.get(2));
        assertEquals("interceptor1b-afterAction:req:resp:sess:someService:ok", invocationList.get(3));
        assertEquals("interceptor1-afterAction:req:resp:sess:someService:ok", invocationList.get(4));
    }

    @Test
    public void interceptedController16NoAction() throws Exception {

        assertThatPOSTRequestFor("/intercepted16").producesErrorPage();
    }

    @Test
    public void interceptedController17NoAction() throws Exception {

        assertThatPOSTRequestFor("/intercepted17").producesErrorPage();
    }

    @Test
    public void interceptedController17HEADAction() throws Exception {

        List<String> invocationList = new ArrayList<String>();
        InterceptedController17.invocationList = invocationList;
        Interceptor1.invocationList = invocationList;
        Interceptor1b.invocationList = invocationList;

        assertThatHEADRequestFor("/intercepted17").succeeds();

        assertEquals(5, invocationList.size());
        assertEquals("interceptor1-beforeAction:req:resp:sess:someService", invocationList.get(0));
        assertEquals("interceptor1b-beforeAction:req:resp:sess:someService", invocationList.get(1));
        assertEquals("headAction", invocationList.get(2));
        assertEquals("interceptor1b-afterAction:req:resp:sess:someService:ok", invocationList.get(3));
        assertEquals("interceptor1-afterAction:req:resp:sess:someService:ok", invocationList.get(4));
    }

    @Test
    public void interceptedController18NoAction() throws Exception {

        assertThatPOSTRequestFor("/intercepted18").producesErrorPage();
    }

    @Test
    public void interceptedController18OPTIONSAction() throws Exception {

        List<String> invocationList = new ArrayList<String>();
        InterceptedController18.invocationList = invocationList;
        Interceptor1.invocationList = invocationList;
        Interceptor1b.invocationList = invocationList;

        assertThatOPTIONSRequestFor("/intercepted18")
            .producesPage()
            .withH2Tag(withContent("Hello from optionsAction"));

        assertEquals(5, invocationList.size());
        assertEquals("interceptor1-beforeAction:req:resp:sess:someService", invocationList.get(0));
        assertEquals("interceptor1b-beforeAction:req:resp:sess:someService", invocationList.get(1));
        assertEquals("optionsAction", invocationList.get(2));
        assertEquals("interceptor1b-afterAction:req:resp:sess:someService:ok", invocationList.get(3));
        assertEquals("interceptor1-afterAction:req:resp:sess:someService:ok", invocationList.get(4));
    }

    @Test
    public void interceptedController19NoAction() throws Exception {

        assertThatPOSTRequestFor("/intercepted19").producesErrorPage();
    }

    @Test
    public void interceptedController19DELETEAction() throws Exception {

        List<String> invocationList = new ArrayList<String>();
        InterceptedController19.invocationList = invocationList;
        Interceptor1.invocationList = invocationList;
        Interceptor1b.invocationList = invocationList;

        assertThatDELETERequestFor("/intercepted19")
            .producesPage()
            .withH2Tag(withContent("Hello from deleteAction"));

        assertEquals(5, invocationList.size());
        assertEquals("interceptor1-beforeAction:req:resp:sess:someService", invocationList.get(0));
        assertEquals("interceptor1b-beforeAction:req:resp:sess:someService", invocationList.get(1));
        assertEquals("deleteAction", invocationList.get(2));
        assertEquals("interceptor1b-afterAction:req:resp:sess:someService:ok", invocationList.get(3));
        assertEquals("interceptor1-afterAction:req:resp:sess:someService:ok", invocationList.get(4));
    }
    
    @Test
    public void paramPathController() throws Exception {
        
        assertThatRequestFor("/parampath/say/John")
            .producesPage()
            .withH2Tag(withContent("Hello from John"));
    }
    
    @Test
    public void paramPathController_NoMatchingRoute() throws Exception {
        
        assertThatRequestFor("/parampath/regex/John").producesErrorPage();
    }
    
    @Test
    public void paramPathController_MatchingRegexRoute() throws Exception {
        
        assertThatRequestFor("/parampath/regex/john")
        .producesPage()
        .withH2Tag(withContent("Hello from john"));
    }
    
    @Test
    public void paramPathController_MultiParams() throws Exception {
        
        assertThatRequestFor("/parampath/multi/John/123")
        .producesPage()
        .withH2Tag(withContent("Hello from John, 123"));
    }
    
    @Test
    public void paramPathController_Encoded() throws Exception {
        
        assertThatRequestFor("/parampath/encoded/a%2Bb/John/123")
        .producesPage()
        .withH2Tag(withContent("Hello from John, 123"));
    }
    
    @Test
    public void paramPathController_GET() throws Exception {
        
        assertThatGETRequestFor("/parampath-http/say/John")
        .producesPage()
        .withH2Tag(withContent("Hello from John, GET"));
    }
    
    @Test
    public void paramPathController_POST() throws Exception {
        
        assertThatPOSTRequestFor("/parampath-http/say/John")
        .producesPage()
        .withH2Tag(withContent("Hello from John, POST"));
    }
    
    @Test
    public void paramPathController_DELETE() throws Exception {
        
        assertThatDELETERequestFor("/parampath-http/say/John")
        .producesPage()
        .withH2Tag(withContent("Hello from John, DELETE"));
    }
    
    @Test
    public void plainText() throws Exception {
        
        assertThatRequestFor("/index/plain-text").producesPage().withContent("hello");
    }
    
    @Test
    public void statusOK() throws Exception {
        
        assertThatRequestFor("/index/status-ok").producesResponse()
            .withStatus(200)
            .withContent("it's ok")
            .withContentType("text/plain")
            .withHeader("Content-Language", "English");
    }
    
    @Test
    public void inputStreamUsedAsParamByItself() throws Exception {
        
        assertThatRequestFor("/stream-param/inputStreamAlone")
        .producesPage()
        .withH2Tag(withContent("ServletInputStream"));
    }
    
    @Test
    public void inputStreamUsedWithOtherParam() throws Exception {
        
        assertThatRequestFor("/stream-param/inputStreamWithParam?p1=other")
        .producesPage()
        .withH2Tag(withContent("other-ServletInputStream"));
    }
    
    @Test
    public void controllerInheritance() throws Exception {
        
        assertThatRequestFor("/inheritance/test")
        .producesPage()
        .withH2Tag(withContent("Hello from inherited"));
    }
}