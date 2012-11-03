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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.mojavemvc.annotations.Action;
import org.mojavemvc.annotations.AfterAction;
import org.mojavemvc.annotations.BeforeAction;
import org.mojavemvc.annotations.DefaultAction;
import org.mojavemvc.annotations.StatelessController;
import org.mojavemvc.views.JspView;
import org.mojavemvc.views.View;

import com.google.inject.Inject;

/**
 * @author Luis Antunes
 */
@StatelessController("some-stateless")
public class SomeStatelessController {

    @Inject
    private HttpServletRequest req;

    @Inject
    private HttpServletResponse resp;

    @Inject
    private HttpSession sess;

    @BeforeAction
    public void doSomethingBefore() {
        beforeInvokeCount++;
    }

    @Action("do-something")
    public View doSomething() {

        return new JspView("param.jsp").withAttribute("var", "test");
    }

    @AfterAction
    public void doSomethingAfter() {
        afterInvokeCount++;
    }

    @DefaultAction
    public View defaultAction() {
        return new JspView("param.jsp").withAttribute("var", "default");
    }

    /*
     * for testing
     */

    public int beforeInvokeCount = 0;
    public int afterInvokeCount = 0;

    public HttpServletRequest getRequest() {
        return req;
    }

    public HttpServletResponse getResponse() {
        return resp;
    }

    public HttpSession getSession() {
        return sess;
    }
}
