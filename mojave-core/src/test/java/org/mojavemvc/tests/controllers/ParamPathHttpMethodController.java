package org.mojavemvc.tests.controllers;

import org.mojavemvc.annotations.DELETEAction;
import org.mojavemvc.annotations.GETAction;
import org.mojavemvc.annotations.POSTAction;
import org.mojavemvc.annotations.Param;
import org.mojavemvc.annotations.ParamPath;
import org.mojavemvc.annotations.StatelessController;
import org.mojavemvc.views.JSP;
import org.mojavemvc.views.View;

@StatelessController("parampath-http")
public class ParamPathHttpMethodController {

    @GETAction
    @ParamPath("say/:name")
    public View getAction(@Param("name") String name) {
        return new JSP("params2")
            .withAttribute("p1", name)
            .withAttribute("p2", "GET");    
    }
    
    @POSTAction
    @ParamPath("say/:name")
    public View postAction(@Param("name") String name) {
        return new JSP("params2")
            .withAttribute("p1", name)
            .withAttribute("p2", "POST");        
    }
    
    @DELETEAction
    @ParamPath("say/:name")
    public View deleteAction(@Param("name") String name) {
        return new JSP("params2")
            .withAttribute("p1", name)
            .withAttribute("p2", "DELETE");        
    }
}
