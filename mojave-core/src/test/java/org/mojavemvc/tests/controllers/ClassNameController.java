package org.mojavemvc.tests.controllers;

import org.mojavemvc.annotations.Action;
import org.mojavemvc.annotations.Param;
import org.mojavemvc.annotations.StatelessController;
import org.mojavemvc.views.JspView;
import org.mojavemvc.views.View;

@StatelessController
public class ClassNameController {

    @Action
    public View sayHello(@Param("name") String name) {

        return new JspView("param.jsp").withAttribute("var", "classNameControllerAction:" + name);
    }
}
