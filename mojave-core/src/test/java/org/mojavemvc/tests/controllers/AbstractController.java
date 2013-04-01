package org.mojavemvc.tests.controllers;

import org.mojavemvc.annotations.Action;
import org.mojavemvc.views.JSP;
import org.mojavemvc.views.View;

public abstract class AbstractController {

    @Action("test")
    public View testInitAction() {
        return new JSP("param").withAttribute("var", getVal());
    }
    
    protected abstract String getVal();
}
