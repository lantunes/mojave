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
import org.mojavemvc.annotations.DefaultAction;
import org.mojavemvc.annotations.Param;
import org.mojavemvc.annotations.SingletonController;
import org.mojavemvc.tests.services.SomeService;
import org.mojavemvc.tests.views.HTMLView;
import org.mojavemvc.views.View;

import com.google.inject.Inject;

@SingletonController("some-singleton")
public class SomeSingletonController {

    @Inject
    private HttpServletRequest request;

    @Inject
    private SomeService someService;

    private String someStatefulVar;

    @DefaultAction
    public View defaultAction() {
        return new HTMLView();
    }

    @Action("some-action")
    public View someAction() {
        return new HTMLView()
            .withH1Content("some-singleton/some-action");
    }

    @Action("set-var")
    public View setVarAction(@Param("var") String var) {

        someStatefulVar = var;
        return new HTMLView()
            .withH1Content("some-singleton/set-var");
    }

    @Action("get-var")
    public View getVarAction() {

        return new HTMLView()
            .withH2Content("Hello from " + someStatefulVar);
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
        return new HTMLView()
            .withH2Content("Hello from " + String.valueOf(ts));
    }

    @Action("get-inj")
    public View getInjAction() {

        return new HTMLView()
            .withH2Content("Hello from " + Integer.toHexString(System.identityHashCode(someService)));
    }
}
