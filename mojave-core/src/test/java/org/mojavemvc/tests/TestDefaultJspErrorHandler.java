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
package org.mojavemvc.tests;

import static org.junit.Assert.*;

import java.lang.reflect.Field;

import org.junit.Test;
import org.mojavemvc.FrontController;
import org.mojavemvc.exception.DefaultJSPErrorHandler;
import org.mojavemvc.exception.ErrorHandler;
import org.mojavemvc.views.JSP;
import org.mojavemvc.views.View;

public class TestDefaultJSPErrorHandler {

    @Test
    public void handleError() throws Exception {

        String errorJsp = "error.jsp";

        Field f = FrontController.class.getDeclaredField("JSP_ERROR_FILE");
        f.setAccessible(true);
        f.set(null, errorJsp);

        ErrorHandler errorHandler = new DefaultJSPErrorHandler();
        View view = errorHandler.handleError(null);

        assertTrue(view instanceof JSP);
        JSP jspView = (JSP) view;
        assertEquals(errorJsp, jspView.getJSPName());
    }
}
