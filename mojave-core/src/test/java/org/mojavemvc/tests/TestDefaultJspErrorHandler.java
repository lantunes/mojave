package org.mojavemvc.tests;

import static org.junit.Assert.*;

import java.lang.reflect.Field;

import org.junit.Test;
import org.mojavemvc.FrontController;
import org.mojavemvc.exception.DefaultJspErrorHandler;
import org.mojavemvc.exception.ErrorHandler;
import org.mojavemvc.views.JspView;
import org.mojavemvc.views.View;

public class TestDefaultJspErrorHandler {

    @Test
    public void handleError() throws Exception {

        String errorJsp = "error.jsp";

        Field f = FrontController.class.getDeclaredField("JSP_ERROR_FILE");
        f.setAccessible(true);
        f.set(null, errorJsp);

        ErrorHandler errorHandler = new DefaultJspErrorHandler();
        View view = errorHandler.handleError(null);

        assertTrue(view instanceof JspView);
        JspView jspView = (JspView) view;
        assertEquals(errorJsp, jspView.getJsp());
    }
}
