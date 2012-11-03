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

import java.util.List;

import org.mojavemvc.annotations.Action;
import org.mojavemvc.annotations.InterceptedBy;
import org.mojavemvc.annotations.Param;
import org.mojavemvc.annotations.StatelessController;
import org.mojavemvc.tests.interceptors.Interceptor10;
import org.mojavemvc.tests.interceptors.Interceptor2;
import org.mojavemvc.tests.interceptors.Interceptor3;
import org.mojavemvc.tests.interceptors.Interceptor5;
import org.mojavemvc.tests.interceptors.Interceptor6;
import org.mojavemvc.tests.interceptors.Interceptor7;
import org.mojavemvc.tests.interceptors.Interceptor8;
import org.mojavemvc.views.JspView;
import org.mojavemvc.views.View;

@StatelessController("intercepted12")
public class InterceptedController12 {

    public static List<String> invocationList;

    @Action("some-action")
    @InterceptedBy(Interceptor2.class)
    public View someAction() {

        invocationList.add("someAction");
        return new JspView("param.jsp").withAttribute("var", "someAction");
    }

    @Action("some-action2")
    @InterceptedBy(Interceptor3.class)
    public View someAction2() {

        invocationList.add("someAction2");
        return new JspView("param.jsp").withAttribute("var", "someAction2");
    }

    @Action("param")
    @InterceptedBy(Interceptor10.class)
    public View someAction(@Param("p1") String name) {

        invocationList.add("param");
        return new JspView("param.jsp").withAttribute("var", name);
    }

    @Action("intercepted-before")
    @InterceptedBy(Interceptor5.class)
    public View interceptedBefore() {

        return new JspView("param.jsp").withAttribute("var", "interceptedBefore");
    }

    @Action("intercepted-before2")
    @InterceptedBy(Interceptor6.class)
    public View interceptedBefore2() {

        invocationList.add("interceptedBefore2");
        return new JspView("param.jsp").withAttribute("var", "interceptedBefore2");
    }

    @Action("intercepted-after")
    @InterceptedBy(Interceptor7.class)
    public View interceptedAfter() {

        return new JspView("param.jsp").withAttribute("var", "interceptedAfter");
    }

    @Action("intercepted-after2")
    @InterceptedBy(Interceptor8.class)
    public View interceptedAfter2() {

        invocationList.add("interceptedAfter2");
        return new JspView("param.jsp").withAttribute("var", "interceptedAfter2");
    }
}
