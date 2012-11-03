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
package org.mojavemvc.tests.controllers;

import java.sql.Date;

import javax.servlet.http.HttpServletRequest;

import org.mojavemvc.FrontController;
import org.mojavemvc.annotations.Action;
import org.mojavemvc.annotations.AfterConstruct;
import org.mojavemvc.annotations.BeforeAction;
import org.mojavemvc.annotations.DefaultAction;
import org.mojavemvc.annotations.DefaultController;
import org.mojavemvc.annotations.Param;
import org.mojavemvc.annotations.StatelessController;
import org.mojavemvc.aop.RequestContext;
import org.mojavemvc.tests.services.SomeService;
import org.mojavemvc.views.JspView;
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
        return new JspView("param.jsp").withAttribute("var", initVal);
    }

    @DefaultAction
    public View defaultActn() {
        return new JspView("param.jsp").withAttribute("var", reqCtxCntrl + " " + reqCtxActn);
    }

    @Action("test")
    public View testAction() {
        return new JspView("test.jsp");
    }

    @Action("with-param")
    public View withParamAction() {
        if (parameterIsEmpty("var")) {
            return new JspView(FrontController.getJspErrorFile());
        }

        return new JspView("param.jsp", new String[] { "var" }, new Object[] { getParameter("var") });
    }

    @Action("another-param")
    public View anotherParamAction() {
        if (parameterIsEmpty("var")) {
            return new JspView(FrontController.getJspErrorFile());
        }

        return new JspView("param.jsp").withAttribute("var", getParameter("var"));
    }

    @Action("some-service")
    public View someServiceAction() {
        if (parameterIsEmpty("var")) {
            return new JspView(FrontController.getJspErrorFile());
        }

        String answer = someService.answerRequest(getParameter("var"));

        return new JspView("some-service.jsp").withAttribute("var", answer);
    }

    @Action("test-annotation")
    public View doAnnotationTest() {
        if (parameterIsEmpty("var")) {
            return new JspView(FrontController.getJspErrorFile());
        }

        return new JspView("param.jsp").withAttribute("var", getParameter("var"));
    }

    @Action("param-annotation-string")
    public View paramAnnotationTest(@Param("p1") String p1) {

        return new JspView("params.jsp").withAttribute("p1", p1 == null ? "null" : p1);
    }

    @Action("param-annotation-string2")
    public View paramAnnotationTest2(@Param("p1") String p1, @Param("p2") String p2) {

        return new JspView("params2.jsp").withAttribute("p1", p1).withAttribute("p2", p2);
    }

    @Action("param-annotation-int")
    public View paramAnnotationTest3(@Param("p1") int p1) {

        return new JspView("params.jsp").withAttribute("p1", p1);
    }

    @Action("param-annotation-double")
    public View paramAnnotationTest4(@Param("p1") double p1) {

        return new JspView("params.jsp").withAttribute("p1", p1);
    }

    @Action("param-annotation-date")
    public View paramAnnotationTest5(@Param("p1") Date p1) {

        return new JspView("params.jsp").withAttribute("p1", p1 == null ? "null" : p1.toString());
    }

    @Action("param-annotation-all")
    public View paramAnnotationTest6(@Param("p1") Date p1, @Param("p2") String p2, @Param("p3") int p3,
            @Param("p4") double p4) {

        return new JspView("params3.jsp").withAttribute("p1", p1.toString()).withAttribute("p2", p2)
                .withAttribute("p3", p3).withAttribute("p4", p4);
    }

    @Action("param-annotation-bool")
    public View paramAnnotationTest7(@Param("p1") boolean p1) {

        return new JspView("params.jsp").withAttribute("p1", p1);
    }

    @Action("param-annotation-ints")
    public View paramAnnotationTest8(@Param("p1") int[] p1) {

        return new JspView("params3.jsp").withAttribute("p1", p1[0]).withAttribute("p2", p1[1])
                .withAttribute("p3", p1[2]).withAttribute("p4", p1[3]);
    }

    @Action("param-annotation-strings")
    public View paramAnnotationTest9(@Param("p1") String[] p1) {

        return new JspView("params3.jsp").withAttribute("p1", p1[0]).withAttribute("p2", p1[1])
                .withAttribute("p3", p1[2]).withAttribute("p4", p1[3]);
    }

    @Action("param-annotation-doubles")
    public View paramAnnotationTest10(@Param("p1") double[] p1) {

        return new JspView("params3.jsp").withAttribute("p1", p1[0]).withAttribute("p2", p1[1])
                .withAttribute("p3", p1[2]).withAttribute("p4", p1[3]);
    }

    @Action("param-annotation-dates")
    public View paramAnnotationTest11(@Param("p1") Date[] p1) {

        return new JspView("params3.jsp").withAttribute("p1", p1[0].toString()).withAttribute("p2", p1[1].toString())
                .withAttribute("p3", p1[2].toString()).withAttribute("p4", p1[3].toString());
    }

    @Action("param-annotation-bools")
    public View paramAnnotationTest12(@Param("p1") boolean[] p1) {

        return new JspView("params3.jsp").withAttribute("p1", p1[0]).withAttribute("p2", p1[1])
                .withAttribute("p3", p1[2]).withAttribute("p4", p1[3]);
    }

    @Action("injected")
    public View injectedAction() {

        String var = injectedController.process("index");
        return new JspView("param.jsp").withAttribute("var", var);
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