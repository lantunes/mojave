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
package org.mojavemvc;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mojavemvc.core.HttpMethod;
import org.mojavemvc.core.MojaveFramework;

/**
 * The FrontController uses the Mojave framework to handle
 * requests. Either this servlet or the {@link RequestFilter} must
 * be used in a Mojave Web MVC application, but not both.
 * 
 * @author Luis Antunes
 */
@SuppressWarnings("serial")
public final class FrontController extends HttpServlet {

    private MojaveFramework framework;

    /**
     * The init method gets the values of the init params from the web.xml file.
     */
    @Override
    public void init() throws ServletException {

        framework = new MojaveFramework();
        framework.init(getServletConfig());
    }

    /**
     * Overrides the HttpServlet doGet() method. Executes processRequest().
     * 
     * @param req
     *            an HttpServletRequest instance reference
     * @param res
     *            an HttpServletResponse instance reference
     */
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        processRequest(req, res, HttpMethod.GET);
    }

    /**
     * Overrides the HttpServlet doPost() method. Executes processRequest().
     * 
     * @param req
     *            an HttpServletRequest instance reference
     * @param res
     *            an HttpServletResponse instance reference
     */
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        processRequest(req, res, HttpMethod.POST);
    }

    /**
     * Overrides the HttpServlet doPut() method. Executes processRequest().
     * 
     * @param req
     *            an HttpServletRequest instance reference
     * @param res
     *            an HttpServletResponse instance reference
     */
    @Override
    public void doPut(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        processRequest(req, res, HttpMethod.PUT);
    }

    /**
     * Overrides the HttpServlet doDelete() method. Executes processRequest().
     * 
     * @param req
     *            an HttpServletRequest instance reference
     * @param res
     *            an HttpServletResponse instance reference
     */
    @Override
    public void doDelete(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        processRequest(req, res, HttpMethod.DELETE);
    }

    /**
     * Overrides the HttpServlet doHead() method. Executes processRequest().
     * 
     * @param req
     *            an HttpServletRequest instance reference
     * @param res
     *            an HttpServletResponse instance reference
     */
    @Override
    public void doHead(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        processRequest(req, res, HttpMethod.HEAD);
    }

    /**
     * Overrides the HttpServlet doTrace() method. Executes processRequest().
     * 
     * @param req
     *            an HttpServletRequest instance reference
     * @param res
     *            an HttpServletResponse instance reference
     */
    @Override
    public void doTrace(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        processRequest(req, res, HttpMethod.TRACE);
    }

    /**
     * Overrides the HttpServlet doOptions() method. Executes processRequest().
     * 
     * @param req
     *            an HttpServletRequest instance reference
     * @param res
     *            an HttpServletResponse instance reference
     */
    @Override
    public void doOptions(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        processRequest(req, res, HttpMethod.OPTIONS);
    }

    /**
     * 
     * @param req
     *            an HttpServletRequest instance reference
     * @param res
     *            an HttpServletResponse instance reference
     * @throws ServletException
     * @throws IOException
     */
    protected void processRequest(HttpServletRequest req, HttpServletResponse res, HttpMethod httpMethod)
            throws ServletException, IOException {

        framework.handleRequest(req, res, httpMethod, undecodedPathInfo(req));
    }
    
    private String undecodedPathInfo(HttpServletRequest req) {
        
        final String pathSeparator = "/";
        final String contextPath = req.getContextPath();
        final String servletPath = req.getServletPath();
        
        String pathInfo = req.getRequestURI();
        if (contextPath.trim().length() > 0 && !contextPath.equals(pathSeparator)) {
            pathInfo = pathInfo.substring(contextPath.length());
        }
        if (servletPath.trim().length() > 0 && !servletPath.equals(pathSeparator)) {
            pathInfo = pathInfo.substring(servletPath.length());
        }
        
        if (!pathInfo.startsWith(pathSeparator)) {
            pathInfo = pathSeparator + pathInfo;
        }
        
        return pathInfo;
    }
}