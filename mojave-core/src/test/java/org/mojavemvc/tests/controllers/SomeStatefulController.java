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

import javax.servlet.http.HttpServletRequest;

import org.mojavemvc.annotations.Action;
import org.mojavemvc.annotations.AfterConstruct;
import org.mojavemvc.annotations.DefaultAction;
import org.mojavemvc.annotations.Param;
import org.mojavemvc.annotations.StatefulController;
import org.mojavemvc.tests.services.SomeService;
import org.mojavemvc.tests.views.HTMLPage;
import org.mojavemvc.views.View;

import com.google.inject.Inject;

/**
 * @author Luis Antunes
 */
@StatefulController("some-stateful")
public class SomeStatefulController {

    @Inject
    private HttpServletRequest request;

    @Inject
    private SomeService someService;

    private String someStatefulVar;

    private int initVal = 0;

    @AfterConstruct
    public void init() {
        initVal++;
    }

    @Action("test-init")
    public View testInitAction() {
        return new HTMLPage()
            .withH2Content("init-called: " + initVal);
    }

    @DefaultAction
    public View defaultAction() {
        return new HTMLPage()
            .withH1Content("some-stateful/default");
    }

    @Action("some-action")
    public View someAction() {
        return new HTMLPage()
            .withH1Content("some-stateful/some-action");
    }

    @Action("set-var")
    public View setVarAction(@Param("var") String var) {

        someStatefulVar = var;
        return new HTMLPage()
            .withH1Content("some-stateful/set-var");
    }

    @Action("get-var")
    public View getVarAction() {

        return new HTMLPage()
            .withH2Content(someStatefulVar != null ? someStatefulVar : "null");
    }
    
    @Action("get-req")
    public View getReqAction() {

        /*
         * it appears that some servlet containers may
         * recycle request objects, so we can't determine if
         * the request was re-injected by looking at object
         * identity; set an attribute on the request instead,
         * and if it's the same request, it will have the same
         * attribute
         */
        Long ts = (Long)request.getAttribute("ts");
        if (ts == null) {
            ts = System.currentTimeMillis();
            request.setAttribute("ts", ts);
        }
        return new HTMLPage()
            .withH2Content(String.valueOf(ts));
    }

    @Action("get-inj")
    public View getInjAction() {

        return new HTMLPage()
            .withH2Content(Integer.toHexString(System.identityHashCode(someService)));
    }
}
