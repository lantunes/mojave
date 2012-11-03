package org.mojavemvc.tests.controllers;

import java.util.List;

import org.mojavemvc.annotations.Action;
import org.mojavemvc.annotations.InterceptedBy;
import org.mojavemvc.annotations.StatelessController;
import org.mojavemvc.tests.interceptors.Interceptor4;
import org.mojavemvc.views.JspView;
import org.mojavemvc.views.View;

@StatelessController("intercepted10")
@InterceptedBy(Interceptor4.class)
public class InterceptedController10 {

    public static List<String> invocationList;

    @Action("some-action")
    public View someAction() {

        invocationList.add("someAction");
        return new JspView("param.jsp").withAttribute("var", "someAction");
    }
}
