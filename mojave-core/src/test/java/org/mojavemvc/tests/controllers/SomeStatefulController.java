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
import org.mojavemvc.views.JSP;
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
        return new JSP("param").withAttribute("var", "init-called: " + initVal);
    }

    @DefaultAction
    public View defaultAction() {
        return new JSP("index");
    }

    @Action("some-action")
    public View someAction() {
        return new JSP("stateful");
    }

    @Action("set-var")
    public View setVarAction(@Param("var") String var) {

        someStatefulVar = var;
        return new JSP("stateful");
    }

    @Action("get-var")
    public View getVarAction() {

        return new JSP("param").withAttribute("var", someStatefulVar);
    }

    @Action("get-req")
    public View getReqAction() {

        return newParamViewWithHexHashcodeOf(request);
    }

    @Action("get-inj")
    public View getInjAction() {

        return newParamViewWithHexHashcodeOf(someService);
    }

    private View newParamViewWithHexHashcodeOf(Object object) {

        String objectName = object.toString();
        String hexHashcode = objectName.substring(objectName.indexOf('@') + 1);

        return new JSP("param").withAttribute("var", hexHashcode);
    }
}
