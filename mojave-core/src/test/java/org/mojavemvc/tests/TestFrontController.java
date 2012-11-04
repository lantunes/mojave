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
        assertThatRequestFor("/mvc/mvctest?c=index&a=default").producesErrorPage();
    }

    @Test
    public void indexControllerDefaultAction2() throws Exception {

        assertThatRequestFor("/mvc/mvctest?c=index")
            .producesPage().withTag("h2", withContent("Hello from index "));
    }

    @Test
    public void indexControllerDefaultAction3() throws Exception {

        assertThatRequestFor("/mvc/mvctest")
            .producesPage().withTag("h2", withContent("Hello from  "));
    }

    @Test
    public void indexControllerTestAction() throws Exception {

        assertThatRequestFor("/mvc/mvctest?c=index&a=test")
            .producesPage().withTag("h1", 
                    withContent("This is the test.jsp file of the IndexController testAction!"));
    }

    @Test
    public void indexControllerWithParamAction() throws Exception {

        assertThatRequestFor("/mvc/mvctest?c=index&a=with-param&var=hello")
            .producesPage()
                .withTag("h1", 
                        withContent("This is the param.jsp file of the IndexController withParamAction!"))
                .withTag("h2",
                        withContent("Hello from hello"));
    }

    @Test
    public void indexControllerAnotherParamAction() throws Exception {

        assertThatRequestFor("/mvc/mvctest?c=index&a=another-param&var=hello")
            .producesPage()
                .withTag("h1", 
                        withContent("This is the param.jsp file of the IndexController withParamAction!"))
                .withTag("h2",
                        withContent("Hello from hello"));
    }

    @Test
    public void indexControllerSomeServiceAction() throws Exception {

        assertThatRequestFor("/mvc/mvctest?c=index&a=some-service&var=hello")
            .producesPage()
                .withTag("h1", 
                        withContent("This is the some-service.jsp file of the IndexController someServiceAction!"))
                .withTag("h2",
                        withContent("answered hello"));
    }

    @Test
    public void indexControllerActionAnnotation() throws Exception {

        assertThatRequestFor("/mvc/mvctest?c=index&a=test-annotation&var=annotationTest")
            .producesPage()
                .withTag("h1", 
                        withContent("This is the param.jsp file of the IndexController withParamAction!"))
                .withTag("h2",
                        withContent("Hello from annotationTest"));
    }

    @Test
    public void someControllerClassControllerAnnotation() throws Exception {

        assertThatRequestFor("/mvc/mvctest?c=annot&a=some-action&var=contollerAnnotationTest")
            .producesPage()
                .withTag("h1", 
                        withContent("This is the some-controller.jsp file of the SomeControllerClass doSomething action!"))
                .withTag("h2",
                        withContent("contollerAnnotationTest"));
    }

    @Test
    public void indexControllerParamAnnotationTest1() throws Exception {

        assertThatRequestFor("/mvc/mvctest?c=index&a=param-annotation-string&p1=param1")
            .producesPage()
                .withTag("h1", 
                        withContent("This is the params.jsp file of the IndexController @Param test action!"))
                .withTag("h2",
                        withContent("Hello from param1"));
    }

    @Test
    public void indexControllerParamAnnotationTest1_WithNull() throws Exception {

        assertThatRequestFor("/mvc/mvctest?c=index&a=param-annotation-string")
            .producesPage()
                .withTag("h1", 
                        withContent("This is the params.jsp file of the IndexController @Param test action!"))
                .withTag("h2",
                        withContent("Hello from null"));
    }

    @Test
    public void indexControllerParamAnnotationTest2() throws Exception {

        assertThatRequestFor("/mvc/mvctest?c=index&a=param-annotation-string2&p1=param1&p2=param2")
            .producesPage()
                .withTag("h1", 
                        withContent("This is the params2.jsp file of the IndexController @Param test action!"))
                .withTag("h2",
                        withContent("Hello from param1, param2"));
    }

    @Test
    public void indexControllerParamAnnotationTest2_WithNull() throws Exception {

        assertThatRequestFor("/mvc/mvctest?c=index&a=param-annotation-string2&p2=param2")
            .producesPage()
                .withTag("h1", 
                        withContent("This is the params2.jsp file of the IndexController @Param test action!"))
                .withTag("h2",
                        withContent("Hello from null, param2"));
    }

    @Test
    public void indexControllerParamAnnotationTest3() throws Exception {

        assertThatRequestFor("/mvc/mvctest?c=index&a=param-annotation-int&p1=123456")
            .producesPage()
                .withTag("h1", 
                        withContent("This is the params.jsp file of the IndexController @Param test action!"))
                .withTag("h2",
                        withContent("Hello from 123456"));
    }

    @Test
    public void indexControllerParamAnnotationTest3_WithNull() throws Exception {

        assertThatRequestFor("/mvc/mvctest?c=index&a=param-annotation-int")
            .producesPage()
                .withTag("h1", 
                        withContent("This is the params.jsp file of the IndexController @Param test action!"))
                .withTag("h2",
                        withContent("Hello from 0"));
    }

    @Test
    public void indexControllerParamAnnotationTest3_WithError() throws Exception {

        assertThatRequestFor("/mvc/mvctest?c=index&a=param-annotation-int&p1=hello").producesErrorPage();
    }

    @Test
    public void indexControllerParamAnnotationTest4() throws Exception {

        assertThatRequestFor("/mvc/mvctest?c=index&a=param-annotation-double&p1=123.456")
            .producesPage()
                .withTag("h1", 
                        withContent("This is the params.jsp file of the IndexController @Param test action!"))
                .withTag("h2",
                        withContent("Hello from 123.456"));
    }

    @Test
    public void indexControllerParamAnnotationTest4_WithNull() throws Exception {

        assertThatRequestFor("/mvc/mvctest?c=index&a=param-annotation-double")
            .producesPage()
                .withTag("h1", 
                        withContent("This is the params.jsp file of the IndexController @Param test action!"))
                .withTag("h2",
                        withContent("Hello from 0.0"));
    }

    @Test
    public void indexControllerParamAnnotationTest4_WithError() throws Exception {

        assertThatRequestFor("/mvc/mvctest?c=index&a=param-annotation-double&p1=hello").producesErrorPage();
    }

    @Test
    public void indexControllerParamAnnotationTest5() throws Exception {

        assertThatRequestFor("/mvc/mvctest?c=index&a=param-annotation-date&p1=2011-03-01")
            .producesPage()
                .withTag("h1", 
                        withContent("This is the params.jsp file of the IndexController @Param test action!"))
                .withTag("h2",
                        withContent("Hello from 2011-03-01"));
    }

    @Test
    public void indexControllerParamAnnotationTest5_WithNull() throws Exception {

        assertThatRequestFor("/mvc/mvctest?c=index&a=param-annotation-date")
            .producesPage()
                .withTag("h1", 
                        withContent("This is the params.jsp file of the IndexController @Param test action!"))
                .withTag("h2",
                        withContent("Hello from null"));
    }

    @Test
    public void indexControllerParamAnnotationTest5_WithError() throws Exception {

        assertThatRequestFor("/mvc/mvctest?c=index&a=param-annotation-date&p1=hello").producesErrorPage();
    }

    @Test
    public void indexControllerParamAnnotationTestAll() throws Exception {

        assertThatRequestFor("/mvc/mvctest?c=index&a=param-annotation-all&" +
        		"p1=2011-03-01&p2=hello&p3=123&p4=1.45")
            .producesPage()
                .withTag("h1", 
                        withContent("This is the params3.jsp file of the IndexController @Param test action!"))
                .withTag("h2",
                        withContent("Hello from 2011-03-01, hello, 123, 1.45"));
    }

    @Test
    public void indexControllerParamAnnotationTest6() throws Exception {

        assertThatRequestFor("/mvc/mvctest?c=index&a=param-annotation-bool&p1=true")
            .producesPage()
                .withTag("h1", 
                        withContent("This is the params.jsp file of the IndexController @Param test action!"))
                .withTag("h2",
                        withContent("Hello from true"));
    }

    @Test
    public void indexControllerParamAnnotationTest6b() throws Exception {

        assertThatRequestFor("/mvc/mvctest?c=index&a=param-annotation-bool&p1=false")
            .producesPage()
                .withTag("h1", 
                        withContent("This is the params.jsp file of the IndexController @Param test action!"))
                .withTag("h2",
                        withContent("Hello from false"));
    }

    @Test
    public void indexControllerParamAnnotationTest6c() throws Exception {

        assertThatRequestFor("/mvc/mvctest?c=index&a=param-annotation-bool&p1=t")
            .producesPage()
                .withTag("h1", 
                        withContent("This is the params.jsp file of the IndexController @Param test action!"))
                .withTag("h2",
                        withContent("Hello from true"));
    }

    @Test
    public void indexControllerParamAnnotationTest6d() throws Exception {

        assertThatRequestFor("/mvc/mvctest?c=index&a=param-annotation-bool&p1=f")
            .producesPage()
                .withTag("h1", 
                        withContent("This is the params.jsp file of the IndexController @Param test action!"))
                .withTag("h2",
                        withContent("Hello from false"));
    }

    @Test
    public void indexControllerParamAnnotationTest6e() throws Exception {

        assertThatRequestFor("/mvc/mvctest?c=index&a=param-annotation-bool&p1=1")
            .producesPage()
                .withTag("h1", 
                        withContent("This is the params.jsp file of the IndexController @Param test action!"))
                .withTag("h2",
                        withContent("Hello from true"));
    }

    @Test
    public void indexControllerParamAnnotationTest6f() throws Exception {

        assertThatRequestFor("/mvc/mvctest?c=index&a=param-annotation-bool&p1=0")
            .producesPage()
                .withTag("h1", 
                        withContent("This is the params.jsp file of the IndexController @Param test action!"))
                .withTag("h2",
                        withContent("Hello from false"));
    }

    @Test
    public void indexControllerParamAnnotationTest6_WithError() throws Exception {

        assertThatRequestFor("/mvc/mvctest?c=index&a=param-annotation-bool&p1=hello").producesErrorPage();
    }

    @Test
    public void indexControllerParamAnnotationTest6_WithError2() throws Exception {

        assertThatRequestFor("/mvc/mvctest?c=index&a=param-annotation-bool")
            .producesPage()
                .withTag("h1", 
                        withContent("This is the params.jsp file of the IndexController @Param test action!"))
                .withTag("h2",
                        withContent("Hello from false"));
    }

    @Test
    public void indexControllerParamAnnotationTestIntCollection() throws Exception {

        assertThatRequestFor("/mvc/mvctest?c=index&a=param-annotation-ints&" +
        		"p1=123&p1=456&p1=789&p1=321")
		    .producesPage()
                .withTag("h1", 
                        withContent("This is the params3.jsp file of the IndexController @Param test action!"))
                .withTag("h2",
                        withContent("Hello from 123, 456, 789, 321"));
    }

    @Test
    public void indexControllerParamAnnotationTestStringCollection() throws Exception {

        assertThatRequestFor("/mvc/mvctest?c=index&a=param-annotation-strings&" +
        		"p1=abc&p1=def&p1=ghi&p1=jkl")
        	.producesPage()
                .withTag("h1", 
                        withContent("This is the params3.jsp file of the IndexController @Param test action!"))
                .withTag("h2",
                        withContent("Hello from abc, def, ghi, jkl"));
    }

    @Test
    public void indexControllerParamAnnotationTestDoubleCollection() throws Exception {

        assertThatRequestFor("/mvc/mvctest?c=index&a=param-annotation-doubles&" +
        		"p1=1.1&p1=2.2&p1=3.3&p1=4.4")
        	.producesPage()
                .withTag("h1", 
                        withContent("This is the params3.jsp file of the IndexController @Param test action!"))
                .withTag("h2",
                        withContent("Hello from 1.1, 2.2, 3.3, 4.4"));
    }

    @Test
    public void indexControllerParamAnnotationTestDateCollection() throws Exception {

        assertThatRequestFor("/mvc/mvctest?c=index&a=param-annotation-dates&"
                + "p1=2011-03-01&p1=2010-02-09&p1=2009-11-23&p1=2008-05-03")
            .producesPage()
                .withTag("h1", 
                        withContent("This is the params3.jsp file of the IndexController @Param test action!"))
                .withTag("h2",
                        withContent("Hello from 2011-03-01, 2010-02-09, 2009-11-23, 2008-05-03"));
    }

    @Test
    public void indexControllerParamAnnotationTestBooleanCollection() throws Exception {

        assertThatRequestFor("/mvc/mvctest?c=index&a=param-annotation-bools&" +
        		"p1=t&p1=false&p1=f&p1=1")
        	.producesPage()
                .withTag("h1", 
                        withContent("This is the params3.jsp file of the IndexController @Param test action!"))
                .withTag("h2",
                        withContent("Hello from true, false, false, true"));
    }

    @Test
    public void indexControllerInjectedAction() throws Exception {

        assertThatRequestFor("/mvc/mvctest?c=index&a=injected")
            .producesPage()
                .withTag("h1", 
                        withContent("This is the param.jsp file of the IndexController withParamAction!"))
                .withTag("h2",
                        withContent("Hello from injected-index"));
    }

    @Test
    public void indexControllerTestInitAction() throws Exception {

        assertThatRequestFor("/mvc/mvctest?c=index&a=test-init")
            .producesPage()
                .withTag("h1", 
                        withContent("This is the param.jsp file of the IndexController withParamAction!"))
                .withTag("h2",
                        withContent("Hello from init-called"));
    }

    @Test
    public void formController() throws Exception {

        assertThatRequestFor("/mvc/mvctest?c=form-controller")
            .afterSubmittingForm("test-form", 
                    withValueFor("userName").setTo("john"),
                    withValueFor("password").setTo("doe"))
            .producesPage()
                .withElement("userName", withContent("john"))
                .withElement("password", withContent("doe"));
    }

    @Test
    public void formControllerWithRegularParam() throws Exception {

        assertThatRequestFor("/mvc/mvctest?c=form-controller&a=form2")
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

        assertThatRequestFor("/mvc/mvctest?c=form-controller&a=form3")
            .afterSubmittingForm("test-form", 
                    withValueFor("userName").setTo("john"),
                    withValueFor("password").setTo("doe"))
            .producesPage()
                .withElement("userName", withContent("JOHN"))
                .withElement("password", withContent("DOE"));
    }

    @Test
    public void formControllerPopulated() throws Exception {

        assertThatRequestFor("/mvc/mvctest?c=form-controller&a=populated")
            .producesPage()
                .withElement("userName", withAttribute("value").setTo("uname"))
                .withElement("password", withAttribute("value").setTo("pswd"));
    }

    @Test
    public void formControllerWithBooleanTrue() throws Exception {

        assertThatRequestFor("/mvc/mvctest?c=form-controller&a=form4")
            .afterSubmittingForm("test-form", 
                    withCheckBox("someFlag").checked())
            .producesPage()
                .withElement("flag", withContent("true"));
    }

    @Test
    public void formControllerWithBooleanFalse() throws Exception {

        assertThatRequestFor("/mvc/mvctest?c=form-controller&a=form4")
            .afterSubmittingForm("test-form")
            .producesPage()
                .withElement("flag", withContent("false"));
    }

    @Test
    public void redirectingController() throws Exception {

        assertThatRequestFor("/mvc/mvctest?c=redirecting&a=redirect")
            .producesPage().withTag("h1", withContent("redirected!"));
    }

    @Test
    public void redirectingController2() throws Exception {

        assertThatRequestFor("/mvc/mvctest?c=redirecting2&a=doSomething")
            .producesPage().withTag("h1", withContent("redirected!"));
    }

    @Test
    public void streamingControllerXML() throws Exception {

        assertThatRequestFor("/mvc/mvctest?c=stream&a=xml")
            .producesPage()
                .withContentType("application/xml")
                .withContent("<?xml version=\"1.0\" encoding=\"UTF-8\"?><Test><hello/></Test>");
    }

    @Test
    public void streamingControllerJSON() throws Exception {

        assertThatRequestFor("/mvc/mvctest?c=stream&a=json")
            .producesPage()
                .withContentType("application/json")
                .withContent("{\"Test\":{\"hello\": 1}}");
    }

    @Test
    public void streamingController2() throws Exception {

        assertThatRequestFor("/mvc/mvctest?c=stream2&a=doSomething")
            .producesPage()
                .withContentType("application/xml")
                .withContent("<?xml version=\"1.0\" encoding=\"UTF-8\"?><Test><hello/></Test>");
    }

    @Test
    public void streamingController3() throws Exception {

        assertThatRequestFor("/mvc/mvctest?c=stream3&a=doSomething")
            .producesPage()
                .withContentType("application/xml")
                .withContent("<?xml version=\"1.0\" encoding=\"UTF-8\"?><Test><hello/></Test>");
    }

    @Test
    public void dispatchingController() throws Exception {

        assertThatRequestFor("/mvc/mvctest?c=dispatching&a=doSomething")
            .producesPage().withTag("h2", withContent("Hello from dispatched"));
    }

    @Test
    public void someStatefulControllerDefaultAction() throws Exception {

        assertThatRequestFor("/mvc/mvctest?c=some-stateful")
            .producesPage()
                .withTag("h1", 
                        withContent("This is the index.jsp file of the IndexController defaultAction!"));
    }

    @Test
    public void someStatefulControllerSomeAction() throws Exception {

        assertThatRequestFor("/mvc/mvctest?c=some-stateful&a=some-action")
            .producesPage()
                .withTag("h1", 
                        withContent("This is the stateful.jsp file of the SomeStatefulController someAction!"));
    }

    @Test
    public void someStatefulControllerVarAction() throws Exception {

        assertThatRequestFor("/mvc/mvctest?c=some-stateful&a=set-var&var=statetest")
            .producesPage()
                .withTag("h1", 
                        withContent("This is the stateful.jsp file of the SomeStatefulController someAction!"));

        /* make a second request */
        assertThatRequestFor("/mvc/mvctest?c=some-stateful&a=get-var")
            .producesPage()
                .withTag("h2", withContent("Hello from statetest"));
    }

    @Test
    public void someStatefulControllerVarAction2() throws Exception {

        assertThatRequestFor("/mvc/mvctest?c=some-stateful&a=set-var&var=statetest")
            .producesPage()
                .withTag("h1", 
                        withContent("This is the stateful.jsp file of the SomeStatefulController someAction!"));

        /* make a second request with a new client, creating a new session */
        newWebClient();
        assertThatRequestFor("/mvc/mvctest?c=some-stateful&a=get-var")
            .producesPage()
                .withTag("h2", withContent("Hello from null")); /* no state stored */
    }

    @Test
    public void someStatefulControllerReqAction() throws Exception {

        /*
         * Test that the injected HttpServletRequests are different, by
         * examining the hex hashcode that is part of the object name obtained
         * by calling toString() in the request instance.
         */

        String hexHashcode1 = makeRequest("/mvc/mvctest?c=some-stateful&a=get-req").getTagContent("h2");

        /* make a second request */
        String hexHashcode2 = makeRequest("/mvc/mvctest?c=some-stateful&a=get-req").getTagContent("h2");

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

        String hexHashcode1 = makeRequest("/mvc/mvctest?c=some-stateful&a=get-inj").getTagContent("h2");

        /* make a second request */
        String hexHashcode2 = makeRequest("/mvc/mvctest?c=some-stateful&a=get-inj").getTagContent("h2");

        /* check that a new @Inject dependency was injected */
        assertFalse(hexHashcode1.equals(hexHashcode2));
    }

    @Test
    public void someStatefulControllerTestInitAction() throws Exception {

        assertThatRequestFor("/mvc/mvctest?c=some-stateful&a=test-init")
            .producesPage().withTag("h2", withContent("Hello from init-called: 1"));

        /*
         * test that init() is called only once after construction, and not per
         * request
         */
        assertThatRequestFor("/mvc/mvctest?c=some-stateful&a=test-init")
            .producesPage().withTag("h2", withContent("Hello from init-called: 1"));
    }

    @Test
    public void injectableControllerTestAction() throws Exception {

        assertThatRequestFor("/mvc/mvctest?c=injectable&a=test&var=inj")
            .producesPage()
                .withTag("h1", 
                        withContent("This is the injectable.jsp file of the InjectableController testAction!"))
                .withTag("h2", 
                        withContent("injected-inj"));
    }

    @Test
    public void someSingletonControllerSomeAction() throws Exception {

        assertThatRequestFor("/mvc/mvctest?c=some-singleton&a=some-action")
            .producesPage()
                .withTag("h1", 
                        withContent("This is the singleton.jsp file of the SomeSingletonController someAction!"));
    }

    @Test
    public void someSingletonControllerVarAction() throws Exception {

        assertThatRequestFor("/mvc/mvctest?c=some-singleton&a=set-var&var=statetest")
            .producesPage()
                .withTag("h1", 
                        withContent("This is the singleton.jsp file of the SomeSingletonController someAction!"));

        /* make a second request */
        assertThatRequestFor("/mvc/mvctest?c=some-singleton&a=get-var")
            .producesPage()
                .withTag("h2", 
                        withContent("Hello from statetest"));
    }

    @Test
    public void someSingletonControllerVarAction2() throws Exception {

        assertThatRequestFor("/mvc/mvctest?c=some-singleton&a=set-var&var=statetest")
            .producesPage()
                .withTag("h1", 
                        withContent("This is the singleton.jsp file of the SomeSingletonController someAction!"));

        /* make a second request with a new client */
        newWebClient();
        assertThatRequestFor("/mvc/mvctest?c=some-singleton&a=get-var")
            .producesPage()
                .withTag("h2", 
                        withContent("Hello from statetest"));
    }

    @Test
    public void someSingletonControllerReqAction() throws Exception {

        /*
         * Test that the injected HttpServletRequests are different, by
         * examining the hex hashcode that is part of the object name obtained
         * by calling toString() in the request instance.
         */

        String hexHashcode1 = makeRequest("/mvc/mvctest?c=some-singleton&a=get-req").getTagContent("h2");

        /* make a second request */
        String hexHashcode2 = makeRequest("/mvc/mvctest?c=some-singleton&a=get-req").getTagContent("h2");

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

        String hexHashcode1 = makeRequest("/mvc/mvctest?c=some-singleton&a=get-inj").getTagContent("h2");

        /* make a second request */
        String hexHashcode2 = makeRequest("/mvc/mvctest?c=some-singleton&a=get-inj").getTagContent("h2");

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

        assertThatRequestFor("/mvc/mvctest?c=before&a=index")
            .producesPage()
                .withTag("h1", 
                        withContent("This is the before.jsp file of the BeforeController beforeAction!"));
    }

    @Test
    public void afterControllerIndexAction() throws Exception {

        assertThatRequestFor("/mvc/mvctest?c=after&a=index")
            .producesPage()
                .withTag("h1", 
                        withContent("This is the after.jsp file of the AfterController afterAction!"));
    }

    @Test
    public void beforeWithContextControllerIndexAction() throws Exception {

        assertThatRequestFor("/mvc/mvctest?c=beforectx&a=index&p1=testctx")
            .producesPage()
                .withTag("h2", 
                        withContent("Hello from testctx"));
    }

    @Test
    public void afterWithContextControllerIndexAction() throws Exception {

        assertThatRequestFor("/mvc/mvctest?c=afterctx&a=index&p1=testctx")
            .producesPage()
                .withTag("h2", 
                        withContent("Hello from testctx"));
    }

    @Test
    public void afterWithContextControllerDefaultAction() throws Exception {

        assertThatRequestFor("/mvc/mvctest?c=afterctx2")
            .producesPage()
                .withTag("h2", 
                        withContent("Hello from default"));
    }

    @Test
    public void beforeWithModelControllerIndexAction() throws Exception {

        assertThatRequestFor("/mvc/mvctest?c=beforectx2&a=index&userName=john&password=doe")
            .producesPage()
                .withTag("h2", 
                        withContent("Hello from john doe"));
    }

    @Test
    public void interceptedController1SomeAction() throws Exception {

        List<String> invocationList = new ArrayList<String>();
        InterceptedController1.invocationList = invocationList;
        Interceptor1.invocationList = invocationList;

        assertThatRequestFor("/mvc/mvctest?c=intercepted1&a=some-action")
            .producesPage()
                .withTag("h2", 
                        withContent("Hello from someAction"));

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

        assertThatRequestFor("/mvc/mvctest?c=intercepted2&a=some-action")
            .producesPage()
                .withTag("h2", 
                        withContent("Hello from someAction"));

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

        assertThatRequestFor("/mvc/mvctest?c=intercepted2")
            .producesPage()
                .withTag("h2", 
                        withContent("Hello from defaultAction"));

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

        assertThatRequestFor("/mvc/mvctest?c=intercepted3&a=some-action")
            .producesPage()
                .withTag("h2", 
                        withContent("Hello from someAction"));

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

        assertThatRequestFor("/mvc/mvctest?c=intercepted4&a=some-action")
            .producesPage()
                .withTag("h2", 
                        withContent("Hello from someAction"));

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

        assertThatRequestFor("/mvc/mvctest?c=intercepted4")
            .producesPage()
                .withTag("h2", 
                        withContent("Hello from defaultAction"));

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

        assertThatRequestFor("/mvc/mvctest?c=intercepted1")
            .producesPage()
                .withTag("h2", 
                        withContent("Hello from defaultAction"));

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

        assertThatRequestFor("/mvc/mvctest?c=intercepted3")
            .producesPage()
                .withTag("h2", 
                        withContent("Hello from defaultAction"));

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

        assertThatRequestFor("/mvc/mvctest?c=intercepted5")
            .producesPage()
                .withTag("h2", 
                        withContent("Hello from defaultAction"));

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

        assertThatRequestFor("/mvc/mvctest?c=intercepted5&a=some-action")
            .producesPage()
                .withTag("h2", 
                        withContent("Hello from someAction"));

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
        
        assertThatRequestFor("/mvc/mvctest?c=intercepted6")
            .producesPage()
                .withTag("h2", 
                        withContent("Hello from defaultAction"));

        assertEquals(1, invocationList.size());
        assertEquals("defaultAction", invocationList.get(0));
    }

    @Test
    public void interceptedController7SomeAction() throws Exception {

        List<String> invocationList = new ArrayList<String>();
        InterceptedController7.invocationList = invocationList;
        Interceptor1.invocationList = invocationList;

        assertThatRequestFor("/mvc/mvctest?c=intercepted7&a=some-action")
            .producesPage()
                .withTag("h2", 
                        withContent("Hello from someAction"));

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

        assertThatRequestFor("/mvc/mvctest?c=intercepted8")
            .producesPage()
                .withTag("h2", 
                        withContent("Hello from defaultAction"));

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

        assertThatRequestFor("/mvc/mvctest?c=intercepted8&a=some-action")
            .producesPage()
                .withTag("h2", 
                        withContent("Hello from someAction"));

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

        assertThatRequestFor("/mvc/mvctest?c=intercepted9")
            .producesPage()
                .withTag("h2", 
                        withContent("Hello from defaultAction"));

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

        assertThatRequestFor("/mvc/mvctest?c=intercepted9&a=some-action")
            .producesPage()
                .withTag("h2", 
                        withContent("Hello from someAction"));

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

        assertThatRequestFor("/mvc/mvctest?c=intercepted10&a=some-action")
            .producesPage()
                .withTag("h2", 
                        withContent("Hello from someAction"));

        assertEquals(2, invocationList.size());
        assertEquals("interceptor4-beforeAction:req:resp:sess:someService", invocationList.get(0));
        assertEquals("someAction", invocationList.get(1));
    }

    @Test
    public void interceptedController11SomeAction() throws Exception {

        List<String> invocationList = new ArrayList<String>();
        InterceptedController11.invocationList = invocationList;
        Interceptor9.invocationList = invocationList;

        assertThatRequestFor("/mvc/mvctest?c=intercepted11&a=some-action")
            .producesPage()
                .withTag("h2", 
                        withContent("Hello from someAction"));

        assertEquals(2, invocationList.size());
        assertEquals("someAction", invocationList.get(0));
        assertEquals("interceptor9-afterAction:req:resp:sess:someService", invocationList.get(1));
    }

    @Test
    public void interceptedController12InterceptedBeforeAction() throws Exception {

        assertThatRequestFor("/mvc/mvctest?c=intercepted12&a=intercepted-before")
            .producesPage()
                .withTag("h2", 
                        withContent("Hello from interceptor5-beforeAction:req:resp:sess:someService"));
    }

    @Test
    public void interceptedController12InterceptedAfterAction() throws Exception {

        assertThatRequestFor("/mvc/mvctest?c=intercepted12&a=intercepted-after")
            .producesPage()
                .withTag("h2", 
                        withContent("Hello from interceptor7-afterAction:req:resp:sess:someService"));
    }

    @Test
    public void interceptedController12InterceptedBefore2Action() throws Exception {

        List<String> invocationList = new ArrayList<String>();
        InterceptedController12.invocationList = invocationList;
        Interceptor6.invocationList = invocationList;

        assertThatRequestFor("/mvc/mvctest?c=intercepted12&a=intercepted-before2")
            .producesPage()
                .withTag("h2", 
                        withContent("Hello from interceptedBefore2"));

        assertEquals(2, invocationList.size());
        assertEquals("interceptor6-beforeAction:req:resp:sess:someService", invocationList.get(0));
        assertEquals("interceptedBefore2", invocationList.get(1));
    }

    @Test
    public void interceptedController12InterceptedAfter2Action() throws Exception {

        List<String> invocationList = new ArrayList<String>();
        InterceptedController12.invocationList = invocationList;
        Interceptor8.invocationList = invocationList;

        assertThatRequestFor("/mvc/mvctest?c=intercepted12&a=intercepted-after2")
            .producesPage()
                .withTag("h2", 
                        withContent("Hello from interceptedAfter2"));

        assertEquals(2, invocationList.size());
        assertEquals("interceptedAfter2", invocationList.get(0));
        assertEquals("interceptor8-afterAction:req:resp:sess:someService", invocationList.get(1));
    }

    @Test
    public void interceptedController12SomeAction() throws Exception {

        List<String> invocationList = new ArrayList<String>();
        InterceptedController12.invocationList = invocationList;
        Interceptor2.invocationList = invocationList;

        assertThatRequestFor("/mvc/mvctest?c=intercepted12&a=some-action")
            .producesPage()
                .withTag("h2", 
                        withContent("Hello from interceptor2-beforeAction:req:resp:sess:someService"));

        assertEquals(0, invocationList.size());
    }

    @Test
    public void interceptedController12SomeAction2() throws Exception {

        List<String> invocationList = new ArrayList<String>();
        InterceptedController12.invocationList = invocationList;
        Interceptor3.invocationList = invocationList;

        assertThatRequestFor("/mvc/mvctest?c=intercepted12&a=some-action2")
            .producesPage()
                .withTag("h2", 
                        withContent("Hello from interceptor3-afterAction:req:resp:sess:someService"));

        assertEquals(2, invocationList.size());
        assertEquals("interceptor3-beforeAction:req:resp:sess:someService", invocationList.get(0));
        assertEquals("someAction2", invocationList.get(1));
    }

    @Test
    public void interceptedController12ParamAction() throws Exception {

        List<String> invocationList = new ArrayList<String>();
        InterceptedController12.invocationList = invocationList;
        Interceptor10.invocationList = invocationList;

        assertThatRequestFor("/mvc/mvctest?c=intercepted12&a=param&p1=test")
            .producesPage()
                .withTag("h2", 
                        withContent("Hello from test"));

        assertEquals(3, invocationList.size());
        assertEquals("interceptor10-beforeAction", invocationList.get(0));
        assertEquals("param", invocationList.get(1));
        assertEquals("interceptor10-afterAction", invocationList.get(2));
    }

    @Test
    public void defaultActionWithArgs() throws Exception {

        assertThatRequestFor("/mvc/mvctest?c=default-args&p1=test")
            .producesPage()
                .withTag("h2", 
                        withContent("Hello from test"));
    }

    @Test
    public void defaultControllerWithAction() throws Exception {

        assertThatRequestFor("/mvc/mvctest?a=with-param&var=test")
            .producesPage()
                .withTag("h2", 
                        withContent("Hello from test"));
    }

    @Test
    public void classNameController() throws Exception {

        assertThatRequestFor("/mvc/mvctest?c=ClassNameController&a=sayHello&name=John")
            .producesPage()
                .withTag("h2", 
                        withContent("Hello from classNameControllerAction:John"));
    }

    @Test
    public void httpMethodGET() throws Exception {

        assertThatPOSTRequestFor("/mvc/mvctest", withParam("c", "httpmethod1"))
            .producesPage()
                .withTag("h2", withContent("Hello from default"));
    }

    @Test
    public void httpMethodGET2() throws Exception {

        assertThatGETRequestFor("/mvc/mvctest?c=httpmethod1")
            .producesPage()
                .withTag("h2", withContent("Hello from get"));
    }

    @Test
    public void httpMethodGET3() throws Exception {

        assertThatRequestFor("/mvc/mvctest?c=httpmethod1&a=sayHello").producesErrorPage();
    }

    @Test
    public void httpMethodPOST() throws Exception {

        assertThatGETRequestFor("/mvc/mvctest?c=httpmethod2")
            .producesPage()
                .withTag("h2", withContent("Hello from default"));
    }

    @Test
    public void httpMethodPOST2() throws Exception {

        assertThatPOSTRequestFor("/mvc/mvctest", withParam("c", "httpmethod2"))
            .producesPage()
                .withTag("h2", withContent("Hello from post"));
    }

    @Test
    public void httpMethodPOST3() throws Exception {

        assertThatRequestFor("/mvc/mvctest?c=httpmethod2&a=sayHello").producesErrorPage();
    }

    @Test
    public void httpMethodPUT() throws Exception {

        assertThatRequestFor("/mvc/mvctest?c=httpmethod3")
            .producesPage()
                .withTag("h2", withContent("Hello from default"));
    }

    @Test
    public void httpMethodPUT2() throws Exception {

        assertThatPUTRequestFor("/mvc/mvctest?c=httpmethod3")
            .producesPage()
                .withTag("h2", withContent("Hello from put"));
    }

    @Test
    public void httpMethodPUT3() throws Exception {

        assertThatRequestFor("/mvc/mvctest?c=httpmethod3&a=doPutAction").producesErrorPage();
    }

    @Test
    public void httpMethodHEAD() throws Exception {

        assertThatRequestFor("/mvc/mvctest?c=httpmethod4")
            .producesPage()
                .withTag("h2", withContent("Hello from default"));
    }

    @Test
    public void httpMethodHEAD2() throws Exception {
        
        assertThatHEADRequestFor("/mvc/mvctest?c=httpmethod4").succeeds();
    }

    @Test
    public void httpMethodHEAD3() throws Exception {

        assertThatRequestFor("/mvc/mvctest?c=httpmethod4&a=doHeadAction").producesErrorPage();
    }

    @Test
    public void httpMethodTRACE() throws Exception {

        assertThatRequestFor("/mvc/mvctest?c=httpmethod5")
            .producesPage()
                .withTag("h2", withContent("Hello from default"));
    }

    @Test
    public void httpMethodTRACE2() throws Exception {

        assertThatRequestFor("/mvc/mvctest?c=httpmethod5&a=doTraceAction").producesErrorPage();
    }

    @Test
    public void httpMethodDELETE() throws Exception {

        assertThatRequestFor("/mvc/mvctest?c=httpmethod6")
            .producesPage()
                .withTag("h2", withContent("Hello from default"));
    }

    @Test
    public void httpMethodDELETE2() throws Exception {

        assertThatDELETERequestFor("/mvc/mvctest?c=httpmethod6")
            .producesPage()
                .withTag("h2", withContent("Hello from delete"));
    }

    @Test
    public void httpMethodDELETE3() throws Exception {

        assertThatRequestFor("/mvc/mvctest?c=httpmethod6&a=doDeleteAction").producesErrorPage();
    }

    @Test
    public void httpMethodOPTIONS() throws Exception {

        assertThatRequestFor("/mvc/mvctest?c=httpmethod7")
            .producesPage()
                .withTag("h2", withContent("Hello from default"));
    }

    @Test
    public void httpMethodOPTIONS2() throws Exception {

        assertThatOPTIONSRequestFor("/mvc/mvctest?c=httpmethod7")
            .producesPage()
                .withTag("h2", withContent("Hello from options"));
    }

    @Test
    public void httpMethodOPTIONS3() throws Exception {

        assertThatRequestFor("/mvc/mvctest?c=httpmethod7&a=doOptionsAction").producesErrorPage();
    }

    @Test
    public void httpMethodMulti() throws Exception {

        assertThatGETRequestFor("/mvc/mvctest?c=httpmethod8")
            .producesPage()
                .withTag("h2", withContent("Hello from multi"));
    }

    @Test
    public void httpMethodMulti2() throws Exception {

        assertThatPOSTRequestFor("/mvc/mvctest", withParam("c", "httpmethod8"))
            .producesPage()
                .withTag("h2", withContent("Hello from multi"));
    }

    @Test
    public void httpMethodMulti3() throws Exception {

        assertThatGETRequestFor("/mvc/mvctest?c=httpmethod8&a=sayHello")
            .producesPage()
                .withTag("h2", withContent("Hello from sayHello"));
    }

    @Test
    public void httpMethodMulti4() throws Exception {
        
        assertThatPOSTRequestFor("/mvc/mvctest", 
                withParam("c", "httpmethod8"),
                withParam("a", "sayHello"))
            .producesPage()
                .withTag("h2", withContent("Hello from sayHello"));
    }

    @Test
    public void interceptedController13NoAction() throws Exception {

        assertThatRequestFor("/mvc/mvctest?c=intercepted13").producesErrorPage();
    }

    @Test
    public void interceptedController13POSTAction() throws Exception {

        List<String> invocationList = new ArrayList<String>();
        InterceptedController13.invocationList = invocationList;
        Interceptor1.invocationList = invocationList;
        Interceptor1b.invocationList = invocationList;

        assertThatPOSTRequestFor("/mvc/mvctest", 
                withParam("c", "intercepted13"))
            .producesPage()
                .withTag("h2", withContent("Hello from postAction"));

        assertEquals(5, invocationList.size());
        assertEquals("interceptor1-beforeAction:req:resp:sess:someService", invocationList.get(0));
        assertEquals("interceptor1b-beforeAction:req:resp:sess:someService", invocationList.get(1));
        assertEquals("postAction", invocationList.get(2));
        assertEquals("interceptor1b-afterAction:req:resp:sess:someService:ok", invocationList.get(3));
        assertEquals("interceptor1-afterAction:req:resp:sess:someService:ok", invocationList.get(4));
    }

    @Test
    public void interceptedController14NoAction() throws Exception {

        assertThatPOSTRequestFor("/mvc/mvctest", withParam("c", "intercepted14")).producesErrorPage();
    }

    @Test
    public void interceptedController14GETAction() throws Exception {

        List<String> invocationList = new ArrayList<String>();
        InterceptedController14.invocationList = invocationList;
        Interceptor1.invocationList = invocationList;
        Interceptor1b.invocationList = invocationList;

        assertThatRequestFor("/mvc/mvctest?c=intercepted14")
            .producesPage()
                .withTag("h2", withContent("Hello from getAction"));

        assertEquals(5, invocationList.size());
        assertEquals("interceptor1-beforeAction:req:resp:sess:someService", invocationList.get(0));
        assertEquals("interceptor1b-beforeAction:req:resp:sess:someService", invocationList.get(1));
        assertEquals("getAction", invocationList.get(2));
        assertEquals("interceptor1b-afterAction:req:resp:sess:someService:ok", invocationList.get(3));
        assertEquals("interceptor1-afterAction:req:resp:sess:someService:ok", invocationList.get(4));
    }

    @Test
    public void interceptedController15NoAction() throws Exception {

        assertThatPOSTRequestFor("/mvc/mvctest", withParam("c", "intercepted15")).producesErrorPage();
    }

    @Test
    public void interceptedController15PUTAction() throws Exception {

        List<String> invocationList = new ArrayList<String>();
        InterceptedController15.invocationList = invocationList;
        Interceptor1.invocationList = invocationList;
        Interceptor1b.invocationList = invocationList;

        assertThatPUTRequestFor("/mvc/mvctest?c=intercepted15")
            .producesPage()
                .withTag("h2", withContent("Hello from putAction"));

        assertEquals(5, invocationList.size());
        assertEquals("interceptor1-beforeAction:req:resp:sess:someService", invocationList.get(0));
        assertEquals("interceptor1b-beforeAction:req:resp:sess:someService", invocationList.get(1));
        assertEquals("putAction", invocationList.get(2));
        assertEquals("interceptor1b-afterAction:req:resp:sess:someService:ok", invocationList.get(3));
        assertEquals("interceptor1-afterAction:req:resp:sess:someService:ok", invocationList.get(4));
    }

    @Test
    public void interceptedController16NoAction() throws Exception {

        assertThatPOSTRequestFor("/mvc/mvctest", withParam("c", "intercepted16")).producesErrorPage();
    }

    @Test
    public void interceptedController17NoAction() throws Exception {

        assertThatPOSTRequestFor("/mvc/mvctest", withParam("c", "intercepted17")).producesErrorPage();
    }

    @Test
    public void interceptedController17HEADAction() throws Exception {

        List<String> invocationList = new ArrayList<String>();
        InterceptedController17.invocationList = invocationList;
        Interceptor1.invocationList = invocationList;
        Interceptor1b.invocationList = invocationList;

        assertThatHEADRequestFor("/mvc/mvctest?c=intercepted17").succeeds();

        assertEquals(5, invocationList.size());
        assertEquals("interceptor1-beforeAction:req:resp:sess:someService", invocationList.get(0));
        assertEquals("interceptor1b-beforeAction:req:resp:sess:someService", invocationList.get(1));
        assertEquals("headAction", invocationList.get(2));
        assertEquals("interceptor1b-afterAction:req:resp:sess:someService:ok", invocationList.get(3));
        assertEquals("interceptor1-afterAction:req:resp:sess:someService:ok", invocationList.get(4));
    }

    @Test
    public void interceptedController18NoAction() throws Exception {

        assertThatPOSTRequestFor("/mvc/mvctest", withParam("c", "intercepted18")).producesErrorPage();
    }

    @Test
    public void interceptedController18OPTIONSAction() throws Exception {

        List<String> invocationList = new ArrayList<String>();
        InterceptedController18.invocationList = invocationList;
        Interceptor1.invocationList = invocationList;
        Interceptor1b.invocationList = invocationList;

        assertThatOPTIONSRequestFor("/mvc/mvctest?c=intercepted18")
            .producesPage()
                .withTag("h2", withContent("Hello from optionsAction"));

        assertEquals(5, invocationList.size());
        assertEquals("interceptor1-beforeAction:req:resp:sess:someService", invocationList.get(0));
        assertEquals("interceptor1b-beforeAction:req:resp:sess:someService", invocationList.get(1));
        assertEquals("optionsAction", invocationList.get(2));
        assertEquals("interceptor1b-afterAction:req:resp:sess:someService:ok", invocationList.get(3));
        assertEquals("interceptor1-afterAction:req:resp:sess:someService:ok", invocationList.get(4));
    }

    @Test
    public void interceptedController19NoAction() throws Exception {

        assertThatPOSTRequestFor("/mvc/mvctest", withParam("c", "intercepted19")).producesErrorPage();
    }

    @Test
    public void interceptedController19DELETEAction() throws Exception {

        List<String> invocationList = new ArrayList<String>();
        InterceptedController19.invocationList = invocationList;
        Interceptor1.invocationList = invocationList;
        Interceptor1b.invocationList = invocationList;

        assertThatDELETERequestFor("/mvc/mvctest?c=intercepted19")
            .producesPage()
                .withTag("h2", withContent("Hello from deleteAction"));

        assertEquals(5, invocationList.size());
        assertEquals("interceptor1-beforeAction:req:resp:sess:someService", invocationList.get(0));
        assertEquals("interceptor1b-beforeAction:req:resp:sess:someService", invocationList.get(1));
        assertEquals("deleteAction", invocationList.get(2));
        assertEquals("interceptor1b-afterAction:req:resp:sess:someService:ok", invocationList.get(3));
        assertEquals("interceptor1-afterAction:req:resp:sess:someService:ok", invocationList.get(4));
    }
}