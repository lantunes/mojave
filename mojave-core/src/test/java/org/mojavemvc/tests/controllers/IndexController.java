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
package org.mojavemvc.tests.controllers;

import java.math.BigDecimal;
import java.sql.Date;

import javax.servlet.http.HttpServletRequest;

import org.mojavemvc.annotations.Action;
import org.mojavemvc.annotations.AfterConstruct;
import org.mojavemvc.annotations.BeforeAction;
import org.mojavemvc.annotations.DefaultAction;
import org.mojavemvc.annotations.DefaultController;
import org.mojavemvc.annotations.Param;
import org.mojavemvc.annotations.StatelessController;
import org.mojavemvc.aop.RequestContext;
import org.mojavemvc.tests.services.SomeProvidedService;
import org.mojavemvc.tests.services.SomeService;
import org.mojavemvc.tests.views.HTMLPage;
import org.mojavemvc.views.PlainText;
import org.mojavemvc.views.Response;
import org.mojavemvc.views.View;

import com.google.inject.Inject;

/**
 * 
 * @author Luis Antunes
 */
@DefaultController
@StatelessController("index")
public class IndexController {

    @Inject
    private HttpServletRequest request;

    @Inject
    private SomeService someService;
    
    @Inject
    private SomeProvidedService someProvidedService;

    @Inject
    private IInjectableController injectedController;

    /**
     * No-arg constructor.
     */
    public IndexController() {
        super();
    }

    private String initVal;

    @AfterConstruct
    public void init() {
        initVal = "init-called";
    }

    private String reqCtxCntrl;
    private String reqCtxActn;

    @BeforeAction
    public void doSomethingBefore(RequestContext ctx) {

        reqCtxCntrl = ctx.getController();
        reqCtxActn = ctx.getAction();
    }

    @Action("test-init")
    public View testInitAction() {
        
        return new HTMLPage()
            .withH2Content("index/test-init " + initVal);
    }

    @DefaultAction
    public View defaultActn() {
        
        return new HTMLPage()
            .withH2Content(reqCtxCntrl + ":" + reqCtxActn);
    }

    @Action("test")
    public View testAction() {
        
        return new HTMLPage()
            .withH1Content("index/test");
    }

    @Action("with-param")
    public View withParamAction() {
        
        return new HTMLPage()
            .withH2Content("index/with-param " + getParameter("var"));
    }

    @Action("another-param")
    public View anotherParamAction() {
        
        return new HTMLPage()
            .withH2Content("index/another-param " + getParameter("var"));
    }

    @Action("some-service")
    public View someServiceAction() {
        
        String answer = someService.answerRequest(getParameter("var"));

        return new HTMLPage()
            .withH2Content("index/some-service " + answer);
    }
    
    @Action("some-provided-service")
    public View someProvidedServiceAction(@Param("var") String var) {
        
        String processed = someProvidedService.processRequest(var);
        
        return new HTMLPage()
            .withH2Content(processed);
    }

    @Action("test-annotation")
    public View doAnnotationTest() {
        
        return new HTMLPage()
            .withH2Content("index/test-annotation " + getParameter("var"));
    }

    @Action("param-annotation-string")
    public View paramAnnotationTest(@Param("p1") String p1) {

        return new HTMLPage()
            .withH2Content("index/param-annotation-string " + (p1 == null ? "null" : p1));
    }

    @Action("param-annotation-string2")
    public View paramAnnotationTest2(@Param("p1") String p1, @Param("p2") String p2) {

        return new HTMLPage()
            .withH2Content("index/param-annotation-string2 " + p1 + ", " + p2);
    }

    @Action("param-annotation-int")
    public View paramAnnotationTest3(@Param("p1") int p1) {

        return new HTMLPage()
            .withH2Content("index/param-annotation-int " + p1);
    }

    @Action("param-annotation-double")
    public View paramAnnotationTest4(@Param("p1") double p1) {

        return new HTMLPage()
            .withH2Content("index/param-annotation-double " + p1);
    }
    
    @Action("param-annotation-bigdecimal")
    public View paramAnnotationTestBigDecimal(@Param("p1") BigDecimal p1) {

        return new HTMLPage()
            .withH2Content("index/param-annotation-bigdecimal " + p1);
    }

    @Action("param-annotation-date")
    public View paramAnnotationTest5(@Param("p1") Date p1) {

        return new HTMLPage()
            .withH2Content("index/param-annotation-date " + (p1 == null ? "null" : p1.toString()));
    }

    @Action("param-annotation-all")
    public View paramAnnotationTest6(@Param("p1") Date p1, @Param("p2") String p2, @Param("p3") int p3,
            @Param("p4") double p4) {

        return new HTMLPage()
            .withH2Content("index/param-annotation-all " + 
                    p1.toString() + ", " + p2 + ", " + p3 + ", " + p4);
    }

    @Action("param-annotation-bool")
    public View paramAnnotationTest7(@Param("p1") boolean p1) {

        return new HTMLPage()
            .withH2Content("index/param-annotation-bool " + p1);
    }

    @Action("param-annotation-ints")
    public View paramAnnotationTest8(@Param("p1") int[] p1) {

        return new HTMLPage()
            .withH2Content("index/param-annotation-ints " + 
                    p1[0] + ", " + p1[1] + ", " + p1[2] + ", " + p1[3]);
    }

    @Action("param-annotation-strings")
    public View paramAnnotationTest9(@Param("p1") String[] p1) {

        return new HTMLPage()
            .withH2Content("index/param-annotation-strings " + 
                    p1[0] + ", " + p1[1] + ", " + p1[2] + ", " + p1[3]);
    }

    @Action("param-annotation-doubles")
    public View paramAnnotationTest10(@Param("p1") double[] p1) {

        return new HTMLPage()
            .withH2Content("index/param-annotation-doubles " + 
                    p1[0] + ", " + p1[1] + ", " + p1[2] + ", " + p1[3]);
    }
    
    @Action("param-annotation-bigdecimals")
    public View paramAnnotationTestBigDecimals(@Param("p1") BigDecimal[] p1) {

        return new HTMLPage()
            .withH2Content("index/param-annotation-bigdecimals " + 
                    p1[0] + ", " + p1[1] + ", " + p1[2] + ", " + p1[3]);
    }

    @Action("param-annotation-dates")
    public View paramAnnotationTest11(@Param("p1") Date[] p1) {

        return new HTMLPage()
            .withH2Content("index/param-annotation-dates " + 
                    p1[0].toString() + ", " + p1[1].toString() + 
                    ", " + p1[2].toString() + ", " + p1[3].toString());
    }

    @Action("param-annotation-bools")
    public View paramAnnotationTest12(@Param("p1") boolean[] p1) {

        return new HTMLPage()
            .withH2Content("index/param-annotation-bools " + 
                    p1[0] + ", " + p1[1] + ", " + p1[2] + ", " + p1[3]);
    }

    @Action("injected")
    public View injectedAction() {

        String var = injectedController.process("index");
        return new HTMLPage()
            .withH2Content("index/injected " + var);
    }

    @Action("plain-text")
    public PlainText getText() {
        
        return new PlainText("hello");
    }
    
    @Action("status-ok")
    public View statusOK() {
        
        return new Response.OK()
            .withContent("it's ok")
            .withContentType("text/plain")
            .withLanguage("English");
    }
    
    public String getParameter(String key) {

        return request.getParameter(key);
    }

    public boolean parameterIsEmpty(String key) {

        if (getParameter(key) == null || getParameter(key).trim().length() == 0) {
            return true;
        }
        return false;
    }
}