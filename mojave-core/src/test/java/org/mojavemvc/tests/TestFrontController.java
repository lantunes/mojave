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

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
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
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.DefaultHandler;
import org.mortbay.jetty.handler.HandlerCollection;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.webapp.WebAppContext;

import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.util.NameValuePair;

/**
 * 
 * @author Luis Antunes
 */
public class TestFrontController {
    private static final int port = 8989;

    private static Server jetty;

    private WebClient client;

    @BeforeClass
    public static void beforeTests() throws Exception {

        jetty = new Server();
        Connector connector = new SelectChannelConnector();
        connector.setPort(port);
        jetty.setConnectors(new Connector[] { connector });

        WebAppContext wactx = new WebAppContext();
        wactx.setClassLoader(TestFrontController.class.getClassLoader());
        wactx.setParentLoaderPriority(true);
        wactx.setContextPath("/mvc");
        wactx.setWar("src/test/resources/standard");

        HandlerCollection handlers = new HandlerCollection();
        handlers.setHandlers(new Handler[] { wactx, new DefaultHandler() });
        jetty.setHandler(handlers);

        jetty.start();
    }

    @Before
    public void beforeEachTest() {

        client = new WebClient();
    }

    @AfterClass
    public static void afterTests() throws Exception {

        if (jetty != null) {
            jetty.stop();
            jetty.destroy();
            jetty = null;
        }
    }

    @Test
    public void indexControllerDefaultAction_WithError() throws Exception {

        /*
         * there is no action called 'default' in the controller; this should
         * throw an IllegalArgumentException internally
         */
        HtmlPage page = (HtmlPage) client.getPage("http://localhost:" + port + "/mvc/mvctest?c=index&a=default");
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h1");
        assertEquals(1, elements.size());
        HtmlElement h1 = elements.get(0);
        assertEquals("Error", h1.getTextContent());
    }

    @Test
    public void indexControllerDefaultAction2() throws Exception {

        HtmlPage page = (HtmlPage) client.getPage("http://localhost:" + port + "/mvc/mvctest?c=index");
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h2");
        assertEquals(1, elements.size());
        HtmlElement h2 = elements.get(0);
        assertEquals("Hello from index ", h2.getTextContent());
    }

    @Test
    public void indexControllerDefaultAction3() throws Exception {

        HtmlPage page = (HtmlPage) client.getPage("http://localhost:" + port + "/mvc/mvctest");
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h2");
        assertEquals(1, elements.size());
        HtmlElement h2 = elements.get(0);
        assertEquals("Hello from  ", h2.getTextContent());
    }

    @Test
    public void indexControllerTestAction() throws Exception {

        HtmlPage page = (HtmlPage) client.getPage("http://localhost:" + port + "/mvc/mvctest?c=index&a=test");
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h1");
        assertEquals(1, elements.size());
        HtmlElement h1 = elements.get(0);
        assertEquals("This is the test.jsp file of the IndexController testAction!", h1.getTextContent());
    }

    @Test
    public void indexControllerWithParamAction() throws Exception {

        HtmlPage page = (HtmlPage) client.getPage("http://localhost:" + port
                + "/mvc/mvctest?c=index&a=with-param&var=hello");
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h1");
        assertEquals(1, elements.size());
        HtmlElement h1 = elements.get(0);
        assertEquals("This is the param.jsp file of the IndexController withParamAction!", h1.getTextContent());
        elements = page.getElementsByTagName("h2");
        assertEquals(1, elements.size());
        HtmlElement h2 = elements.get(0);
        assertEquals("Hello from hello", h2.getTextContent());
    }

    @Test
    public void indexControllerAnotherParamAction() throws Exception {

        HtmlPage page = (HtmlPage) client.getPage("http://localhost:" + port
                + "/mvc/mvctest?c=index&a=another-param&var=hello");
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h1");
        assertEquals(1, elements.size());
        HtmlElement h1 = elements.get(0);
        assertEquals("This is the param.jsp file of the IndexController withParamAction!", h1.getTextContent());
        elements = page.getElementsByTagName("h2");
        assertEquals(1, elements.size());
        HtmlElement h2 = elements.get(0);
        assertEquals("Hello from hello", h2.getTextContent());
    }

    @Test
    public void indexControllerSomeServiceAction() throws Exception {

        HtmlPage page = (HtmlPage) client.getPage("http://localhost:" + port
                + "/mvc/mvctest?c=index&a=some-service&var=hello");
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h1");
        assertEquals(1, elements.size());
        HtmlElement h1 = elements.get(0);
        assertEquals("This is the some-service.jsp file of the IndexController someServiceAction!", h1.getTextContent());
        elements = page.getElementsByTagName("h2");
        assertEquals(1, elements.size());
        HtmlElement h2 = elements.get(0);
        assertEquals("answered hello", h2.getTextContent());
    }

    @Test
    public void indexControllerActionAnnotation() throws Exception {

        HtmlPage page = (HtmlPage) client.getPage("http://localhost:" + port
                + "/mvc/mvctest?c=index&a=test-annotation&var=annotationTest");
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h1");
        assertEquals(1, elements.size());
        HtmlElement h1 = elements.get(0);
        assertEquals("This is the param.jsp file of the IndexController withParamAction!", h1.getTextContent());
        elements = page.getElementsByTagName("h2");
        assertEquals(1, elements.size());
        HtmlElement h2 = elements.get(0);
        assertEquals("Hello from annotationTest", h2.getTextContent());
    }

    @Test
    public void someControllerClassControllerAnnotation() throws Exception {

        HtmlPage page = (HtmlPage) client.getPage("http://localhost:" + port
                + "/mvc/mvctest?c=annot&a=some-action&var=contollerAnnotationTest");
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h1");
        assertEquals(1, elements.size());
        HtmlElement h1 = elements.get(0);
        assertEquals("This is the some-controller.jsp file of the SomeControllerClass doSomething action!",
                h1.getTextContent());
        elements = page.getElementsByTagName("h2");
        assertEquals(1, elements.size());
        HtmlElement h2 = elements.get(0);
        assertEquals("contollerAnnotationTest", h2.getTextContent());
    }

    @Test
    public void indexControllerParamAnnotationTest1() throws Exception {

        HtmlPage page = (HtmlPage) client.getPage("http://localhost:" + port
                + "/mvc/mvctest?c=index&a=param-annotation-string&p1=param1");
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h1");
        assertEquals(1, elements.size());
        HtmlElement h1 = elements.get(0);
        assertEquals("This is the params.jsp file of the IndexController @Param test action!", h1.getTextContent());
        elements = page.getElementsByTagName("h2");
        assertEquals(1, elements.size());
        HtmlElement h2 = elements.get(0);
        assertEquals("Hello from param1", h2.getTextContent());
    }

    @Test
    public void indexControllerParamAnnotationTest1_WithNull() throws Exception {

        HtmlPage page = (HtmlPage) client.getPage("http://localhost:" + port
                + "/mvc/mvctest?c=index&a=param-annotation-string");
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h1");
        assertEquals(1, elements.size());
        HtmlElement h1 = elements.get(0);
        assertEquals("This is the params.jsp file of the IndexController @Param test action!", h1.getTextContent());
        elements = page.getElementsByTagName("h2");
        assertEquals(1, elements.size());
        HtmlElement h2 = elements.get(0);
        assertEquals("Hello from null", h2.getTextContent());
    }

    @Test
    public void indexControllerParamAnnotationTest2() throws Exception {

        HtmlPage page = (HtmlPage) client.getPage("http://localhost:" + port
                + "/mvc/mvctest?c=index&a=param-annotation-string2&" + "p1=param1&p2=param2");
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h1");
        assertEquals(1, elements.size());
        HtmlElement h1 = elements.get(0);
        assertEquals("This is the params2.jsp file of the IndexController @Param test action!", h1.getTextContent());
        elements = page.getElementsByTagName("h2");
        assertEquals(1, elements.size());
        HtmlElement h2 = elements.get(0);
        assertEquals("Hello from param1, param2", h2.getTextContent());
    }

    @Test
    public void indexControllerParamAnnotationTest2_WithNull() throws Exception {

        HtmlPage page = (HtmlPage) client.getPage("http://localhost:" + port
                + "/mvc/mvctest?c=index&a=param-annotation-string2&p2=param2");
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h1");
        assertEquals(1, elements.size());
        HtmlElement h1 = elements.get(0);
        assertEquals("This is the params2.jsp file of the IndexController @Param test action!", h1.getTextContent());
        elements = page.getElementsByTagName("h2");
        assertEquals(1, elements.size());
        HtmlElement h2 = elements.get(0);
        assertEquals("Hello from null, param2", h2.getTextContent());
    }

    @Test
    public void indexControllerParamAnnotationTest3() throws Exception {

        HtmlPage page = (HtmlPage) client.getPage("http://localhost:" + port
                + "/mvc/mvctest?c=index&a=param-annotation-int&p1=123456");
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h1");
        assertEquals(1, elements.size());
        HtmlElement h1 = elements.get(0);
        assertEquals("This is the params.jsp file of the IndexController @Param test action!", h1.getTextContent());
        elements = page.getElementsByTagName("h2");
        assertEquals(1, elements.size());
        HtmlElement h2 = elements.get(0);
        assertEquals("Hello from 123456", h2.getTextContent());
    }

    @Test
    public void indexControllerParamAnnotationTest3_WithNull() throws Exception {

        HtmlPage page = (HtmlPage) client.getPage("http://localhost:" + port
                + "/mvc/mvctest?c=index&a=param-annotation-int");
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h1");
        assertEquals(1, elements.size());
        HtmlElement h1 = elements.get(0);
        assertEquals("This is the params.jsp file of the IndexController @Param test action!", h1.getTextContent());
        elements = page.getElementsByTagName("h2");
        assertEquals(1, elements.size());
        HtmlElement h2 = elements.get(0);
        assertEquals("Hello from 0", h2.getTextContent());
    }

    @Test
    public void indexControllerParamAnnotationTest3_WithError() throws Exception {

        HtmlPage page = (HtmlPage) client.getPage("http://localhost:" + port
                + "/mvc/mvctest?c=index&a=param-annotation-int&p1=hello");
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h1");
        assertEquals(1, elements.size());
        HtmlElement h1 = elements.get(0);
        assertEquals("Error", h1.getTextContent());
    }

    @Test
    public void indexControllerParamAnnotationTest4() throws Exception {

        HtmlPage page = (HtmlPage) client.getPage("http://localhost:" + port
                + "/mvc/mvctest?c=index&a=param-annotation-double&p1=123.456");
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h1");
        assertEquals(1, elements.size());
        HtmlElement h1 = elements.get(0);
        assertEquals("This is the params.jsp file of the IndexController @Param test action!", h1.getTextContent());
        elements = page.getElementsByTagName("h2");
        assertEquals(1, elements.size());
        HtmlElement h2 = elements.get(0);
        assertEquals("Hello from 123.456", h2.getTextContent());
    }

    @Test
    public void indexControllerParamAnnotationTest4_WithNull() throws Exception {

        HtmlPage page = (HtmlPage) client.getPage("http://localhost:" + port
                + "/mvc/mvctest?c=index&a=param-annotation-double");
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h1");
        assertEquals(1, elements.size());
        HtmlElement h1 = elements.get(0);
        assertEquals("This is the params.jsp file of the IndexController @Param test action!", h1.getTextContent());
        elements = page.getElementsByTagName("h2");
        assertEquals(1, elements.size());
        HtmlElement h2 = elements.get(0);
        assertEquals("Hello from 0.0", h2.getTextContent());
    }

    @Test
    public void indexControllerParamAnnotationTest4_WithError() throws Exception {

        HtmlPage page = (HtmlPage) client.getPage("http://localhost:" + port
                + "/mvc/mvctest?c=index&a=param-annotation-double&p1=hello");
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h1");
        assertEquals(1, elements.size());
        HtmlElement h1 = elements.get(0);
        assertEquals("Error", h1.getTextContent());
    }

    @Test
    public void indexControllerParamAnnotationTest5() throws Exception {

        HtmlPage page = (HtmlPage) client.getPage("http://localhost:" + port
                + "/mvc/mvctest?c=index&a=param-annotation-date&p1=2011-03-01");
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h1");
        assertEquals(1, elements.size());
        HtmlElement h1 = elements.get(0);
        assertEquals("This is the params.jsp file of the IndexController @Param test action!", h1.getTextContent());
        elements = page.getElementsByTagName("h2");
        assertEquals(1, elements.size());
        HtmlElement h2 = elements.get(0);
        assertEquals("Hello from 2011-03-01", h2.getTextContent());
    }

    @Test
    public void indexControllerParamAnnotationTest5_WithNull() throws Exception {

        HtmlPage page = (HtmlPage) client.getPage("http://localhost:" + port
                + "/mvc/mvctest?c=index&a=param-annotation-date");
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h1");
        assertEquals(1, elements.size());
        HtmlElement h1 = elements.get(0);
        assertEquals("This is the params.jsp file of the IndexController @Param test action!", h1.getTextContent());
        elements = page.getElementsByTagName("h2");
        assertEquals(1, elements.size());
        HtmlElement h2 = elements.get(0);
        assertEquals("Hello from null", h2.getTextContent());
    }

    @Test
    public void indexControllerParamAnnotationTest5_WithError() throws Exception {

        HtmlPage page = (HtmlPage) client.getPage("http://localhost:" + port
                + "/mvc/mvctest?c=index&a=param-annotation-date&p1=hello");
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h1");
        assertEquals(1, elements.size());
        HtmlElement h1 = elements.get(0);
        assertEquals("Error", h1.getTextContent());
    }

    @Test
    public void indexControllerParamAnnotationTestAll() throws Exception {

        HtmlPage page = (HtmlPage) client.getPage("http://localhost:" + port
                + "/mvc/mvctest?c=index&a=param-annotation-all&" + "p1=2011-03-01&p2=hello&p3=123&p4=1.45");
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h1");
        assertEquals(1, elements.size());
        HtmlElement h1 = elements.get(0);
        assertEquals("This is the params3.jsp file of the IndexController @Param test action!", h1.getTextContent());
        elements = page.getElementsByTagName("h2");
        assertEquals(1, elements.size());
        HtmlElement h2 = elements.get(0);
        assertEquals("Hello from 2011-03-01, hello, 123, 1.45", h2.getTextContent());
    }

    @Test
    public void indexControllerParamAnnotationTest6() throws Exception {

        HtmlPage page = (HtmlPage) client.getPage("http://localhost:" + port
                + "/mvc/mvctest?c=index&a=param-annotation-bool&p1=true");
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h1");
        assertEquals(1, elements.size());
        HtmlElement h1 = elements.get(0);
        assertEquals("This is the params.jsp file of the IndexController @Param test action!", h1.getTextContent());
        elements = page.getElementsByTagName("h2");
        assertEquals(1, elements.size());
        HtmlElement h2 = elements.get(0);
        assertEquals("Hello from true", h2.getTextContent());
    }

    @Test
    public void indexControllerParamAnnotationTest6b() throws Exception {

        HtmlPage page = (HtmlPage) client.getPage("http://localhost:" + port
                + "/mvc/mvctest?c=index&a=param-annotation-bool&p1=false");
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h1");
        assertEquals(1, elements.size());
        HtmlElement h1 = elements.get(0);
        assertEquals("This is the params.jsp file of the IndexController @Param test action!", h1.getTextContent());
        elements = page.getElementsByTagName("h2");
        assertEquals(1, elements.size());
        HtmlElement h2 = elements.get(0);
        assertEquals("Hello from false", h2.getTextContent());
    }

    @Test
    public void indexControllerParamAnnotationTest6c() throws Exception {

        HtmlPage page = (HtmlPage) client.getPage("http://localhost:" + port
                + "/mvc/mvctest?c=index&a=param-annotation-bool&p1=t");
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h1");
        assertEquals(1, elements.size());
        HtmlElement h1 = elements.get(0);
        assertEquals("This is the params.jsp file of the IndexController @Param test action!", h1.getTextContent());
        elements = page.getElementsByTagName("h2");
        assertEquals(1, elements.size());
        HtmlElement h2 = elements.get(0);
        assertEquals("Hello from true", h2.getTextContent());
    }

    @Test
    public void indexControllerParamAnnotationTest6d() throws Exception {

        HtmlPage page = (HtmlPage) client.getPage("http://localhost:" + port
                + "/mvc/mvctest?c=index&a=param-annotation-bool&p1=f");
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h1");
        assertEquals(1, elements.size());
        HtmlElement h1 = elements.get(0);
        assertEquals("This is the params.jsp file of the IndexController @Param test action!", h1.getTextContent());
        elements = page.getElementsByTagName("h2");
        assertEquals(1, elements.size());
        HtmlElement h2 = elements.get(0);
        assertEquals("Hello from false", h2.getTextContent());
    }

    @Test
    public void indexControllerParamAnnotationTest6e() throws Exception {

        HtmlPage page = (HtmlPage) client.getPage("http://localhost:" + port
                + "/mvc/mvctest?c=index&a=param-annotation-bool&p1=1");
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h1");
        assertEquals(1, elements.size());
        HtmlElement h1 = elements.get(0);
        assertEquals("This is the params.jsp file of the IndexController @Param test action!", h1.getTextContent());
        elements = page.getElementsByTagName("h2");
        assertEquals(1, elements.size());
        HtmlElement h2 = elements.get(0);
        assertEquals("Hello from true", h2.getTextContent());
    }

    @Test
    public void indexControllerParamAnnotationTest6f() throws Exception {

        HtmlPage page = (HtmlPage) client.getPage("http://localhost:" + port
                + "/mvc/mvctest?c=index&a=param-annotation-bool&p1=0");
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h1");
        assertEquals(1, elements.size());
        HtmlElement h1 = elements.get(0);
        assertEquals("This is the params.jsp file of the IndexController @Param test action!", h1.getTextContent());
        elements = page.getElementsByTagName("h2");
        assertEquals(1, elements.size());
        HtmlElement h2 = elements.get(0);
        assertEquals("Hello from false", h2.getTextContent());
    }

    @Test
    public void indexControllerParamAnnotationTest6_WithError() throws Exception {

        HtmlPage page = (HtmlPage) client.getPage("http://localhost:" + port
                + "/mvc/mvctest?c=index&a=param-annotation-bool&p1=hello");
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h1");
        assertEquals(1, elements.size());
        HtmlElement h1 = elements.get(0);
        assertEquals("Error", h1.getTextContent());
    }

    @Test
    public void indexControllerParamAnnotationTest6_WithError2() throws Exception {

        HtmlPage page = (HtmlPage) client.getPage("http://localhost:" + port
                + "/mvc/mvctest?c=index&a=param-annotation-bool");
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h1");
        assertEquals(1, elements.size());
        HtmlElement h1 = elements.get(0);
        assertEquals("This is the params.jsp file of the IndexController @Param test action!", h1.getTextContent());
        elements = page.getElementsByTagName("h2");
        assertEquals(1, elements.size());
        HtmlElement h2 = elements.get(0);
        assertEquals("Hello from false", h2.getTextContent());
    }

    @Test
    public void indexControllerParamAnnotationTestIntCollection() throws Exception {

        HtmlPage page = (HtmlPage) client.getPage("http://localhost:" + port
                + "/mvc/mvctest?c=index&a=param-annotation-ints&" + "p1=123&p1=456&p1=789&p1=321");
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h1");
        assertEquals(1, elements.size());
        HtmlElement h1 = elements.get(0);
        assertEquals("This is the params3.jsp file of the IndexController @Param test action!", h1.getTextContent());
        elements = page.getElementsByTagName("h2");
        assertEquals(1, elements.size());
        HtmlElement h2 = elements.get(0);
        assertEquals("Hello from 123, 456, 789, 321", h2.getTextContent());
    }

    @Test
    public void indexControllerParamAnnotationTestStringCollection() throws Exception {

        HtmlPage page = (HtmlPage) client.getPage("http://localhost:" + port
                + "/mvc/mvctest?c=index&a=param-annotation-strings&" + "p1=abc&p1=def&p1=ghi&p1=jkl");
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h1");
        assertEquals(1, elements.size());
        HtmlElement h1 = elements.get(0);
        assertEquals("This is the params3.jsp file of the IndexController @Param test action!", h1.getTextContent());
        elements = page.getElementsByTagName("h2");
        assertEquals(1, elements.size());
        HtmlElement h2 = elements.get(0);
        assertEquals("Hello from abc, def, ghi, jkl", h2.getTextContent());
    }

    @Test
    public void indexControllerParamAnnotationTestDoubleCollection() throws Exception {

        HtmlPage page = (HtmlPage) client.getPage("http://localhost:" + port
                + "/mvc/mvctest?c=index&a=param-annotation-doubles&" + "p1=1.1&p1=2.2&p1=3.3&p1=4.4");
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h1");
        assertEquals(1, elements.size());
        HtmlElement h1 = elements.get(0);
        assertEquals("This is the params3.jsp file of the IndexController @Param test action!", h1.getTextContent());
        elements = page.getElementsByTagName("h2");
        assertEquals(1, elements.size());
        HtmlElement h2 = elements.get(0);
        assertEquals("Hello from 1.1, 2.2, 3.3, 4.4", h2.getTextContent());
    }

    @Test
    public void indexControllerParamAnnotationTestDateCollection() throws Exception {

        HtmlPage page = (HtmlPage) client.getPage("http://localhost:" + port
                + "/mvc/mvctest?c=index&a=param-annotation-dates&"
                + "p1=2011-03-01&p1=2010-02-09&p1=2009-11-23&p1=2008-05-03");
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h1");
        assertEquals(1, elements.size());
        HtmlElement h1 = elements.get(0);
        assertEquals("This is the params3.jsp file of the IndexController @Param test action!", h1.getTextContent());
        elements = page.getElementsByTagName("h2");
        assertEquals(1, elements.size());
        HtmlElement h2 = elements.get(0);
        assertEquals("Hello from 2011-03-01, 2010-02-09, 2009-11-23, 2008-05-03", h2.getTextContent());
    }

    @Test
    public void indexControllerParamAnnotationTestBooleanCollection() throws Exception {

        HtmlPage page = (HtmlPage) client.getPage("http://localhost:" + port
                + "/mvc/mvctest?c=index&a=param-annotation-bools&" + "p1=t&p1=false&p1=f&p1=1");
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h1");
        assertEquals(1, elements.size());
        HtmlElement h1 = elements.get(0);
        assertEquals("This is the params3.jsp file of the IndexController @Param test action!", h1.getTextContent());
        elements = page.getElementsByTagName("h2");
        assertEquals(1, elements.size());
        HtmlElement h2 = elements.get(0);
        assertEquals("Hello from true, false, false, true", h2.getTextContent());
    }

    @Test
    public void indexControllerInjectedAction() throws Exception {

        HtmlPage page = (HtmlPage) client.getPage("http://localhost:" + port + "/mvc/mvctest?c=index&a=injected");
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h1");
        assertEquals(1, elements.size());
        HtmlElement h1 = elements.get(0);
        assertEquals("This is the param.jsp file of the IndexController withParamAction!", h1.getTextContent());
        elements = page.getElementsByTagName("h2");
        assertEquals(1, elements.size());
        HtmlElement h2 = elements.get(0);
        assertEquals("Hello from injected-index", h2.getTextContent());
    }

    @Test
    public void indexControllerTestInitAction() throws Exception {

        HtmlPage page = (HtmlPage) client.getPage("http://localhost:" + port + "/mvc/mvctest?c=index&a=test-init");
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h1");
        assertEquals(1, elements.size());
        HtmlElement h1 = elements.get(0);
        assertEquals("This is the param.jsp file of the IndexController withParamAction!", h1.getTextContent());
        elements = page.getElementsByTagName("h2");
        assertEquals(1, elements.size());
        HtmlElement h2 = elements.get(0);
        assertEquals("Hello from init-called", h2.getTextContent());
    }

    @Test
    public void formController() throws Exception {

        HtmlPage page = (HtmlPage) client.getPage("http://localhost:" + port + "/mvc/mvctest?c=form-controller");

        HtmlForm form = page.getFormByName("test-form");
        HtmlInput userNameInput = form.getInputByName("userName");
        userNameInput.setValueAttribute("john");
        HtmlInput passwordInput = form.getInputByName("password");
        passwordInput.setValueAttribute("doe");
        HtmlSubmitInput submit = form.getInputByValue("submit");
        page = submit.click();

        HtmlElement userNameElement = page.getElementById("userName");
        assertEquals("john", userNameElement.getTextContent());
        HtmlElement passwordElement = page.getElementById("password");
        assertEquals("doe", passwordElement.getTextContent());
    }

    @Test
    public void formControllerWithRegularParam() throws Exception {

        HtmlPage page = (HtmlPage) client
                .getPage("http://localhost:" + port + "/mvc/mvctest?c=form-controller&a=form2");

        HtmlForm form = page.getFormByName("test-form");
        HtmlInput userNameInput = form.getInputByName("userName");
        userNameInput.setValueAttribute("john");
        HtmlInput passwordInput = form.getInputByName("password");
        passwordInput.setValueAttribute("doe");
        HtmlSubmitInput submit = form.getInputByValue("submit");
        page = submit.click();

        HtmlElement userNameElement = page.getElementById("userName");
        assertEquals("john", userNameElement.getTextContent());
        HtmlElement passwordElement = page.getElementById("password");
        assertEquals("doe", passwordElement.getTextContent());
        HtmlElement p1Element = page.getElementById("p1");
        assertEquals("hello", p1Element.getTextContent());
    }

    @Test
    public void formControllerSubmittable() throws Exception {

        HtmlPage page = (HtmlPage) client
                .getPage("http://localhost:" + port + "/mvc/mvctest?c=form-controller&a=form3");

        HtmlForm form = page.getFormByName("test-form");
        HtmlInput userNameInput = form.getInputByName("userName");
        userNameInput.setValueAttribute("john");
        HtmlInput passwordInput = form.getInputByName("password");
        passwordInput.setValueAttribute("doe");
        HtmlSubmitInput submit = form.getInputByValue("submit");
        page = submit.click();

        HtmlElement userNameElement = page.getElementById("userName");
        assertEquals("JOHN", userNameElement.getTextContent());
        HtmlElement passwordElement = page.getElementById("password");
        assertEquals("DOE", passwordElement.getTextContent());
    }

    @Test
    public void formControllerPopulated() throws Exception {

        HtmlPage page = (HtmlPage) client.getPage("http://localhost:" + port
                + "/mvc/mvctest?c=form-controller&a=populated");

        HtmlElement userNameElement = page.getElementById("userName");
        assertEquals("uname", userNameElement.getAttribute("value"));
        HtmlElement passwordElement = page.getElementById("password");
        assertEquals("pswd", passwordElement.getAttribute("value"));
    }

    @Test
    public void formControllerWithBooleanTrue() throws Exception {

        HtmlPage page = (HtmlPage) client
                .getPage("http://localhost:" + port + "/mvc/mvctest?c=form-controller&a=form4");

        HtmlForm form = page.getFormByName("test-form");
        HtmlCheckBoxInput checkbox = form.getInputByName("someFlag");
        checkbox.setChecked(true);
        HtmlSubmitInput submit = form.getInputByValue("submit");
        page = submit.click();

        HtmlElement flagElement = page.getElementById("flag");
        assertEquals("true", flagElement.getTextContent());
    }

    @Test
    public void formControllerWithBooleanFalse() throws Exception {

        HtmlPage page = (HtmlPage) client
                .getPage("http://localhost:" + port + "/mvc/mvctest?c=form-controller&a=form4");

        HtmlForm form = page.getFormByName("test-form");
        HtmlSubmitInput submit = form.getInputByValue("submit");
        page = submit.click();

        HtmlElement flagElement = page.getElementById("flag");
        assertEquals("false", flagElement.getTextContent());
    }

    @Test
    public void redirectingController() throws Exception {

        HtmlPage page = (HtmlPage) client.getPage("http://localhost:" + port + "/mvc/mvctest?c=redirecting&a=redirect");
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h1");
        assertEquals(1, elements.size());
        HtmlElement h1 = elements.get(0);
        assertEquals("redirected!", h1.getTextContent());
    }

    @Test
    public void redirectingController2() throws Exception {

        HtmlPage page = (HtmlPage) client.getPage("http://localhost:" + port
                + "/mvc/mvctest?c=redirecting2&a=doSomething");
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h1");
        assertEquals(1, elements.size());
        HtmlElement h1 = elements.get(0);
        assertEquals("redirected!", h1.getTextContent());
    }

    @Test
    public void streamingControllerXML() throws Exception {

        Page page = client.getPage("http://localhost:" + port + "/mvc/mvctest?c=stream&a=xml");
        assertEquals("application/xml", page.getWebResponse().getContentType());
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?><Test><hello/></Test>", page.getWebResponse()
                .getContentAsString());
    }

    @Test
    public void streamingControllerJSON() throws Exception {

        Page page = client.getPage("http://localhost:" + port + "/mvc/mvctest?c=stream&a=json");
        assertEquals("application/json", page.getWebResponse().getContentType());
        assertEquals("{\"Test\":{\"hello\": 1}}", page.getWebResponse().getContentAsString());
    }

    @Test
    public void streamingController2() throws Exception {

        Page page = client.getPage("http://localhost:" + port + "/mvc/mvctest?c=stream2&a=doSomething");
        assertEquals("application/xml", page.getWebResponse().getContentType());
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?><Test><hello/></Test>", page.getWebResponse()
                .getContentAsString());
    }

    @Test
    public void streamingController3() throws Exception {

        Page page = client.getPage("http://localhost:" + port + "/mvc/mvctest?c=stream3&a=doSomething");
        assertEquals("application/xml", page.getWebResponse().getContentType());
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?><Test><hello/></Test>", page.getWebResponse()
                .getContentAsString());
    }

    @Test
    public void dispatchingController() throws Exception {

        HtmlPage page = (HtmlPage) client.getPage("http://localhost:" + port
                + "/mvc/mvctest?c=dispatching&a=doSomething");
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h2");
        assertEquals(1, elements.size());
        HtmlElement h2 = elements.get(0);
        assertEquals("Hello from dispatched", h2.getTextContent());
    }

    @Test
    public void someStatefulControllerDefaultAction() throws Exception {

        HtmlPage page = (HtmlPage) client.getPage("http://localhost:" + port + "/mvc/mvctest?c=some-stateful");
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h1");
        assertEquals(1, elements.size());
        HtmlElement h1 = elements.get(0);
        assertEquals("This is the index.jsp file of the IndexController defaultAction!", h1.getTextContent());
    }

    @Test
    public void someStatefulControllerSomeAction() throws Exception {

        HtmlPage page = (HtmlPage) client.getPage("http://localhost:" + port
                + "/mvc/mvctest?c=some-stateful&a=some-action");
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h1");
        assertEquals(1, elements.size());
        HtmlElement h1 = elements.get(0);
        assertEquals("This is the stateful.jsp file of the SomeStatefulController someAction!", h1.getTextContent());
    }

    @Test
    public void someStatefulControllerVarAction() throws Exception {

        HtmlPage page = (HtmlPage) client.getPage("http://localhost:" + port
                + "/mvc/mvctest?c=some-stateful&a=set-var&var=statetest");
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h1");
        assertEquals(1, elements.size());
        HtmlElement h1 = elements.get(0);
        assertEquals("This is the stateful.jsp file of the SomeStatefulController someAction!", h1.getTextContent());

        /* make a second request */
        page = client.getPage("http://localhost:" + port + "/mvc/mvctest?c=some-stateful&a=get-var");
        elements = page.getElementsByTagName("h2");
        assertEquals(1, elements.size());
        HtmlElement h2 = elements.get(0);
        assertEquals("Hello from statetest", h2.getTextContent());
    }

    @Test
    public void someStatefulControllerVarAction2() throws Exception {

        HtmlPage page = (HtmlPage) client.getPage("http://localhost:" + port
                + "/mvc/mvctest?c=some-stateful&a=set-var&var=statetest");
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h1");
        assertEquals(1, elements.size());
        HtmlElement h1 = elements.get(0);
        assertEquals("This is the stateful.jsp file of the SomeStatefulController someAction!", h1.getTextContent());

        /* make a second request with a new client, creating a new session */
        client = new WebClient();
        page = client.getPage("http://localhost:" + port + "/mvc/mvctest?c=some-stateful&a=get-var");
        elements = page.getElementsByTagName("h2");
        assertEquals(1, elements.size());
        HtmlElement h2 = elements.get(0);
        assertEquals("Hello from null", h2.getTextContent()); /* no state stored */
    }

    @Test
    public void someStatefulControllerReqAction() throws Exception {

        /*
         * Test that the injected HttpServletRequests are different, by
         * examining the hex hashcode that is part of the object name obtained
         * by calling toString() in the request instance.
         */

        HtmlPage page = (HtmlPage) client
                .getPage("http://localhost:" + port + "/mvc/mvctest?c=some-stateful&a=get-req");
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h2");
        assertEquals(1, elements.size());
        HtmlElement h2 = elements.get(0);
        String hexHashcode1 = h2.getTextContent();

        /* make a second request */
        page = client.getPage("http://localhost:" + port + "/mvc/mvctest?c=some-stateful&a=get-req");
        elements = page.getElementsByTagName("h2");
        assertEquals(1, elements.size());
        h2 = elements.get(0);
        String hexHashcode2 = h2.getTextContent();

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

        HtmlPage page = (HtmlPage) client
                .getPage("http://localhost:" + port + "/mvc/mvctest?c=some-stateful&a=get-inj");
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h2");
        assertEquals(1, elements.size());
        HtmlElement h2 = elements.get(0);
        String hexHashcode1 = h2.getTextContent();

        /* make a second request */
        page = client.getPage("http://localhost:" + port + "/mvc/mvctest?c=some-stateful&a=get-inj");
        elements = page.getElementsByTagName("h2");
        assertEquals(1, elements.size());
        h2 = elements.get(0);
        String hexHashcode2 = h2.getTextContent();

        /* check that a new @Inject dependency was injected */
        assertFalse(hexHashcode1.equals(hexHashcode2));
    }

    @Test
    public void someStatefulControllerTestInitAction() throws Exception {

        HtmlPage page = (HtmlPage) client.getPage("http://localhost:" + port
                + "/mvc/mvctest?c=some-stateful&a=test-init");
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h2");
        assertEquals(1, elements.size());
        HtmlElement h2 = elements.get(0);
        assertEquals("Hello from init-called: 1", h2.getTextContent());

        /*
         * test that init() is called only once after construction, and not per
         * request
         */
        page = client.getPage("http://localhost:" + port + "/mvc/mvctest?c=some-stateful&a=test-init");
        elements = page.getElementsByTagName("h2");
        assertEquals(1, elements.size());
        h2 = elements.get(0);
        assertEquals("Hello from init-called: 1", h2.getTextContent());
    }

    @Test
    public void injectableControllerTestAction() throws Exception {

        HtmlPage page = (HtmlPage) client.getPage("http://localhost:" + port
                + "/mvc/mvctest?c=injectable&a=test&var=inj");
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h1");
        assertEquals(1, elements.size());
        HtmlElement h1 = elements.get(0);
        assertEquals("This is the injectable.jsp file of the InjectableController testAction!", h1.getTextContent());
        elements = page.getElementsByTagName("h2");
        assertEquals(1, elements.size());
        HtmlElement h2 = elements.get(0);
        assertEquals("injected-inj", h2.getTextContent());
    }

    @Test
    public void someSingletonControllerSomeAction() throws Exception {

        HtmlPage page = (HtmlPage) client.getPage("http://localhost:" + port
                + "/mvc/mvctest?c=some-singleton&a=some-action");
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h1");
        assertEquals(1, elements.size());
        HtmlElement h1 = elements.get(0);
        assertEquals("This is the singleton.jsp file of the SomeSingletonController someAction!", h1.getTextContent());
    }

    @Test
    public void someSingletonControllerVarAction() throws Exception {

        HtmlPage page = (HtmlPage) client.getPage("http://localhost:" + port
                + "/mvc/mvctest?c=some-singleton&a=set-var&var=statetest");
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h1");
        assertEquals(1, elements.size());
        HtmlElement h1 = elements.get(0);
        assertEquals("This is the singleton.jsp file of the SomeSingletonController someAction!", h1.getTextContent());

        /* make a second request */
        page = client.getPage("http://localhost:" + port + "/mvc/mvctest?c=some-singleton&a=get-var");
        elements = page.getElementsByTagName("h2");
        assertEquals(1, elements.size());
        HtmlElement h2 = elements.get(0);
        assertEquals("Hello from statetest", h2.getTextContent());
    }

    @Test
    public void someSingletonControllerVarAction2() throws Exception {

        HtmlPage page = (HtmlPage) client.getPage("http://localhost:" + port
                + "/mvc/mvctest?c=some-singleton&a=set-var&var=statetest");
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h1");
        assertEquals(1, elements.size());
        HtmlElement h1 = elements.get(0);
        assertEquals("This is the singleton.jsp file of the SomeSingletonController someAction!", h1.getTextContent());

        /* make a second request with a new client */
        client = new WebClient();
        page = client.getPage("http://localhost:" + port + "/mvc/mvctest?c=some-singleton&a=get-var");
        elements = page.getElementsByTagName("h2");
        assertEquals(1, elements.size());
        HtmlElement h2 = elements.get(0);
        assertEquals("Hello from statetest", h2.getTextContent()); /*
                                                                    * state
                                                                    * stored as
                                                                    * controller
                                                                    * is a
                                                                    * singleton
                                                                    */
    }

    @Test
    public void someSingletonControllerReqAction() throws Exception {

        /*
         * Test that the injected HttpServletRequests are different, by
         * examining the hex hashcode that is part of the object name obtained
         * by calling toString() in the request instance.
         */

        HtmlPage page = (HtmlPage) client.getPage("http://localhost:" + port
                + "/mvc/mvctest?c=some-singleton&a=get-req");
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h2");
        assertEquals(1, elements.size());
        HtmlElement h2 = elements.get(0);
        String hexHashcode1 = h2.getTextContent();

        /* make a second request */
        page = client.getPage("http://localhost:" + port + "/mvc/mvctest?c=some-singleton&a=get-req");
        elements = page.getElementsByTagName("h2");
        assertEquals(1, elements.size());
        h2 = elements.get(0);
        String hexHashcode2 = h2.getTextContent();

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

        HtmlPage page = (HtmlPage) client.getPage("http://localhost:" + port
                + "/mvc/mvctest?c=some-singleton&a=get-inj");
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h2");
        assertEquals(1, elements.size());
        HtmlElement h2 = elements.get(0);
        String hexHashcode1 = h2.getTextContent();

        /* make a second request */
        page = client.getPage("http://localhost:" + port + "/mvc/mvctest?c=some-singleton&a=get-inj");
        elements = page.getElementsByTagName("h2");
        assertEquals(1, elements.size());
        h2 = elements.get(0);
        String hexHashcode2 = h2.getTextContent();

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

        HtmlPage page = (HtmlPage) client.getPage("http://localhost:" + port + "/mvc/mvctest?c=before&a=index");
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h1");
        assertEquals(1, elements.size());
        HtmlElement h1 = elements.get(0);
        assertEquals("This is the before.jsp file of the BeforeController beforeAction!", h1.getTextContent());
    }

    @Test
    public void afterControllerIndexAction() throws Exception {

        HtmlPage page = (HtmlPage) client.getPage("http://localhost:" + port + "/mvc/mvctest?c=after&a=index");
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h1");
        assertEquals(1, elements.size());
        HtmlElement h1 = elements.get(0);
        assertEquals("This is the after.jsp file of the AfterController afterAction!", h1.getTextContent());
    }

    @Test
    public void beforeWithContextControllerIndexAction() throws Exception {

        HtmlPage page = (HtmlPage) client.getPage("http://localhost:" + port
                + "/mvc/mvctest?c=beforectx&a=index&p1=testctx");
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h2");
        assertEquals(1, elements.size());
        HtmlElement h2 = elements.get(0);
        assertEquals("Hello from testctx", h2.getTextContent());
    }

    @Test
    public void afterWithContextControllerIndexAction() throws Exception {

        HtmlPage page = (HtmlPage) client.getPage("http://localhost:" + port
                + "/mvc/mvctest?c=afterctx&a=index&p1=testctx");
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h2");
        assertEquals(1, elements.size());
        HtmlElement h2 = elements.get(0);
        assertEquals("Hello from testctx", h2.getTextContent());
    }

    @Test
    public void afterWithContextControllerDefaultAction() throws Exception {

        HtmlPage page = (HtmlPage) client.getPage("http://localhost:" + port + "/mvc/mvctest?c=afterctx2");
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h2");
        assertEquals(1, elements.size());
        HtmlElement h2 = elements.get(0);
        assertEquals("Hello from default", h2.getTextContent());
    }

    @Test
    public void beforeWithModelControllerIndexAction() throws Exception {

        HtmlPage page = (HtmlPage) client.getPage("http://localhost:" + port
                + "/mvc/mvctest?c=beforectx2&a=index&userName=john&password=doe");
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h2");
        assertEquals(1, elements.size());
        HtmlElement h2 = elements.get(0);
        assertEquals("Hello from john doe", h2.getTextContent());
    }

    @Test
    public void interceptedController1SomeAction() throws Exception {

        List<String> invocationList = new ArrayList<String>();
        InterceptedController1.invocationList = invocationList;
        Interceptor1.invocationList = invocationList;

        HtmlPage page = (HtmlPage) client.getPage("http://localhost:" + port
                + "/mvc/mvctest?c=intercepted1&a=some-action");
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h2");
        assertEquals(1, elements.size());
        HtmlElement h2 = elements.get(0);
        assertEquals("Hello from someAction", h2.getTextContent());

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

        HtmlPage page = (HtmlPage) client.getPage("http://localhost:" + port
                + "/mvc/mvctest?c=intercepted2&a=some-action");
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h2");
        assertEquals(1, elements.size());
        HtmlElement h2 = elements.get(0);
        assertEquals("Hello from someAction", h2.getTextContent());

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

        HtmlPage page = (HtmlPage) client.getPage("http://localhost:" + port + "/mvc/mvctest?c=intercepted2");
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h2");
        assertEquals(1, elements.size());
        HtmlElement h2 = elements.get(0);
        assertEquals("Hello from defaultAction", h2.getTextContent());

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

        HtmlPage page = (HtmlPage) client.getPage("http://localhost:" + port
                + "/mvc/mvctest?c=intercepted3&a=some-action");
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h2");
        assertEquals(1, elements.size());
        HtmlElement h2 = elements.get(0);
        assertEquals("Hello from someAction", h2.getTextContent());

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

        HtmlPage page = (HtmlPage) client.getPage("http://localhost:" + port
                + "/mvc/mvctest?c=intercepted4&a=some-action");
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h2");
        assertEquals(1, elements.size());
        HtmlElement h2 = elements.get(0);
        assertEquals("Hello from someAction", h2.getTextContent());

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

        HtmlPage page = (HtmlPage) client.getPage("http://localhost:" + port + "/mvc/mvctest?c=intercepted4");
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h2");
        assertEquals(1, elements.size());
        HtmlElement h2 = elements.get(0);
        assertEquals("Hello from defaultAction", h2.getTextContent());

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

        HtmlPage page = (HtmlPage) client.getPage("http://localhost:" + port + "/mvc/mvctest?c=intercepted1");
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h2");
        assertEquals(1, elements.size());
        HtmlElement h2 = elements.get(0);
        assertEquals("Hello from defaultAction", h2.getTextContent());

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

        HtmlPage page = (HtmlPage) client.getPage("http://localhost:" + port + "/mvc/mvctest?c=intercepted3");
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h2");
        assertEquals(1, elements.size());
        HtmlElement h2 = elements.get(0);
        assertEquals("Hello from defaultAction", h2.getTextContent());

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

        HtmlPage page = (HtmlPage) client.getPage("http://localhost:" + port + "/mvc/mvctest?c=intercepted5");
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h2");
        assertEquals(1, elements.size());
        HtmlElement h2 = elements.get(0);
        assertEquals("Hello from defaultAction", h2.getTextContent());

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

        HtmlPage page = (HtmlPage) client.getPage("http://localhost:" + port
                + "/mvc/mvctest?c=intercepted5&a=some-action");
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h2");
        assertEquals(1, elements.size());
        HtmlElement h2 = elements.get(0);
        assertEquals("Hello from someAction", h2.getTextContent());

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

        HtmlPage page = (HtmlPage) client.getPage("http://localhost:" + port + "/mvc/mvctest?c=intercepted6");
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h2");
        assertEquals(1, elements.size());
        HtmlElement h2 = elements.get(0);
        assertEquals("Hello from defaultAction", h2.getTextContent());

        assertEquals(1, invocationList.size());
        assertEquals("defaultAction", invocationList.get(0));
    }

    @Test
    public void interceptedController7SomeAction() throws Exception {

        List<String> invocationList = new ArrayList<String>();
        InterceptedController7.invocationList = invocationList;
        Interceptor1.invocationList = invocationList;

        HtmlPage page = (HtmlPage) client.getPage("http://localhost:" + port
                + "/mvc/mvctest?c=intercepted7&a=some-action");
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h2");
        assertEquals(1, elements.size());
        HtmlElement h2 = elements.get(0);
        assertEquals("Hello from someAction", h2.getTextContent());

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

        HtmlPage page = (HtmlPage) client.getPage("http://localhost:" + port + "/mvc/mvctest?c=intercepted8");
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h2");
        assertEquals(1, elements.size());
        HtmlElement h2 = elements.get(0);
        assertEquals("Hello from defaultAction", h2.getTextContent());

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

        HtmlPage page = (HtmlPage) client.getPage("http://localhost:" + port
                + "/mvc/mvctest?c=intercepted8&a=some-action");
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h2");
        assertEquals(1, elements.size());
        HtmlElement h2 = elements.get(0);
        assertEquals("Hello from someAction", h2.getTextContent());

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

        HtmlPage page = (HtmlPage) client.getPage("http://localhost:" + port + "/mvc/mvctest?c=intercepted9");
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h2");
        assertEquals(1, elements.size());
        HtmlElement h2 = elements.get(0);
        assertEquals("Hello from defaultAction", h2.getTextContent());

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

        HtmlPage page = (HtmlPage) client.getPage("http://localhost:" + port
                + "/mvc/mvctest?c=intercepted9&a=some-action");
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h2");
        assertEquals(1, elements.size());
        HtmlElement h2 = elements.get(0);
        assertEquals("Hello from someAction", h2.getTextContent());

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

        HtmlPage page = (HtmlPage) client.getPage("http://localhost:" + port
                + "/mvc/mvctest?c=intercepted10&a=some-action");
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h2");
        assertEquals(1, elements.size());
        HtmlElement h2 = elements.get(0);
        assertEquals("Hello from someAction", h2.getTextContent());

        assertEquals(2, invocationList.size());
        assertEquals("interceptor4-beforeAction:req:resp:sess:someService", invocationList.get(0));
        assertEquals("someAction", invocationList.get(1));
    }

    @Test
    public void interceptedController11SomeAction() throws Exception {

        List<String> invocationList = new ArrayList<String>();
        InterceptedController11.invocationList = invocationList;
        Interceptor9.invocationList = invocationList;

        HtmlPage page = (HtmlPage) client.getPage("http://localhost:" + port
                + "/mvc/mvctest?c=intercepted11&a=some-action");
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h2");
        assertEquals(1, elements.size());
        HtmlElement h2 = elements.get(0);
        assertEquals("Hello from someAction", h2.getTextContent());

        assertEquals(2, invocationList.size());
        assertEquals("someAction", invocationList.get(0));
        assertEquals("interceptor9-afterAction:req:resp:sess:someService", invocationList.get(1));
    }

    @Test
    public void interceptedController12InterceptedBeforeAction() throws Exception {

        HtmlPage page = (HtmlPage) client.getPage("http://localhost:" + port
                + "/mvc/mvctest?c=intercepted12&a=intercepted-before");
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h2");
        assertEquals(1, elements.size());
        HtmlElement h2 = elements.get(0);
        assertEquals("Hello from interceptor5-beforeAction:req:resp:sess:someService", h2.getTextContent());
    }

    @Test
    public void interceptedController12InterceptedAfterAction() throws Exception {

        HtmlPage page = (HtmlPage) client.getPage("http://localhost:" + port
                + "/mvc/mvctest?c=intercepted12&a=intercepted-after");
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h2");
        assertEquals(1, elements.size());
        HtmlElement h2 = elements.get(0);
        assertEquals("Hello from interceptor7-afterAction:req:resp:sess:someService", h2.getTextContent());
    }

    @Test
    public void interceptedController12InterceptedBefore2Action() throws Exception {

        List<String> invocationList = new ArrayList<String>();
        InterceptedController12.invocationList = invocationList;
        Interceptor6.invocationList = invocationList;

        HtmlPage page = (HtmlPage) client.getPage("http://localhost:" + port
                + "/mvc/mvctest?c=intercepted12&a=intercepted-before2");
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h2");
        assertEquals(1, elements.size());
        HtmlElement h2 = elements.get(0);
        assertEquals("Hello from interceptedBefore2", h2.getTextContent());

        assertEquals(2, invocationList.size());
        assertEquals("interceptor6-beforeAction:req:resp:sess:someService", invocationList.get(0));
        assertEquals("interceptedBefore2", invocationList.get(1));
    }

    @Test
    public void interceptedController12InterceptedAfter2Action() throws Exception {

        List<String> invocationList = new ArrayList<String>();
        InterceptedController12.invocationList = invocationList;
        Interceptor8.invocationList = invocationList;

        HtmlPage page = (HtmlPage) client.getPage("http://localhost:" + port
                + "/mvc/mvctest?c=intercepted12&a=intercepted-after2");
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h2");
        assertEquals(1, elements.size());
        HtmlElement h2 = elements.get(0);
        assertEquals("Hello from interceptedAfter2", h2.getTextContent());

        assertEquals(2, invocationList.size());
        assertEquals("interceptedAfter2", invocationList.get(0));
        assertEquals("interceptor8-afterAction:req:resp:sess:someService", invocationList.get(1));
    }

    @Test
    public void interceptedController12SomeAction() throws Exception {

        List<String> invocationList = new ArrayList<String>();
        InterceptedController12.invocationList = invocationList;
        Interceptor2.invocationList = invocationList;

        HtmlPage page = (HtmlPage) client.getPage("http://localhost:" + port
                + "/mvc/mvctest?c=intercepted12&a=some-action");
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h2");
        assertEquals(1, elements.size());
        HtmlElement h2 = elements.get(0);
        assertEquals("Hello from interceptor2-beforeAction:req:resp:sess:someService", h2.getTextContent());

        assertEquals(0, invocationList.size());
    }

    @Test
    public void interceptedController12SomeAction2() throws Exception {

        List<String> invocationList = new ArrayList<String>();
        InterceptedController12.invocationList = invocationList;
        Interceptor3.invocationList = invocationList;

        HtmlPage page = (HtmlPage) client.getPage("http://localhost:" + port
                + "/mvc/mvctest?c=intercepted12&a=some-action2");
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h2");
        assertEquals(1, elements.size());
        HtmlElement h2 = elements.get(0);
        assertEquals("Hello from interceptor3-afterAction:req:resp:sess:someService", h2.getTextContent());

        assertEquals(2, invocationList.size());
        assertEquals("interceptor3-beforeAction:req:resp:sess:someService", invocationList.get(0));
        assertEquals("someAction2", invocationList.get(1));
    }

    @Test
    public void interceptedController12ParamAction() throws Exception {

        List<String> invocationList = new ArrayList<String>();
        InterceptedController12.invocationList = invocationList;
        Interceptor10.invocationList = invocationList;

        HtmlPage page = (HtmlPage) client.getPage("http://localhost:" + port
                + "/mvc/mvctest?c=intercepted12&a=param&p1=test");
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h2");
        assertEquals(1, elements.size());
        HtmlElement h2 = elements.get(0);
        assertEquals("Hello from test", h2.getTextContent());

        assertEquals(3, invocationList.size());
        assertEquals("interceptor10-beforeAction", invocationList.get(0));
        assertEquals("param", invocationList.get(1));
        assertEquals("interceptor10-afterAction", invocationList.get(2));
    }

    @Test
    public void defaultActionWithArgs() throws Exception {

        HtmlPage page = (HtmlPage) client.getPage("http://localhost:" + port + "/mvc/mvctest?c=default-args&p1=test");
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h2");
        assertEquals(1, elements.size());
        HtmlElement h2 = elements.get(0);
        assertEquals("Hello from test", h2.getTextContent());
    }

    @Test
    public void defaultControllerWithAction() throws Exception {

        HtmlPage page = (HtmlPage) client.getPage("http://localhost:" + port + "/mvc/mvctest?a=with-param&var=test");
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h2");
        assertEquals(1, elements.size());
        HtmlElement h2 = elements.get(0);
        assertEquals("Hello from test", h2.getTextContent());
    }

    @Test
    public void classNameController() throws Exception {

        HtmlPage page = (HtmlPage) client.getPage("http://localhost:" + port
                + "/mvc/mvctest?c=ClassNameController&a=sayHello&name=John");
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h2");
        assertEquals(1, elements.size());
        HtmlElement h2 = elements.get(0);
        assertEquals("Hello from classNameControllerAction:John", h2.getTextContent());
    }

    @Test
    public void httpMethodGET() throws Exception {

        WebRequest wr = new WebRequest(new URL("http://localhost:" + port + "/mvc/mvctest"), HttpMethod.POST);
        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        pairs.add(new NameValuePair("c", "httpmethod1"));
        wr.setRequestParameters(pairs);
        HtmlPage page = (HtmlPage) client.getPage(wr);
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h2");
        assertEquals(1, elements.size());
        HtmlElement h2 = elements.get(0);
        assertEquals("Hello from default", h2.getTextContent());
    }

    @Test
    public void httpMethodGET2() throws Exception {

        WebRequest wr = new WebRequest(new URL("http://localhost:" + port + "/mvc/mvctest?c=httpmethod1"),
                HttpMethod.GET);
        HtmlPage page = (HtmlPage) client.getPage(wr);
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h2");
        assertEquals(1, elements.size());
        HtmlElement h2 = elements.get(0);
        assertEquals("Hello from get", h2.getTextContent());
    }

    @Test
    public void httpMethodGET3() throws Exception {

        HtmlPage page = (HtmlPage) client.getPage("http://localhost:" + port + "/mvc/mvctest?c=httpmethod1&a=sayHello");
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h1");
        assertEquals(1, elements.size());
        HtmlElement h1 = elements.get(0);
        assertEquals("Error", h1.getTextContent());
    }

    @Test
    public void httpMethodPOST() throws Exception {

        WebRequest wr = new WebRequest(new URL("http://localhost:" + port + "/mvc/mvctest?c=httpmethod2"),
                HttpMethod.GET);
        HtmlPage page = (HtmlPage) client.getPage(wr);
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h2");
        assertEquals(1, elements.size());
        HtmlElement h2 = elements.get(0);
        assertEquals("Hello from default", h2.getTextContent());
    }

    @Test
    public void httpMethodPOST2() throws Exception {

        WebRequest wr = new WebRequest(new URL("http://localhost:" + port + "/mvc/mvctest"), HttpMethod.POST);
        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        pairs.add(new NameValuePair("c", "httpmethod2"));
        wr.setRequestParameters(pairs);
        HtmlPage page = (HtmlPage) client.getPage(wr);
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h2");
        assertEquals(1, elements.size());
        HtmlElement h2 = elements.get(0);
        assertEquals("Hello from post", h2.getTextContent());
    }

    @Test
    public void httpMethodPOST3() throws Exception {

        HtmlPage page = (HtmlPage) client.getPage("http://localhost:" + port + "/mvc/mvctest?c=httpmethod2&a=sayHello");
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h1");
        assertEquals(1, elements.size());
        HtmlElement h1 = elements.get(0);
        assertEquals("Error", h1.getTextContent());
    }

    @Test
    public void httpMethodPUT() throws Exception {

        HtmlPage page = (HtmlPage) client.getPage("http://localhost:" + port + "/mvc/mvctest?c=httpmethod3");
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h2");
        assertEquals(1, elements.size());
        HtmlElement h2 = elements.get(0);
        assertEquals("Hello from default", h2.getTextContent());
    }

    @Test
    public void httpMethodPUT2() throws Exception {

        WebRequest wr = new WebRequest(new URL("http://localhost:" + port + "/mvc/mvctest?c=httpmethod3"),
                HttpMethod.PUT);
        HtmlPage page = (HtmlPage) client.getPage(wr);
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h2");
        assertEquals(1, elements.size());
        HtmlElement h2 = elements.get(0);
        assertEquals("Hello from put", h2.getTextContent());
    }

    @Test
    public void httpMethodPUT3() throws Exception {

        HtmlPage page = (HtmlPage) client.getPage("http://localhost:" + port
                + "/mvc/mvctest?c=httpmethod3&a=doPutAction");
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h1");
        assertEquals(1, elements.size());
        HtmlElement h1 = elements.get(0);
        assertEquals("Error", h1.getTextContent());
    }

    @Test
    public void httpMethodHEAD() throws Exception {

        HtmlPage page = (HtmlPage) client.getPage("http://localhost:" + port + "/mvc/mvctest?c=httpmethod4");
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h2");
        assertEquals(1, elements.size());
        HtmlElement h2 = elements.get(0);
        assertEquals("Hello from default", h2.getTextContent());
    }

    @Test
    public void httpMethodHEAD2() throws Exception {

        WebRequest wr = new WebRequest(new URL("http://localhost:" + port + "/mvc/mvctest?c=httpmethod4"),
                HttpMethod.HEAD);
        Page page = client.getPage(wr);
        /*
         * HEAD responses are not supposed to contain a message-body as per RFC
         * 2616
         */
        List<NameValuePair> headers = page.getWebResponse().getResponseHeaders();
        NameValuePair calledHeader = null;
        for (NameValuePair pair : headers) {
            if ("CALLED".equalsIgnoreCase(pair.getName())) {
                calledHeader = pair;
            }
        }
        assertNotNull(calledHeader);
        assertEquals("called", calledHeader.getValue());
    }

    @Test
    public void httpMethodHEAD3() throws Exception {

        HtmlPage page = (HtmlPage) client.getPage("http://localhost:" + port
                + "/mvc/mvctest?c=httpmethod4&a=doHeadAction");
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h1");
        assertEquals(1, elements.size());
        HtmlElement h1 = elements.get(0);
        assertEquals("Error", h1.getTextContent());
    }

    @Test
    public void httpMethodTRACE() throws Exception {

        HtmlPage page = (HtmlPage) client.getPage("http://localhost:" + port + "/mvc/mvctest?c=httpmethod5");
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h2");
        assertEquals(1, elements.size());
        HtmlElement h2 = elements.get(0);
        assertEquals("Hello from default", h2.getTextContent());
    }

    @Test
    public void httpMethodTRACE2() throws Exception {

        HtmlPage page = (HtmlPage) client.getPage("http://localhost:" + port
                + "/mvc/mvctest?c=httpmethod5&a=doTraceAction");
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h1");
        assertEquals(1, elements.size());
        HtmlElement h1 = elements.get(0);
        assertEquals("Error", h1.getTextContent());
    }

    @Test
    public void httpMethodDELETE() throws Exception {

        HtmlPage page = (HtmlPage) client.getPage("http://localhost:" + port + "/mvc/mvctest?c=httpmethod6");
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h2");
        assertEquals(1, elements.size());
        HtmlElement h2 = elements.get(0);
        assertEquals("Hello from default", h2.getTextContent());
    }

    @Test
    public void httpMethodDELETE2() throws Exception {

        WebRequest wr = new WebRequest(new URL("http://localhost:" + port + "/mvc/mvctest?c=httpmethod6"),
                HttpMethod.DELETE);
        HtmlPage page = (HtmlPage) client.getPage(wr);
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h2");
        assertEquals(1, elements.size());
        HtmlElement h2 = elements.get(0);
        assertEquals("Hello from delete", h2.getTextContent());
    }

    @Test
    public void httpMethodDELETE3() throws Exception {

        HtmlPage page = (HtmlPage) client.getPage("http://localhost:" + port
                + "/mvc/mvctest?c=httpmethod6&a=doDeleteAction");
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h1");
        assertEquals(1, elements.size());
        HtmlElement h1 = elements.get(0);
        assertEquals("Error", h1.getTextContent());
    }

    @Test
    public void httpMethodOPTIONS() throws Exception {

        HtmlPage page = (HtmlPage) client.getPage("http://localhost:" + port + "/mvc/mvctest?c=httpmethod7");
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h2");
        assertEquals(1, elements.size());
        HtmlElement h2 = elements.get(0);
        assertEquals("Hello from default", h2.getTextContent());
    }

    @Test
    public void httpMethodOPTIONS2() throws Exception {

        WebRequest wr = new WebRequest(new URL("http://localhost:" + port + "/mvc/mvctest?c=httpmethod7"),
                HttpMethod.OPTIONS);
        HtmlPage page = (HtmlPage) client.getPage(wr);
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h2");
        assertEquals(1, elements.size());
        HtmlElement h2 = elements.get(0);
        assertEquals("Hello from options", h2.getTextContent());
    }

    @Test
    public void httpMethodOPTIONS3() throws Exception {

        HtmlPage page = (HtmlPage) client.getPage("http://localhost:" + port
                + "/mvc/mvctest?c=httpmethod7&a=doOptionsAction");
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h1");
        assertEquals(1, elements.size());
        HtmlElement h1 = elements.get(0);
        assertEquals("Error", h1.getTextContent());
    }

    @Test
    public void httpMethodMulti() throws Exception {

        WebRequest wr = new WebRequest(new URL("http://localhost:" + port + "/mvc/mvctest?c=httpmethod8"),
                HttpMethod.GET);
        HtmlPage page = (HtmlPage) client.getPage(wr);
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h2");
        assertEquals(1, elements.size());
        HtmlElement h2 = elements.get(0);
        assertEquals("Hello from multi", h2.getTextContent());
    }

    @Test
    public void httpMethodMulti2() throws Exception {

        WebRequest wr = new WebRequest(new URL("http://localhost:" + port + "/mvc/mvctest"), HttpMethod.POST);
        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        pairs.add(new NameValuePair("c", "httpmethod8"));
        wr.setRequestParameters(pairs);
        HtmlPage page = (HtmlPage) client.getPage(wr);
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h2");
        assertEquals(1, elements.size());
        HtmlElement h2 = elements.get(0);
        assertEquals("Hello from multi", h2.getTextContent());
    }

    @Test
    public void httpMethodMulti3() throws Exception {

        WebRequest wr = new WebRequest(new URL("http://localhost:" + port + "/mvc/mvctest?c=httpmethod8&a=sayHello"),
                HttpMethod.GET);
        HtmlPage page = (HtmlPage) client.getPage(wr);
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h2");
        assertEquals(1, elements.size());
        HtmlElement h2 = elements.get(0);
        assertEquals("Hello from sayHello", h2.getTextContent());
    }

    @Test
    public void httpMethodMulti4() throws Exception {

        WebRequest wr = new WebRequest(new URL("http://localhost:" + port + "/mvc/mvctest"), HttpMethod.POST);
        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        pairs.add(new NameValuePair("c", "httpmethod8"));
        pairs.add(new NameValuePair("a", "sayHello"));
        wr.setRequestParameters(pairs);
        HtmlPage page = (HtmlPage) client.getPage(wr);
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h2");
        assertEquals(1, elements.size());
        HtmlElement h2 = elements.get(0);
        assertEquals("Hello from sayHello", h2.getTextContent());
    }

    @Test
    public void interceptedController13NoAction() throws Exception {

        HtmlPage page = (HtmlPage) client.getPage("http://localhost:" + port + "/mvc/mvctest?c=intercepted13");
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h1");
        assertEquals(1, elements.size());
        HtmlElement h1 = elements.get(0);
        assertEquals("Error", h1.getTextContent());
    }

    @Test
    public void interceptedController13POSTAction() throws Exception {

        List<String> invocationList = new ArrayList<String>();
        InterceptedController13.invocationList = invocationList;
        Interceptor1.invocationList = invocationList;
        Interceptor1b.invocationList = invocationList;

        WebRequest wr = new WebRequest(new URL("http://localhost:" + port + "/mvc/mvctest"), HttpMethod.POST);
        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        pairs.add(new NameValuePair("c", "intercepted13"));
        wr.setRequestParameters(pairs);
        HtmlPage page = (HtmlPage) client.getPage(wr);
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h2");
        assertEquals(1, elements.size());
        HtmlElement h2 = elements.get(0);
        assertEquals("Hello from postAction", h2.getTextContent());

        assertEquals(5, invocationList.size());
        assertEquals("interceptor1-beforeAction:req:resp:sess:someService", invocationList.get(0));
        assertEquals("interceptor1b-beforeAction:req:resp:sess:someService", invocationList.get(1));
        assertEquals("postAction", invocationList.get(2));
        assertEquals("interceptor1b-afterAction:req:resp:sess:someService:ok", invocationList.get(3));
        assertEquals("interceptor1-afterAction:req:resp:sess:someService:ok", invocationList.get(4));
    }

    @Test
    public void interceptedController14NoAction() throws Exception {

        WebRequest wr = new WebRequest(new URL("http://localhost:" + port + "/mvc/mvctest"), HttpMethod.POST);
        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        pairs.add(new NameValuePair("c", "intercepted14"));
        wr.setRequestParameters(pairs);
        HtmlPage page = (HtmlPage) client.getPage(wr);
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h1");
        assertEquals(1, elements.size());
        HtmlElement h1 = elements.get(0);
        assertEquals("Error", h1.getTextContent());
    }

    @Test
    public void interceptedController14GETAction() throws Exception {

        List<String> invocationList = new ArrayList<String>();
        InterceptedController14.invocationList = invocationList;
        Interceptor1.invocationList = invocationList;
        Interceptor1b.invocationList = invocationList;

        HtmlPage page = (HtmlPage) client.getPage("http://localhost:" + port + "/mvc/mvctest?c=intercepted14");
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h2");
        assertEquals(1, elements.size());
        HtmlElement h2 = elements.get(0);
        assertEquals("Hello from getAction", h2.getTextContent());

        assertEquals(5, invocationList.size());
        assertEquals("interceptor1-beforeAction:req:resp:sess:someService", invocationList.get(0));
        assertEquals("interceptor1b-beforeAction:req:resp:sess:someService", invocationList.get(1));
        assertEquals("getAction", invocationList.get(2));
        assertEquals("interceptor1b-afterAction:req:resp:sess:someService:ok", invocationList.get(3));
        assertEquals("interceptor1-afterAction:req:resp:sess:someService:ok", invocationList.get(4));
    }

    @Test
    public void interceptedController15NoAction() throws Exception {

        WebRequest wr = new WebRequest(new URL("http://localhost:" + port + "/mvc/mvctest"), HttpMethod.POST);
        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        pairs.add(new NameValuePair("c", "intercepted15"));
        wr.setRequestParameters(pairs);
        HtmlPage page = (HtmlPage) client.getPage(wr);
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h1");
        assertEquals(1, elements.size());
        HtmlElement h1 = elements.get(0);
        assertEquals("Error", h1.getTextContent());
    }

    @Test
    public void interceptedController15PUTAction() throws Exception {

        List<String> invocationList = new ArrayList<String>();
        InterceptedController15.invocationList = invocationList;
        Interceptor1.invocationList = invocationList;
        Interceptor1b.invocationList = invocationList;

        WebRequest wr = new WebRequest(new URL("http://localhost:" + port + "/mvc/mvctest?c=intercepted15"),
                HttpMethod.PUT);
        HtmlPage page = (HtmlPage) client.getPage(wr);
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h2");
        assertEquals(1, elements.size());
        HtmlElement h2 = elements.get(0);
        assertEquals("Hello from putAction", h2.getTextContent());

        assertEquals(5, invocationList.size());
        assertEquals("interceptor1-beforeAction:req:resp:sess:someService", invocationList.get(0));
        assertEquals("interceptor1b-beforeAction:req:resp:sess:someService", invocationList.get(1));
        assertEquals("putAction", invocationList.get(2));
        assertEquals("interceptor1b-afterAction:req:resp:sess:someService:ok", invocationList.get(3));
        assertEquals("interceptor1-afterAction:req:resp:sess:someService:ok", invocationList.get(4));
    }

    @Test
    public void interceptedController16NoAction() throws Exception {

        WebRequest wr = new WebRequest(new URL("http://localhost:" + port + "/mvc/mvctest"), HttpMethod.POST);
        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        pairs.add(new NameValuePair("c", "intercepted16"));
        wr.setRequestParameters(pairs);
        HtmlPage page = (HtmlPage) client.getPage(wr);
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h1");
        assertEquals(1, elements.size());
        HtmlElement h1 = elements.get(0);
        assertEquals("Error", h1.getTextContent());
    }

    @Test
    public void interceptedController17NoAction() throws Exception {

        WebRequest wr = new WebRequest(new URL("http://localhost:" + port + "/mvc/mvctest"), HttpMethod.POST);
        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        pairs.add(new NameValuePair("c", "intercepted17"));
        wr.setRequestParameters(pairs);
        HtmlPage page = (HtmlPage) client.getPage(wr);
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h1");
        assertEquals(1, elements.size());
        HtmlElement h1 = elements.get(0);
        assertEquals("Error", h1.getTextContent());
    }

    @Test
    public void interceptedController17HEADAction() throws Exception {

        List<String> invocationList = new ArrayList<String>();
        InterceptedController17.invocationList = invocationList;
        Interceptor1.invocationList = invocationList;
        Interceptor1b.invocationList = invocationList;

        WebRequest wr = new WebRequest(new URL("http://localhost:" + port + "/mvc/mvctest?c=intercepted17"),
                HttpMethod.HEAD);
        Page page = client.getPage(wr);
        /*
         * HEAD responses are not supposed to contain a message-body as per RFC
         * 2616
         */
        List<NameValuePair> headers = page.getWebResponse().getResponseHeaders();
        NameValuePair calledHeader = null;
        for (NameValuePair pair : headers) {
            if ("CALLED".equalsIgnoreCase(pair.getName())) {
                calledHeader = pair;
            }
        }
        assertNotNull(calledHeader);
        assertEquals("called", calledHeader.getValue());

        assertEquals(5, invocationList.size());
        assertEquals("interceptor1-beforeAction:req:resp:sess:someService", invocationList.get(0));
        assertEquals("interceptor1b-beforeAction:req:resp:sess:someService", invocationList.get(1));
        assertEquals("headAction", invocationList.get(2));
        assertEquals("interceptor1b-afterAction:req:resp:sess:someService:ok", invocationList.get(3));
        assertEquals("interceptor1-afterAction:req:resp:sess:someService:ok", invocationList.get(4));
    }

    @Test
    public void interceptedController18NoAction() throws Exception {

        WebRequest wr = new WebRequest(new URL("http://localhost:" + port + "/mvc/mvctest"), HttpMethod.POST);
        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        pairs.add(new NameValuePair("c", "intercepted18"));
        wr.setRequestParameters(pairs);
        HtmlPage page = (HtmlPage) client.getPage(wr);
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h1");
        assertEquals(1, elements.size());
        HtmlElement h1 = elements.get(0);
        assertEquals("Error", h1.getTextContent());
    }

    @Test
    public void interceptedController18OPTIONSAction() throws Exception {

        List<String> invocationList = new ArrayList<String>();
        InterceptedController18.invocationList = invocationList;
        Interceptor1.invocationList = invocationList;
        Interceptor1b.invocationList = invocationList;

        WebRequest wr = new WebRequest(new URL("http://localhost:" + port + "/mvc/mvctest?c=intercepted18"),
                HttpMethod.OPTIONS);
        HtmlPage page = (HtmlPage) client.getPage(wr);
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h2");
        assertEquals(1, elements.size());
        HtmlElement h2 = elements.get(0);
        assertEquals("Hello from optionsAction", h2.getTextContent());

        assertEquals(5, invocationList.size());
        assertEquals("interceptor1-beforeAction:req:resp:sess:someService", invocationList.get(0));
        assertEquals("interceptor1b-beforeAction:req:resp:sess:someService", invocationList.get(1));
        assertEquals("optionsAction", invocationList.get(2));
        assertEquals("interceptor1b-afterAction:req:resp:sess:someService:ok", invocationList.get(3));
        assertEquals("interceptor1-afterAction:req:resp:sess:someService:ok", invocationList.get(4));
    }

    @Test
    public void interceptedController19NoAction() throws Exception {

        WebRequest wr = new WebRequest(new URL("http://localhost:" + port + "/mvc/mvctest"), HttpMethod.POST);
        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        pairs.add(new NameValuePair("c", "intercepted19"));
        wr.setRequestParameters(pairs);
        HtmlPage page = (HtmlPage) client.getPage(wr);
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h1");
        assertEquals(1, elements.size());
        HtmlElement h1 = elements.get(0);
        assertEquals("Error", h1.getTextContent());
    }

    @Test
    public void interceptedController19DELETEAction() throws Exception {

        List<String> invocationList = new ArrayList<String>();
        InterceptedController19.invocationList = invocationList;
        Interceptor1.invocationList = invocationList;
        Interceptor1b.invocationList = invocationList;

        WebRequest wr = new WebRequest(new URL("http://localhost:" + port + "/mvc/mvctest?c=intercepted19"),
                HttpMethod.DELETE);
        HtmlPage page = (HtmlPage) client.getPage(wr);
        DomNodeList<HtmlElement> elements = page.getElementsByTagName("h2");
        assertEquals(1, elements.size());
        HtmlElement h2 = elements.get(0);
        assertEquals("Hello from deleteAction", h2.getTextContent());

        assertEquals(5, invocationList.size());
        assertEquals("interceptor1-beforeAction:req:resp:sess:someService", invocationList.get(0));
        assertEquals("interceptor1b-beforeAction:req:resp:sess:someService", invocationList.get(1));
        assertEquals("deleteAction", invocationList.get(2));
        assertEquals("interceptor1b-afterAction:req:resp:sess:someService:ok", invocationList.get(3));
        assertEquals("interceptor1-afterAction:req:resp:sess:someService:ok", invocationList.get(4));
    }
}