package org.mojavemvc.tests.controllers;

import java.io.InputStream;

import javax.servlet.ServletInputStream;

import org.mojavemvc.annotations.Action;
import org.mojavemvc.annotations.Param;
import org.mojavemvc.annotations.Resource;
import org.mojavemvc.annotations.StatelessController;
import org.mojavemvc.views.JSP;
import org.mojavemvc.views.View;

@StatelessController("stream-param")
public class StreamParamController {

    @Action
    public View inputStreamAlone(@Resource InputStream in) {
        String inputStreamClassName = getInputStreamClassName(in);
        return new JSP("some-controller").withAttribute("var", inputStreamClassName);
    }
    
    @Action
    public View inputStreamWithParam(@Param("p1") String p1, @Resource InputStream in) {
        String inputStreamClassName = getInputStreamClassName(in);
        return new JSP("some-controller").withAttribute("var", p1 + "-" + inputStreamClassName);
    }
    
    private String getInputStreamClassName(InputStream in) {
        String inputStreamClassName = "null";
        if (in != null && in instanceof ServletInputStream) {
            inputStreamClassName = "ServletInputStream";
        }
        return inputStreamClassName;
    }
}
