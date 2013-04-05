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
package org.mojavemvc.views;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 * Represents the renderable view in the model-view-controller pattern. Users
 * can create their own renderable views by implementing this class, and
 * returning a concrete instance in an action method.
 * </p>
 * 
 * @author Luis Antunes
 */
public interface View {

    /**
     * <p>
     * This method is invoked last in the sequence of steps involved in
     * processing a request sent to the FrontController. The servlet request can
     * be used to set any attributes, then to obtain a RequestDispatcher for
     * forwarding or including, or the servlet response may be used to send a
     * redirect, for example.
     * </p>
     * 
     * <p>
     * Users should note that any exceptions thrown from this method are not
     * guaranteed to be caught in the FrontController. They may escape to the
     * requestor. Users should handle any exceptions inside the implementation
     * of this method if different behaviour is desired.
     * </p>
     * 
     * @param request
     *            the servlet request
     * @param response
     *            the servlet response
     * @throws ServletException
     * @throws IOException
     */
    void render(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;
}
