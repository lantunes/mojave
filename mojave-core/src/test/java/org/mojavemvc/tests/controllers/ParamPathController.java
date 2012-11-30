package org.mojavemvc.tests.controllers;


import org.mojavemvc.annotations.Action;
import org.mojavemvc.annotations.DefaultAction;
import org.mojavemvc.annotations.Param;
import org.mojavemvc.annotations.ParamPath;
import org.mojavemvc.annotations.StatelessController;
import org.mojavemvc.views.JSP;
import org.mojavemvc.views.View;

@StatelessController("parampath")
public class ParamPathController {

    @DefaultAction
    @ParamPath("say/:name")
    public View defaultAction(@Param("name") String name) {
        return new JSP("param").withAttribute("var", name);        
    }
    
    @Action("regex")
    @ParamPath(":name<[a-z]+>")
    public View regexAction(@Param("name") String name) {
        return new JSP("param").withAttribute("var", name);        
    }
    
    @Action("multi")
    @ParamPath(":name/:id<[0-9]+>")
    public View multiParamAction(@Param("name") String name, @Param("id") int id) {
        return new JSP("params2")
            .withAttribute("p1", name)
            .withAttribute("p2", id);        
    }
    
    @Action("encoded")
    @ParamPath("a+b/:name/:id<[0-9]+>")
    public View encodedAction(@Param("name") String name, @Param("id") int id) {
        return new JSP("params2")
            .withAttribute("p1", name)
            .withAttribute("p2", id);        
    }
}
