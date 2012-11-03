/*
 * Copyright (C) 2011 Mojavemvc.org
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

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.mojavemvc.annotations.Action;
import org.mojavemvc.annotations.DefaultAction;
import org.mojavemvc.annotations.InterceptedBy;
import org.mojavemvc.annotations.StatelessController;
import org.mojavemvc.tests.interceptors.Interceptor1;
import org.mojavemvc.tests.services.SomeService;
import org.mojavemvc.views.JspView;
import org.mojavemvc.views.View;

import com.google.inject.Inject;

/**
 * @author Luis Antunes
 */
@StatelessController("intercepted1")
@InterceptedBy(Interceptor1.class)
public class InterceptedController1 {

    @Inject
    private HttpServletRequest req;

    @Inject
    private HttpServletResponse resp;

    @Inject
    private HttpSession sess;

    @Inject
    private SomeService someService;

    public static List<String> invocationList;

    @Action("some-action")
    public View someAction() {

        invocationList.add("someAction");
        return new JspView("param.jsp").withAttribute("var", "someAction");
    }

    @DefaultAction
    public View defaultAction() {

        invocationList.add("defaultAction");
        return new JspView("param.jsp").withAttribute("var", "defaultAction");
    }

    public HttpServletRequest getRequest() {
        return req;
    }

    public HttpServletResponse getResponse() {
        return resp;
    }

    public HttpSession getSession() {
        return sess;
    }

    public SomeService getSomeService() {
        return someService;
    }
}
