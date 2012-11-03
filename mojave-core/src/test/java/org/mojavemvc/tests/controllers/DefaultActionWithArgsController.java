package org.mojavemvc.tests.controllers;

import org.mojavemvc.annotations.DefaultAction;
import org.mojavemvc.annotations.Param;
import org.mojavemvc.annotations.StatelessController;
import org.mojavemvc.views.JspView;
import org.mojavemvc.views.View;

@StatelessController("default-args")
public class DefaultActionWithArgsController {

    @DefaultAction
    public View defaultAction(@Param("p1") String name) {

        return new JspView("param.jsp").withAttribute("var", name);
    }
}
