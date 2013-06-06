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

import net.sf.cglib.reflect.FastClass;

import org.mojavemvc.exception.ConfigurationException;
import org.mojavemvc.exception.DefaultErrorHandlerFactory;
import org.mojavemvc.exception.ErrorHandlerFactory;
import org.mojavemvc.initialization.AppProperties;
import org.mojavemvc.initialization.AppPropertyCollector;
import org.mojavemvc.initialization.AppResources;
import org.mojavemvc.initialization.InitParams;
import org.mojavemvc.initialization.Initializer;
import org.mojavemvc.initialization.ModuleProvider;
import org.mojavemvc.marshalling.EntityMarshaller;
import org.mojavemvc.marshalling.JSONEntityMarshaller;
import org.mojavemvc.marshalling.PlainTextEntityMarshaller;
import org.mojavemvc.marshalling.XMLEntityMarshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * 
 * @author Luis Antunes
 */
public class FrameworkInitializer {

    private static final Logger logger = LoggerFactory.getLogger("org.mojavemvc");

    private static final String CONTROLLER_CLASSES = "controller-classes";
    private static final String GUICE_MODULES = "guice-modules";
    private static final String GUICE_MODULE_PROVIDER = "guice-module-provider";
    private static final String ERROR_HANDLER_FACTORY = "error-handler-factory";
    private static final String ENTITY_MARSHALLERS = "entity-marshallers";
    private static final String INITIALIZERS = "initializers";
    
    private static final String INTERNAL_INITIALIZER_PACKAGE = "org.mojavemvc.initialization.internal";
    
    private static final String NAMESPACE_SEPARATOR = ",";

    private final Config config;
    private final Context context;
    private final ClasspathScanner scanner;

    public FrameworkInitializer(Config config, Context context, 
            ClasspathScanner scanner) {

        this.config = config;
        this.context = context;
        this.scanner = scanner;
    }

    public void performInitialization() {

        logger.debug("performing initialization...");
        createGuiceInjector();
        processInitializers();
        createControllerDatabase();
        createErrorHandlerFactory();
    }
    
    private void createGuiceInjector() {

        logger.debug("creating Guice Injector...");

        try {

            Set<Class<? extends Module>> moduleClasses = scanModuleClasses();
            Set<Module> providedModules = getModulesFromProvider();
            AppProperties appProps = new DefaultAppProperties();
            context.setAttribute(AppProperties.KEY, appProps); 
            GuiceInitializer guiceInitializer = 
                    new GuiceInitializer(moduleClasses, providedModules, appProps);
            Injector injector = guiceInitializer.initializeInjector();
            context.setAttribute(GuiceInitializer.KEY, injector);

        } catch (Throwable e) {
            logger.error("error initializing Guice", e);
        }
    }
    
    private Set<Class<? extends Module>> scanModuleClasses() {

        String guiceModulesNamespaces = config.getInitParameter(GUICE_MODULES);
        List<String> packages = getPackagesOrEntireClasspathIfEmpty(guiceModulesNamespaces);
        return scanner.scanModules(packages);
    }
    
    private Set<Module> getModulesFromProvider() {
        
        Set<Module> modules = new HashSet<Module>();
        String moduleProviderName = config.getInitParameter(GUICE_MODULE_PROVIDER);
        if (!isEmpty(moduleProviderName)) {
            
            try {
                
                Class<?> moduleProviderClass = Class.forName(moduleProviderName);
                Constructor<?> moduleProviderConstructor = moduleProviderClass.getDeclaredConstructor();
                ModuleProvider moduleProvider = (ModuleProvider)moduleProviderConstructor.newInstance();
                
                Set<Module> providedModules = moduleProvider.getModules();
                if (providedModules != null) {
                    modules.addAll(providedModules);
                }
                
            } catch (Exception e) {
                logger.error("error invoking module provider", e);
            }
        }
        return modules;
    }

    private void processInitializers() {
        
        Set<Class<? extends Initializer>> initializers = scanInitializerClasses();
        
        DefaultAppPropertyCollector collector = new DefaultAppPropertyCollector();
        InitParams params = newInitParams();
        AppResources resources = new ServletAppResources(config.getServletContext(), 
                (Injector)context.getAttribute(GuiceInitializer.KEY));
        for (Class<? extends Initializer> initializerClass : initializers) {
            
            initialize(initializerClass, collector, resources, params);
        }
        
        /*
         * we take this approach because the Injector needs to exist
         * before we've had a chance to collect the app properties
         */
        ((DefaultAppProperties)context.getAttribute(AppProperties.KEY))
            .setProperties(collector.getProperties());
    }

    private Set<Class<? extends Initializer>> scanInitializerClasses() {
        
        List<String> packages = getInitializerPackages();
        return scanner.scanInitializers(packages);
    }
    
    private InitParams newInitParams() {
        
        Map<String, String> params = new HashMap<String, String>();
        for (Enumeration<String> en = config.getInitParameterNames(); 
                en.hasMoreElements();) {
            String name = en.nextElement();
            params.put(name, config.getInitParameter(name));
        }
        return new ServletInitParams(params);
    }
    
    private List<String> getInitializerPackages() {
        
        List<String> packages = new ArrayList<String>();
        packages.add(INTERNAL_INITIALIZER_PACKAGE);
        String initializerNamespaces = 
                config.getInitParameter(INITIALIZERS);
        if (!isEmpty(initializerNamespaces)) {
            addNamespaces(initializerNamespaces, packages);
        }
        return packages;
    }

    private void initialize(Class<? extends Initializer> initializerClass, 
            AppPropertyCollector collector, AppResources resources, InitParams params) {
        
        Injector injector = (Injector)context.getAttribute(GuiceInitializer.KEY);
        try {
            
            Initializer init = injector.getInstance(initializerClass);
            init.initialize(params, resources, collector);
            
        } catch (Exception e) {
            logger.error("error processing initializer " + 
                    initializerClass.getName(), e);
        }
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

        List<String> packages = getControllerPackages();
        Set<Class<?>> controllers = scanner.scanControllers(packages);
        if (controllers == null || controllers.isEmpty()) {
            throw new ConfigurationException("there must be at least one " +
                    "controller in the application");
        }
        return controllers;
    }

    private List<String> getControllerPackages() {
        
        String controllerClassNamespaces = 
                config.getInitParameter(CONTROLLER_CLASSES);
        List<String> packages = getPackagesOrEntireClasspathIfEmpty(controllerClassNamespaces);
        return packages;
    }
    
    private Map<String, EntityMarshaller> scanEntityMarshallers() {
        
        Map<String, EntityMarshaller> marshallerMap = new HashMap<String, EntityMarshaller>();
        
        /* place the framework marshallers in the map first so that they 
         * can be overridden by user's marshallers */
        addToEntityMarshallerMap(new PlainTextEntityMarshaller(), marshallerMap);
        addToEntityMarshallerMap(new JSONEntityMarshaller(), marshallerMap);
        addToEntityMarshallerMap(new XMLEntityMarshaller(), marshallerMap);
        
        String marshallersNamespaces = config.getInitParameter(ENTITY_MARSHALLERS);
        if (!isEmpty(marshallersNamespaces)) {
            List<String> packages = new ArrayList<String>();
            addNamespaces(marshallersNamespaces, packages);
            Set<Class<? extends EntityMarshaller>> customMarshallers = 
                    scanner.scanEntityMarshallers(packages);
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
    
    private void addToEntityMarshallerMap(Class<? extends EntityMarshaller> customMarshaller, 
            Map<String, EntityMarshaller> marshallerMap) {
        
        Injector injector = (Injector)context.getAttribute(GuiceInitializer.KEY);
        EntityMarshaller marshaller = null;
        try {
            marshaller = injector.getInstance(customMarshaller);
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
        
        String errorHandlerFactory = config.getInitParameter(ERROR_HANDLER_FACTORY);
        if (isEmpty(errorHandlerFactory)) {

            logger.debug("no " + ERROR_HANDLER_FACTORY + " init-param specified, using default...");
            
            AppProperties properties = (AppProperties)context.getAttribute(AppProperties.KEY);
            String defaultErrorHandlerFactory = (String)properties.getProperty(ErrorHandlerFactory.DEFAULT_FACTORY);
            if (isEmpty(defaultErrorHandlerFactory)) {
                defaultErrorHandlerFactory = DefaultErrorHandlerFactory.class.getName();
            }
            
            errorHandlerFactory = defaultErrorHandlerFactory;
        }
        
        logger.debug("setting " + ERROR_HANDLER_FACTORY + " to " + errorHandlerFactory);
        
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

    private List<String> getPackagesOrEntireClasspathIfEmpty(String namespaces) {
        
        List<String> packages = new ArrayList<String>();
        if (isEmpty(namespaces)) {
            /* scan the entire classpath */
            packages.add("");
        } else {
            addNamespaces(namespaces, packages);
        }
        return packages;
    }
    
    private void addNamespaces(String ns, List<String> packages) {
        
        String[] namespaces = ns.split(NAMESPACE_SEPARATOR);
        for (String namespace : namespaces) {
            if (!isEmpty(namespace)) {
                packages.add(namespace.trim());
            }
        }
    }
    
    private boolean isEmpty(String arg) {
        return arg == null || arg.trim().length() == 0;
    }
}
