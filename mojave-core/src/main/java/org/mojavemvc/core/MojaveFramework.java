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
package org.mojavemvc.core;

import java.io.IOException;

import javax.servlet.FilterConfig;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mojavemvc.exception.ErrorHandler;
import org.mojavemvc.exception.ErrorHandlerFactory;
import org.mojavemvc.initialization.AppProperties;
import org.mojavemvc.views.View;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Injector;

/**
 * @author Luis Antunes
 */
public class MojaveFramework {

    private static final Logger logger = LoggerFactory.getLogger("org.mojavemvc");

    /*
     * NOTE: In distributed environments like Google App Engine, one
     * should not use the ServletContext to store global information.
     * This applies here, to the Context. Singleton controllers are 
     * stored in the context, so their use must be re-considered
     * when deploying to a distributed environment.
     */
    private Context ctx;
    
    public void init(FilterConfig config) {
        
        init(new FilterBasedConfig(config));
    }
    
    public void init(ServletConfig config) {
        
        init(new ServletBasedConfig(config));
    }
    
    private void init(Config config) {
        
        logger.info("initializing Mojave framework...");

        ctx = new FrameworkContext();

        FrameworkInitializer initializer = 
                new FrameworkInitializer(config, ctx, new ReflectionsClasspathScanner());

        initializer.performInitialization();

        initializer.createInitControllers();
    }
    
    public void handleRequest(HttpServletRequest req, HttpServletResponse res, HttpMethod httpMethod, String path)
            throws ServletException, IOException {
        
        ControllerDatabase controllerDb = (ControllerDatabase) ctx.getAttribute(ControllerDatabase.KEY);

        ErrorHandlerFactory errorHandlerFactory = (ErrorHandlerFactory) ctx.getAttribute(ErrorHandlerFactory.KEY);
        ErrorHandler errorHandler = errorHandlerFactory.createErrorHandler();

        Injector injector = (Injector) ctx.getAttribute(GuiceInitializer.KEY);
        AppProperties properties = (AppProperties) ctx.getAttribute(AppProperties.KEY);

        ServletResourceModule.set(req, res);

        View view;
        try {
            
            RequestRouter router = new HttpRequestRouter(path, 
                    new HttpParameterMapSource(req), controllerDb.getRouteMap());
            
            RoutedRequest routed = router.route();
            
            ActionResolver resolver = new HttpActionResolver(ctx, req, httpMethod, controllerDb, injector);
    
            ActionInvoker invoker = new HttpActionInvoker(req, res, controllerDb, routed, injector);
    
            RequestProcessor requestProcessor = new RequestProcessor(resolver, invoker, errorHandler);
            
            view = requestProcessor.process(routed.getController(), routed.getAction(), properties);
    
            logger.debug("processed request for " + requestProcessor.getControllerClassName() + "; rendering...");

            view.render(req, res, properties);

        } catch (Throwable e) {

            logger.error("error processing request: ", e);
            view = errorHandler.handleError(e, properties);
            if (view != null) {
                /*
                 * we're not catching any exceptions thrown from rendering the
                 * error view
                 */
                view.render(req, res, properties);
            }
        }
    }
}
