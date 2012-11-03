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
package org.mojavemvc.core;

import java.util.Set;

import javax.servlet.ServletConfig;

import net.sf.cglib.reflect.FastClass;

import org.mojavemvc.annotations.SingletonController;
import org.mojavemvc.annotations.StatefulController;
import org.mojavemvc.annotations.StatelessController;
import org.mojavemvc.exception.ConfigurationException;
import org.mojavemvc.exception.DefaultErrorHandlerFactory;
import org.mojavemvc.exception.ErrorHandlerFactory;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;

/**
 * 
 * @author Luis Antunes
 */
public class FrontControllerInitializer {

    private static final Logger logger = LoggerFactory.getLogger(FrontControllerInitializer.class);

    private static final String CONTROLLER_CLASS_NAMESPACE = "controller-classes";
    private static final String JSP_PATH = "jsp-path";
    private static final String JSP_ERROR_FILE = "jsp-error-file";
    private static final String CONTROLLER_VARIABLE = "controller-variable";
    private static final String ACTION_VARIABLE = "action-variable";
    private static final String GUICE_MODULES = "guice-modules";
    private static final String ERROR_HANDLER_FACTORY = "error-handler-factory";

    private String controllerClassNamespace;
    private String controllerVariable;
    private String actionVariable;
    private String jspPath;
    private String jspErrorFile;
    private String guiceModulesNamespace;
    private String errorHandlerFactory;

    private final ServletConfig servletConfig;
    private final ControllerContext context;

    public FrontControllerInitializer(ServletConfig servletConfig, ControllerContext context) {

        this.servletConfig = servletConfig;
        this.context = context;
    }

    public void performInitialization() {

        readInitParams();
        createGuiceInjector();
        createControllerDatabase();
        createErrorHandlerFactory();
    }

    private void readInitParams() {

        logger.debug("reading init parameters...");
        readControllerClassNamespace();
        readJspPath();
        readJspErrorFile();
        readControllerVariable();
        readActionVariable();
        readGuiceModulesNamespace();
        readErrorHandlerFactory();
    }

    private void readControllerClassNamespace() {

        controllerClassNamespace = servletConfig.getInitParameter(CONTROLLER_CLASS_NAMESPACE);
        if (isEmpty(controllerClassNamespace)) {

            throw new ConfigurationException("controller class namespace must be specified in " + "web.xml as servlet "
                    + CONTROLLER_CLASS_NAMESPACE + " init-param.");
        }
    }

    private void readJspPath() {

        jspPath = servletConfig.getInitParameter(JSP_PATH);
        if (!isEmpty(jspPath)) {

            jspPath = processContextPath(jspPath);
            logger.debug("setting " + JSP_PATH + " to " + jspPath);

        } else {

            jspPath = "";
            logger.debug("no " + JSP_PATH + " init-param specified; setting to \"\"");
        }
    }

    private void readJspErrorFile() {

        jspErrorFile = servletConfig.getInitParameter(JSP_ERROR_FILE);
        if (!isEmpty(jspErrorFile)) {

            logger.debug("setting " + JSP_ERROR_FILE + " to " + jspErrorFile);

        } else {

            jspErrorFile = "error.jsp";
            logger.debug("no " + JSP_ERROR_FILE + " init-param specified; setting to " + jspErrorFile);
        }
    }

    private void readControllerVariable() {

        controllerVariable = servletConfig.getInitParameter(CONTROLLER_VARIABLE);
        if (!isEmpty(controllerVariable)) {

            logger.debug("setting " + CONTROLLER_VARIABLE + " to " + controllerVariable);

        } else {

            controllerVariable = "cntrl";
            logger.debug("no " + CONTROLLER_VARIABLE + " init-param specified; setting to " + controllerVariable);
        }
    }

    private void readActionVariable() {

        actionVariable = servletConfig.getInitParameter(ACTION_VARIABLE);
        if (!isEmpty(actionVariable)) {

            logger.debug("setting " + ACTION_VARIABLE + " to " + actionVariable);

        } else {

            actionVariable = "actn";
            logger.debug("no " + ACTION_VARIABLE + " init-param specified; setting to " + actionVariable);
        }
    }

    private void readGuiceModulesNamespace() {

        guiceModulesNamespace = servletConfig.getInitParameter(GUICE_MODULES);
    }

    private void readErrorHandlerFactory() {

        errorHandlerFactory = servletConfig.getInitParameter(ERROR_HANDLER_FACTORY);
        if (!isEmpty(errorHandlerFactory)) {

            logger.debug("setting " + ERROR_HANDLER_FACTORY + " to " + errorHandlerFactory);

        } else {

            String defaultErrorHandlerFactory = DefaultErrorHandlerFactory.class.getName();

            logger.debug("no " + ERROR_HANDLER_FACTORY + " init-param specified; setting to "
                    + defaultErrorHandlerFactory);

            errorHandlerFactory = defaultErrorHandlerFactory;
        }
    }

    private void createGuiceInjector() {

        logger.debug("creating Guice Injector...");

        try {

            Set<Class<? extends AbstractModule>> moduleClasses = scanModuleClasses();
            GuiceInitializer guiceInitializer = new GuiceInitializer(moduleClasses);
            Injector injector = guiceInitializer.initializeInjector();
            context.setAttribute(GuiceInitializer.KEY, injector);

        } catch (Throwable e) {
            logger.error("error initializing Guice", e);
        }
    }

    private void createControllerDatabase() {

        logger.debug("creating ControllerDatabase...");

        try {

            Set<Class<?>> controllerClasses = scanControllerClasses();
            ControllerDatabase controllerDatabase = new MappedControllerDatabase(controllerClasses);
            context.setAttribute(ControllerDatabase.KEY, controllerDatabase);

        } catch (Throwable e) {
            logger.error("error creating ControllerDatabase", e);
        }
    }

    private Set<Class<?>> scanControllerClasses() {

        Reflections reflections = new Reflections(controllerClassNamespace);
        Set<Class<?>> controllerClasses = reflections.getTypesAnnotatedWith(StatelessController.class);
        controllerClasses.addAll(reflections.getTypesAnnotatedWith(StatefulController.class));
        controllerClasses.addAll(reflections.getTypesAnnotatedWith(SingletonController.class));
        return controllerClasses;
    }

    private Set<Class<? extends AbstractModule>> scanModuleClasses() {

        Reflections reflections = new Reflections(guiceModulesNamespace);
        Set<Class<? extends AbstractModule>> moduleClasses = reflections.getSubTypesOf(AbstractModule.class);

        return moduleClasses;
    }

    private String processContextPath(String path) {

        if (path == null || path.trim().length() == 0) {
            return path;
        }
        if (path.charAt(path.length() - 1) != '/') {
            path += "/";
        }
        return path;
    }

    private void createErrorHandlerFactory() {

        logger.debug("creating error handler factory - " + errorHandlerFactory + " ...");

        try {

            Class<?> errorHandlerFactoryClass = Class.forName(errorHandlerFactory);
            FastClass errorHandlerFactoryFastClass = FastClass.create(errorHandlerFactoryClass);
            ErrorHandlerFactory errorHandlerFactory = (ErrorHandlerFactory) errorHandlerFactoryFastClass.newInstance();

            context.setAttribute(ErrorHandlerFactory.KEY, errorHandlerFactory);

        } catch (Throwable e) {

            logger.error("error creating error handler factory", e);
        }
    }

    public void createInitControllers() {

        ControllerDatabase controllerDb = (ControllerDatabase) context.getAttribute(ControllerDatabase.KEY);
        Set<Class<?>> initControllers = controllerDb.getInitControllers();

        if (initControllers != null && !initControllers.isEmpty()) {

            logger.debug("creating init controllers...");

            Injector injectorInstance = (Injector) context.getAttribute(GuiceInitializer.KEY);

            try {

                for (Class<?> controllerClass : initControllers) {

                    /* create the controller instance */
                    Object actionController = injectorInstance.getInstance(controllerClass);

                    /* invoke the after construct method if present */
                    ActionSignature afterConstructSig = controllerDb.getAfterConstructMethodFor(controllerClass);
                    if (afterConstructSig != null) {
                        /*
                         * we've already validated that there are no method
                         * parameters when creating the controller database
                         */
                        FastClass actionFastClass = controllerDb.getFastClass(controllerClass);
                        actionFastClass.invoke(afterConstructSig.fastIndex(), actionController, new Object[] {});
                        logger.debug("invoked after construct action for " + controllerClass.getName());
                    }

                    /* add this singleton controller to the servlet context */
                    context.setAttribute(controllerClass.getName(), actionController);
                }

            } catch (Throwable e) {
                logger.error("error creating Init controllers", e);
            }
        }
    }

    private boolean isEmpty(String arg) {
        return arg == null || arg.trim().length() == 0;
    }

    public String getControllerVariable() {
        return controllerVariable;
    }

    public String getActionVariable() {
        return actionVariable;
    }

    public String getJspPath() {
        return jspPath;
    }

    public String getJspErrorFile() {
        return jspErrorFile;
    }
}
