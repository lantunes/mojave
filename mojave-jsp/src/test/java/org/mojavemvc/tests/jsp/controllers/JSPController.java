/*
 * Copyright (C) 2011-2013 Mojavemvc.org
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
package org.mojavemvc.tests.jsp.controllers;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mojavemvc.annotations.Action;
import org.mojavemvc.annotations.Param;
import org.mojavemvc.annotations.StatelessController;
import org.mojavemvc.initialization.AppProperties;
import org.mojavemvc.views.JSP;
import org.mojavemvc.views.View;

/**
 * @author Luis Antunes
 */
@StatelessController("jsp")
public class JSPController {

    @Action("basic/:val")
    public View basic(@Param("val") String val) {
        
        return new JSP("basic").withAttribute("val", val);
    }
    
    @Action("include")
    public View include() {

        return new View() {
            @Override
            public void render(HttpServletRequest request, 
                    HttpServletResponse response, AppProperties properties)
                    throws ServletException, IOException {

                String jspPath = (String) properties.getProperty(JSP.PATH_PROPERTY);
                request.setAttribute("val", "Included");
                RequestDispatcher dispatcher = 
                        request.getRequestDispatcher(jspPath + "basic.jsp");
                dispatcher.include(request, response);
            }
        };
    }
}
