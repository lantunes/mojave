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

import org.mojavemvc.core.ActionInvoker;
import org.mojavemvc.core.ActionResolver;
import org.mojavemvc.core.ControllerContext;
import org.mojavemvc.core.ControllerDatabase;
import org.mojavemvc.core.FrontControllerContext;
import org.mojavemvc.core.FrontControllerInitializer;
import org.mojavemvc.core.GuiceInitializer;
import org.mojavemvc.core.HttpActionInvoker;
import org.mojavemvc.core.HttpActionResolver;
import org.mojavemvc.core.HttpMethod;
import org.mojavemvc.core.HttpRequestRouter;
import org.mojavemvc.core.RequestProcessor;
import org.mojavemvc.core.RequestRouter;
import org.mojavemvc.core.RoutedRequest;
import org.mojavemvc.core.ServletResourceModule;
import org.mojavemvc.exception.ErrorHandler;
import org.mojavemvc.exception.ErrorHandlerFactory;
import org.mojavemvc.views.View;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Injector;

/**
 * The FrontController is the only servlet in the application, and handles all
 * requests that come in to the application.
 * 
 * @author Luis Antunes
 */
@SuppressWarnings("serial")
public final class FrontController extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(FrontController.class);

    private static String JSP_PATH = null;
    private static String JSP_ERROR_FILE = null;

    private ControllerContext ctx;

    /**
     * The init method gets the values of the init params from the web.xml file.
     */
    @Override
    public void init() throws ServletException {

        logger.info("initializing FrontController...");

        ctx = new FrontControllerContext();

        FrontControllerInitializer initializer = new FrontControllerInitializer(getServletConfig(), ctx);

        initializer.performInitialization();

        JSP_PATH = initializer.getJSPPath();
        JSP_ERROR_FILE = initializer.getJSPErrorFile();

        initializer.createInitControllers();
    }

    public static String getJSPPath() {

        return JSP_PATH;
    }

    public static String getJSPErrorFile() {

        return JSP_ERROR_FILE;
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

        ControllerDatabase controllerDb = (ControllerDatabase) ctx.getAttribute(ControllerDatabase.KEY);

        ErrorHandlerFactory errorHandlerFactory = (ErrorHandlerFactory) ctx.getAttribute(ErrorHandlerFactory.KEY);
        ErrorHandler errorHandler = errorHandlerFactory.createErrorHandler();

        Injector injector = (Injector) ctx.getAttribute(GuiceInitializer.KEY);

        ServletResourceModule.set(req, res);

        View view;
        try {
            
            RequestRouter router = new HttpRequestRouter(req, controllerDb.getRouteMap());
            RoutedRequest routed = router.route();
            
            ActionResolver resolver = new HttpActionResolver(ctx, req, httpMethod, controllerDb, injector);
    
            ActionInvoker invoker = new HttpActionInvoker(req, res, controllerDb, routed, injector);
    
            RequestProcessor requestProcessor = new RequestProcessor(resolver, invoker, errorHandler);
            
            view = requestProcessor.process(routed.getController(), routed.getAction());
    
            logger.debug("processed request for " + requestProcessor.getControllerClassName() + "; rendering...");

            view.render(req, res);

        } catch (Throwable e) {

            logger.error("error processing request: ", e);
            view = errorHandler.handleError(e);
            if (view != null) {
                /*
                 * we're not catching any exceptions thrown from rendering the
                 * error view
                 */
                view.render(req, res);
            }
        }
    }
}