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

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletConfig;

import net.sf.cglib.reflect.FastClass;

import org.mojavemvc.annotations.SingletonController;
import org.mojavemvc.annotations.StatefulController;
import org.mojavemvc.annotations.StatelessController;
import org.mojavemvc.exception.ConfigurationException;
import org.mojavemvc.exception.DefaultErrorHandlerFactory;
import org.mojavemvc.exception.DefaultJSPErrorHandler;
import org.mojavemvc.exception.DefaultJSPErrorHandlerFactory;
import org.mojavemvc.exception.ErrorHandlerFactory;
import org.mojavemvc.initialization.AppProperties;
import org.mojavemvc.initialization.AppPropertyCollector;
import org.mojavemvc.initialization.AppResources;
import org.mojavemvc.initialization.InitParams;
import org.mojavemvc.initialization.Initializer;
import org.mojavemvc.marshalling.EntityMarshaller;
import org.mojavemvc.marshalling.JSONEntityMarshaller;
import org.mojavemvc.marshalling.PlainTextEntityMarshaller;
import org.mojavemvc.marshalling.XMLEntityMarshaller;
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

    private static final Logger logger = LoggerFactory.getLogger("org.mojavemvc");

    private static final String CONTROLLER_CLASS_NAMESPACE = "controller-classes";
    private static final String GUICE_MODULES = "guice-modules";
    private static final String ERROR_HANDLER_FACTORY = "error-handler-factory";
    private static final String ENTITY_MARSHALLERS = "entity-marshallers";
    private static final String INITIALIZERS_NAMESPACE = "initializers";
    
    private static final String INTERNAL_INITIALIZER_PACKAGE = "org.mojavemvc.initialization.internal";

    private final ServletConfig servletConfig;
    private final ControllerContext context;

    public FrontControllerInitializer(ServletConfig servletConfig, ControllerContext context) {

        this.servletConfig = servletConfig;
        this.context = context;
    }

    public void performInitialization() {

        logger.debug("performing initialization...");
        processInitializers();
        createGuiceInjector();
        createControllerDatabase();
        createErrorHandlerFactory();
    }

    private void processInitializers() {
        
        Set<Class<? extends Initializer>> initializers = scanInitializerClasses();
        
        DefaultAppPropertyCollector collector = new DefaultAppPropertyCollector();
        InitParams params = newInitParams();
        AppResources resources = new ServletAppResources(servletConfig.getServletContext());
        for (Class<? extends Initializer> initializerClass : initializers) {
            
            initialize(initializerClass, collector, resources, params);
        }
        
        context.setAttribute(AppProperties.KEY, 
                new DefaultAppProperties(collector.getProperties()));
    }

    private Set<Class<? extends Initializer>> scanInitializerClasses() {
        
        List<String> packages = getInitializerPackages();
        
        Reflections reflections = new Reflections(packages.toArray());
        Set<Class<? extends Initializer>> initializers = 
                reflections.getSubTypesOf(Initializer.class);
        return initializers;
    }
    
    @SuppressWarnings("unchecked")
    private InitParams newInitParams() {
        
        Map<String, String> params = new HashMap<String, String>();
        for (Enumeration<String> en = servletConfig.getInitParameterNames(); 
                en.hasMoreElements();) {
            String name = en.nextElement();
            params.put(name, servletConfig.getInitParameter(name));
        }
        return new ServletInitParams(params);
    }
    
    private List<String> getInitializerPackages() {
        
        List<String> packages = new ArrayList<String>();
        packages.add(INTERNAL_INITIALIZER_PACKAGE);
        String initializerNamespace = 
                servletConfig.getInitParameter(INITIALIZERS_NAMESPACE);
        if (!isEmpty(initializerNamespace)) {
            packages.add(initializerNamespace);
        }
        return packages;
    }
    
    private void initialize(Class<? extends Initializer> initializerClass, 
            AppPropertyCollector collector, AppResources resources, InitParams params) {
        
        try {
            
            Constructor<? extends Initializer> constructor = 
                    initializerClass.getConstructor();
            Initializer init = constructor.newInstance();
            init.initialize(params, resources, collector);
            
        } catch (Exception e) {
            logger.error("error processing initializer " + 
                    initializerClass.getName(), e);
        }
    }
    
    private void createGuiceInjector() {

        logger.debug("creating Guice Injector...");

        try {

            Set<Class<? extends AbstractModule>> moduleClasses = scanModuleClasses();
            AppProperties appProps = (AppProperties)context.getAttribute(AppProperties.KEY);
            GuiceInitializer guiceInitializer = new GuiceInitializer(moduleClasses, appProps);
            Injector injector = guiceInitializer.initializeInjector();
            context.setAttribute(GuiceInitializer.KEY, injector);

        } catch (Throwable e) {
            logger.error("error initializing Guice", e);
        }
    }
    
    private Set<Class<? extends AbstractModule>> scanModuleClasses() {

        String guiceModulesNamespace = servletConfig.getInitParameter(GUICE_MODULES);
        if (!isEmpty(guiceModulesNamespace)) {
            Reflections reflections = new Reflections(guiceModulesNamespace);
            return reflections.getSubTypesOf(AbstractModule.class);
        }
        return new HashSet<Class<? extends AbstractModule>>();
    }

    private void createControllerDatabase() {

        logger.debug("creating ControllerDatabase...");

        try {

            Set<Class<?>> controllerClasses = scanControllerClasses();
            Map<String, EntityMarshaller> entityMarshallers = scanEntityMarshallers();
            ControllerDatabase controllerDatabase = 
                    new MappedControllerDatabase(controllerClasses, new RegexRouteMap(), 
                            entityMarshallers);
            context.setAttribute(ControllerDatabase.KEY, controllerDatabase);

        } catch (Throwable e) {
            logger.error("error creating ControllerDatabase", e);
        }
    }

    private Set<Class<?>> scanControllerClasses() {

        String controllerClassNamespace = getControllerClassNameSpace();
        Reflections reflections = new Reflections(controllerClassNamespace);
        Set<Class<?>> controllerClasses = reflections.getTypesAnnotatedWith(StatelessController.class);
        controllerClasses.addAll(reflections.getTypesAnnotatedWith(StatefulController.class));
        controllerClasses.addAll(reflections.getTypesAnnotatedWith(SingletonController.class));
        return controllerClasses;
    }
    
    private String getControllerClassNameSpace() {
        
        String controllerClassNamespace = 
                servletConfig.getInitParameter(CONTROLLER_CLASS_NAMESPACE);
        if (isEmpty(controllerClassNamespace)) {

            throw new ConfigurationException("controller class namespace " +
                    "must be specified in " + "web.xml as servlet "
                    + CONTROLLER_CLASS_NAMESPACE + " init-param.");
        }
        
        return controllerClassNamespace;
    }
    
    private Map<String, EntityMarshaller> scanEntityMarshallers() {
        
        Map<String, EntityMarshaller> marshallerMap = new HashMap<String, EntityMarshaller>();
        
        /* place the framework marshallers in the map first so that they 
         * can be overridden by user's marshallers */
        addToEntityMarshallerMap(new PlainTextEntityMarshaller(), marshallerMap);
        addToEntityMarshallerMap(new JSONEntityMarshaller(), marshallerMap);
        addToEntityMarshallerMap(new XMLEntityMarshaller(), marshallerMap);
        
        String marshallersNamespace = servletConfig.getInitParameter(ENTITY_MARSHALLERS);
        if (!isEmpty(marshallersNamespace)) {
            Set<Class<? extends EntityMarshaller>> customMarshallers = 
                    scanEntityMarshallerClasses(marshallersNamespace);
            for (Class<? extends EntityMarshaller> customMarshaller : customMarshallers) {
                addToEntityMarshallerMap(customMarshaller, marshallerMap);
            }
        }
        
        return marshallerMap;
    }

    private void addToEntityMarshallerMap(EntityMarshaller marshaller, 
            Map<String, EntityMarshaller> marshallerMap) {
        
        for (String contentType : marshaller.contentTypesHandled()) {
            marshallerMap.put(contentType, marshaller);
        }
    }
    
    private Set<Class<? extends EntityMarshaller>> scanEntityMarshallerClasses(String namespace) {
        
        Reflections reflections = new Reflections(namespace);
        Set<Class<? extends EntityMarshaller>> customMarshallers = 
                reflections.getSubTypesOf(EntityMarshaller.class);
        return customMarshallers;
    }
    
    private void addToEntityMarshallerMap(Class<? extends EntityMarshaller> customMarshaller, 
            Map<String, EntityMarshaller> marshallerMap) {
        
        EntityMarshaller marshaller = null;
        try {
            Constructor<? extends EntityMarshaller> constructor = customMarshaller.getConstructor();
            marshaller = constructor.newInstance();
        } catch (Exception e) {
            logger.error("error contructing entity marshaller " + customMarshaller.getName(), e);
        }
        if (marshaller != null) {
            addToEntityMarshallerMap(marshaller, marshallerMap);
        }
    }

    private void createErrorHandlerFactory() {

        String errorHandlerFactoryName = getErrorHandlerFactoryName();

        logger.debug("creating error handler factory - " + errorHandlerFactoryName + " ...");
        
        try {

            Class<?> errorHandlerFactoryClass = Class.forName(errorHandlerFactoryName);
            FastClass errorHandlerFactoryFastClass = FastClass.create(errorHandlerFactoryClass);
            ErrorHandlerFactory errorHandlerFactory = (ErrorHandlerFactory) errorHandlerFactoryFastClass.newInstance();

            context.setAttribute(ErrorHandlerFactory.KEY, errorHandlerFactory);

        } catch (Throwable e) {

            logger.error("error creating error handler factory", e);
        }
    }
    
    private String getErrorHandlerFactoryName() {
        
        String errorHandlerFactory = servletConfig.getInitParameter(ERROR_HANDLER_FACTORY);
        if (!isEmpty(errorHandlerFactory)) {

            logger.debug("setting " + ERROR_HANDLER_FACTORY + " to " + errorHandlerFactory);

        } else {

            AppProperties properties = (AppProperties)context.getAttribute(AppProperties.KEY);
            String jspErrorFile = (String)properties.getProperty(DefaultJSPErrorHandler.JSP_ERROR_FILE_PROPERTY);
            
            String defaultErrorHandlerFactory = (!isEmpty(jspErrorFile)) ? 
                    DefaultJSPErrorHandlerFactory.class.getName() :
                    DefaultErrorHandlerFactory.class.getName();

            logger.debug("no " + ERROR_HANDLER_FACTORY + " init-param specified; setting to "
                    + defaultErrorHandlerFactory);

            errorHandlerFactory = defaultErrorHandlerFactory;
        }
        
        return errorHandlerFactory;
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
}
