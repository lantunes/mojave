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
package org.mojavemvc.tests.controllers;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mojavemvc.annotations.Action;
import org.mojavemvc.annotations.BeforeAction;
import org.mojavemvc.annotations.StatelessController;
import org.mojavemvc.initialization.AppProperties;
import org.mojavemvc.views.JSP;
import org.mojavemvc.views.View;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * 
 * @author Luis Antunes
 */
@StatelessController("dispatching")
public class DispatchingController {

    private static final Logger logger = LoggerFactory.getLogger(StreamController3.class);

    @Inject
    HttpServletRequest request;

    @Inject
    HttpServletResponse response;
    
    @Inject
    AppProperties appProperties;

    @BeforeAction
    public void before() {

        try {

            request.setAttribute("var", "dispatched");
            String jspPath = (String)appProperties.getProperty(JSP.PATH_PROPERTY);
            RequestDispatcher dispatcher = request.getRequestDispatcher(jspPath + "param.jsp");
            dispatcher.forward(request, response);

        } catch (Exception e) {
            logger.error("error", e);
        }
    }

    @Action
    public View doSomething() {

        return new JSP("param").withAttribute("var", "streaming3");
    }
}
