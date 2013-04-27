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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.cglib.reflect.FastClass;

import org.mojavemvc.annotations.Action;
import org.mojavemvc.annotations.AfterAction;
import org.mojavemvc.annotations.AfterConstruct;
import org.mojavemvc.annotations.BeforeAction;
import org.mojavemvc.annotations.DELETEAction;
import org.mojavemvc.annotations.DefaultAction;
import org.mojavemvc.annotations.DefaultController;
import org.mojavemvc.annotations.Entity;
import org.mojavemvc.annotations.Expects;
import org.mojavemvc.annotations.GETAction;
import org.mojavemvc.annotations.HEADAction;
import org.mojavemvc.annotations.Init;
import org.mojavemvc.annotations.InterceptedBy;
import org.mojavemvc.annotations.OPTIONSAction;
import org.mojavemvc.annotations.POSTAction;
import org.mojavemvc.annotations.PUTAction;
import org.mojavemvc.annotations.ParamPath;
import org.mojavemvc.annotations.SingletonController;
import org.mojavemvc.annotations.StatefulController;
import org.mojavemvc.annotations.StatelessController;
import org.mojavemvc.annotations.TRACEAction;
import org.mojavemvc.aop.RequestContext;
import org.mojavemvc.exception.ConfigurationException;
import org.mojavemvc.marshalling.DefaultEntityMarshaller;
import org.mojavemvc.marshalling.EntityMarshaller;
import org.mojavemvc.util.ParamPathHelper;
import org.mojavemvc.views.View;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A single instance of this class is meant to exist in the front controller
 * context, to be shared by multiple threads. The only methods that can be
 * called during requests are read methods. This class is immutable once
 * created, and thus is thread-safe.
 * 
 * @author Luis Antunes
 */
public class MappedControllerDatabase implements ControllerDatabase {

    private static final Logger logger = LoggerFactory.getLogger(MappedControllerDatabase.class);

    /*
     * a map of the controller variable names to their classes (eg. "index" ->
     * org.mojavemvc.tests.IndexController)
     */
    private final Map<String, Class<?>> controllerClassesMap = new HashMap<String, Class<?>>();

    /*
     * a map of the controller classes to a map of their action names to method
     * signatures eg. org.mojavemvc.tests.IndexController -> ["some-action" ->
     * ActionSignature[ "someAction", [] ]]
     */
    private final Map<Class<?>, Map<String, ActionSignature>> controllerClassToActionMap = new HashMap<Class<?>, Map<String, ActionSignature>>();

    /*
     * a map of the controller classes to their @BeforeAction methods eg.
     * org.mojavemvc.tests.IndexController ->
     * ActionSignature["doSomethingBefore"]
     */
    private final Map<Class<?>, ActionSignature> controllerClassToBeforeActionMap = new HashMap<Class<?>, ActionSignature>();

    /*
     * a map of the controller classes to their @AfterAction methods eg.
     * org.mojavemvc.tests.IndexController ->
     * ActionSignature["doSomethingAfter"]
     */
    private final Map<Class<?>, ActionSignature> controllerClassToAfterActionMap = new HashMap<Class<?>, ActionSignature>();

    /*
     * a map of the controller classes to their @DefaultAction methods eg.
     * org.mojavemvc.tests.IndexController -> ActionSignature[ "someAction", []
     * ]]
     */
    private final Map<Class<?>, ActionSignature> controllerClassToDefaultActionMap = new HashMap<Class<?>, ActionSignature>();

    /*
     * a map of the controller classes to their @AfterConstruct methods eg.
     * org.mojavemvc.tests.IndexController -> ActionSignature["init"]
     */
    private final Map<Class<?>, ActionSignature> controllerClassToAfterConstructMap = new HashMap<Class<?>, ActionSignature>();

    /*
     * a set of singleton controllers that are marked for creation during init
     */
    private final Set<Class<?>> initControllers = new HashSet<Class<?>>();

    /*
     * the application-wide default controller class, if any, as determined by
     * the
     * 
     * @DefaultController annotation
     */
    private Class<?> defaultControllerClass = null;

    /*
     * a map of the controller classes to a collection of their interceptor
     * classes eg. org.mojavemvc.tests.IndexController -> Class[]
     */
    private final Map<Class<?>, List<Class<?>>> controllerClassToInterceptorsMap = new HashMap<Class<?>, List<Class<?>>>();

    /*
     * a map of the controller classes to a collection of its default action
     * interceptor classes eg. org.mojavemvc.tests.IndexController -> Class[]
     */
    private final Map<Class<?>, List<Class<?>>> controllerClassToDefaultActionInterceptorsMap = new HashMap<Class<?>, List<Class<?>>>();

    /*
     * a map of the interceptor classes to their @BeforeAction methods eg.
     * org.mojavemvc.tests.Interceptor -> ActionSignature["doSomethingBefore"]
     */
    private final Map<Class<?>, ActionSignature> interceptorClassToBeforeActionMap = new HashMap<Class<?>, ActionSignature>();

    /*
     * a map of the interceptor classes to their @AfterAction methods eg.
     * org.mojavemvc.tests.Interceptor -> Method["doSomethingAfter"]
     */
    private final Map<Class<?>, ActionSignature> interceptorClassToAfterActionMap = new HashMap<Class<?>, ActionSignature>();

    /*
     * a map of the controller classes to a map of their action names to
     * interceptor classes eg. org.mojavemvc.tests.IndexController ->
     * ["some-action" -> Interceptor[]]
     */
    private final Map<Class<?>, Map<String, List<Class<?>>>> controllerClassToActionInterceptorsMap = new HashMap<Class<?>, Map<String, List<Class<?>>>>();

    /*
     * a map of the controller classes to a map of their HttpMethods to
     * interceptor classes eg. org.mojavemvc.tests.IndexController ->
     * [HttpMethod.GET -> Interceptor[]]
     */
    private final Map<Class<?>, Map<HttpMethod, List<Class<?>>>> controllerClassToHttpMethodActionInterceptorsMap = new HashMap<Class<?>, Map<HttpMethod, List<Class<?>>>>();

    /*
     * a map of controller and interceptor classes to their FastClass
     * counter-part
     */
    private final Map<Class<?>, FastClass> classToFastClassMap = new HashMap<Class<?>, FastClass>();

    /*
     * a map of controller classes to a map of their HTTP methods to
     * corresponding HttpMethod Action eg. org.mojavemvc.tests.IndexController
     * -> [HttpMethod.GET - > ActionSignature]
     */
    private final Map<Class<?>, Map<HttpMethod, ActionSignature>> controllerClassToHttpMethodMap = new HashMap<Class<?>, Map<HttpMethod, ActionSignature>>();
    
    /*
     * the route map to configure during initialization
     */
    private final RouteMap routeMap;

    /*
     * the map of global entity marshallers
     * key: content type
     * val: marhsaller instance
     */
    private final Map<String, EntityMarshaller> entityMarshallerMap;
    
    /**
     * Construct a controller database based on the given Set of controller
     * Classes.
     * 
     * @param controllerClasses
     */
    public MappedControllerDatabase(Set<Class<?>> controllerClasses, RouteMap routeMap, 
            Map<String, EntityMarshaller> entityMarshallerMap) {

        this.routeMap = routeMap;
        this.entityMarshallerMap = entityMarshallerMap;
        init(controllerClasses);
    }

    /**
     * Get the RouteMap for the application.
     * 
     * @return the route map for the application
     */
    public RouteMap getRouteMap() {
        return routeMap;
    }
    
    /**
     * Get the controller Class associated with the given controller variable
     * name.
     * 
     * @param controllerVariable
     * @return the controller class associated with the variable name
     */
    public Class<?> getControllerClass(String controllerVariable) {

        return controllerClassesMap.get(controllerVariable);
    }

    /**
     * Get the ActionSignature associated with the given controller class.
     * ActionSignature is a thread-safe class.
     * 
     * @param controllerClass
     *            the controller class
     * @param action
     *            the request action
     * @return the ActionSignature associated with the controller class
     */
    public ActionSignature getActionMethodSignature(Class<?> controllerClass, String action) {

        Map<String, ActionSignature> actionMap = controllerClassToActionMap.get(controllerClass);
        if (actionMap == null) {
            throw new ConfigurationException("no actions defined for " + controllerClass.getName());
        }
        return actionMap.get(action);
    }

    /**
     * Get the ActionSignature annotated with @BeforeAction for the given
     * controller class. ActionSignature is thread-safe.
     * 
     * @param controllerClass
     *            the controller class
     * @return the ActionSignature, or null if there is no ActionSignature
     *         annotated with @BeforeAction
     */
    public ActionSignature getBeforeActionMethodFor(Class<?> controllerClass) {

        return controllerClassToBeforeActionMap.get(controllerClass);
    }

    /**
     * Get the ActionSignature annotated with @AfterAction for the given
     * controller class. ActionSignature is thread-safe.
     * 
     * @param controllerClass
     *            the controller class
     * @return the ActionSignature, or null if there is no ActionSignature
     *         annotated with @AfterAction
     */
    public ActionSignature getAfterActionMethodFor(Class<?> controllerClass) {

        return controllerClassToAfterActionMap.get(controllerClass);
    }

    /**
     * Get the ActionSignature annotated with @DefaultAction for the given
     * controller class. ActionSignature is thread-safe.
     * 
     * @param controllerClass
     *            the controller class
     * @return the ActionSignature, or null if there is no method annotated with @DefaultAction
     */
    public ActionSignature getDefaultActionMethodFor(Class<?> controllerClass) {

        return controllerClassToDefaultActionMap.get(controllerClass);
    }

    /**
     * Get the ActionSignature annotated with @AfterConstruct for the given
     * controller class. ActionSignature is thread-safe.
     * 
     * @param controllerClass
     *            the controller class
     * @return the ActionSignature, or null if there is no ActionSignature
     *         annotated with @AfterConstruct
     */
    public ActionSignature getAfterConstructMethodFor(Class<?> controllerClass) {

        return controllerClassToAfterConstructMap.get(controllerClass);
    }

    /**
     * Get an unmodifiable set of singleton controller classes that are marked
     * for creation during the FrontController init().
     * 
     * @return the singleton controller class
     */
    public Set<Class<?>> getInitControllers() {

        return Collections.unmodifiableSet(initControllers);
    }

    /**
     * Get an unmodifiable list of Classes that represent interceptors for the
     * given class, in the order in which they are declared in the @InterceptedBy
     * annotation.
     * 
     * @param controllerClass
     *            the controller class
     * @return the classes of interceptors associated with the given class
     */
    public List<Class<?>> getInterceptorsFor(Class<?> controllerClass) {

        return controllerClassToInterceptorsMap.get(controllerClass);
    }

    /**
     * Get the ActionSignature annotated with @AfterAction for the given
     * interceptor class. ActionSignature is thread-safe.
     * 
     * @param interceptorClass
     *            the interceptor class
     * @return the ActionSignature, or null if there is no ActionSignature
     *         annotated with @AfterAction
     */
    public ActionSignature getAfterActionMethodForInterceptor(Class<?> interceptorClass) {

        return interceptorClassToAfterActionMap.get(interceptorClass);
    }

    /**
     * Get the ActionSignature annotated with @BeforeAction for the given
     * interceptor class. Method is thread-safe.
     * 
     * @param interceptorClass
     *            the interceptor class
     * @return the ActionSignature, or null if there is no ActionSignature
     *         annotated with @BeforeAction
     */
    public ActionSignature getBeforeActionMethodForInterceptor(Class<?> interceptorClass) {

        return interceptorClassToBeforeActionMap.get(interceptorClass);
    }

    /**
     * Get the default controller for the application, if specified.
     * 
     * @return the default controller class, or null if no @DefaultController is
     *         specified
     */
    public Class<?> getDefaultControllerClass() {

        return defaultControllerClass;
    }

    /**
     * Get an unmodifiable list of Classes that represent interceptors for the
     * given action method in the given class, in the order in which they are
     * declared in the @InterceptedBy annotation.
     * 
     * @param controllerClass
     *            the controller class
     * @param action
     *            the action being invoked in the request
     * @return an unmodifiable List of interceptor classes in the order in which
     *         they are declared, or null if there are no interceptors
     */
    public List<Class<?>> getInterceptorsForAction(Class<?> controllerClass, String action) {

        Map<String, List<Class<?>>> actionInterceptorsMap = controllerClassToActionInterceptorsMap.get(controllerClass);
        if (actionInterceptorsMap == null) {
            return null;
        }
        return actionInterceptorsMap.get(action);
    }

    /**
     * Get an unmodifiable list of Classes that represent interceptors for the
     * default action method in the given class, in the order in which they are
     * declared in the @InterceptedBy annotation.
     * 
     * @param controllerClass
     *            the controller class
     * @return an unmodifiable List of interceptor classes in the order in which
     *         they are declared
     */
    public List<Class<?>> getInterceptorsForDefaultAction(Class<?> controllerClass) {

        return controllerClassToDefaultActionInterceptorsMap.get(controllerClass);
    }

    /**
     * Get an unmodifiable list of Classes that represent interceptors for the
     * given HTTP method action in the given class, in the order in which they
     * are declared in the @InterceptedBy annotation.
     * 
     * @param controllerClass
     *            the controller class
     * @param httpMethod
     *            the HTTP method being invoked in the request
     * @return an unmodifiable List of interceptor classes in the order in which
     *         they are declared, or null if there are no interceptors
     */
    public List<Class<?>> getInterceptorsForHttpMethodAction(Class<?> controllerClass, HttpMethod httpMethod) {

        Map<HttpMethod, List<Class<?>>> httpMethodActionInterceptorsMap = controllerClassToHttpMethodActionInterceptorsMap
                .get(controllerClass);
        if (httpMethodActionInterceptorsMap == null) {
            return null;
        }
        return httpMethodActionInterceptorsMap.get(httpMethod);
    }

    /**
     * Get the cached FastClass version of the given controller or interceptor
     * class. FastClass is thread-safe.
     * 
     * @param clazz
     *            the controller or interceptor class
     * @return the FastClass version of the class
     */
    public FastClass getFastClass(Class<?> clazz) {

        return classToFastClassMap.get(clazz);
    }

    /**
     * Get the HTTP method ActionSignature associated with the given controller
     * class. ActionSignature is a thread-safe class.
     * 
     * @param controllerClass
     *            the controller class
     * @param httpMethod
     *            the HTTP method
     * @return the ActionSignature associated with the controller class, or null
     *         if there is no Action for the given HttpMethod
     */
    public ActionSignature getHttpMethodActionSignature(Class<?> controllerClass, HttpMethod httpMethod) {

        Map<HttpMethod, ActionSignature> actionMap = controllerClassToHttpMethodMap.get(controllerClass);
        if (actionMap != null) {

            return actionMap.get(httpMethod);
        }
        return null;
    }

    /*--------------------------private methods----------------------------------------*/

    private void init(Set<Class<?>> controllerClasses) {

        for (Class<?> controllerClass : controllerClasses) {

            logger.debug("found controller class: " + controllerClass.getName());

            Annotation controllerAnnotation = getControllerAnnotation(controllerClass);

            String controllerVariable = getControllerVariable(controllerClass, controllerAnnotation);

            addControllerClass(controllerVariable, controllerClass);
        }
    }

    private Annotation getControllerAnnotation(Class<?> controllerClass) {

        Annotation[] controllerAnnotations = controllerClass.getAnnotations();
        Annotation controllerAnnotation = null;
        for (Annotation annot : controllerAnnotations) {

            if (annot instanceof StatelessController || annot instanceof StatefulController
                    || annot instanceof SingletonController) {

                if (controllerAnnotation != null) {
                    throw new ConfigurationException("controller " + controllerClass.getName()
                            + " is annotated with multiple controller annotations");
                }

                controllerAnnotation = annot;
            }
        }

        if (controllerAnnotation == null) {
            throw new ConfigurationException("controller " + controllerClass.getName()
                    + " not annotated with a controller annotation");
        }

        return controllerAnnotation;
    }

    private String getControllerVariable(Class<?> controllerClass, Annotation controllerAnnotation) {

        String controllerVariable = null;

        if (controllerAnnotation instanceof StatelessController) {
            controllerVariable = ((StatelessController) controllerAnnotation).value();
        } else if (controllerAnnotation instanceof StatefulController) {
            controllerVariable = ((StatefulController) controllerAnnotation).value();
        } else if (controllerAnnotation instanceof SingletonController) {
            controllerVariable = ((SingletonController) controllerAnnotation).value();
        }

        if (controllerVariable == null || controllerVariable.trim().length() == 0) {
            /*
             * if no value is provided in the annotation, map the controller to
             * its simple class name
             */
            controllerVariable = controllerClass.getSimpleName();
        }

        return controllerVariable;
    }

    private void addControllerClass(String controllerVariable, Class<?> controllerClass) {

        /*
         * check if this controller variable already exists; if so raise an
         * exception
         */
        Class<?> existing = controllerClassesMap.get(controllerClass);
        if (existing != null) {
            throw new ConfigurationException("a controller variable with the value " + controllerVariable
                    + " already exists");
        }
        controllerClassesMap.put(controllerVariable, controllerClass);
        checkForInitController(controllerClass);
        boolean isDefaultController = checkForDefaultController(controllerClass);
        setActionMethodIndicesFor(controllerClass, controllerVariable, isDefaultController);
        setInterceptorsFor(controllerClass);
    }

    private void checkForInitController(Class<?> controllerClass) {

        Annotation annot = controllerClass.getAnnotation(Init.class);
        if (annot != null) {
            /* do some validation */
            annot = controllerClass.getAnnotation(SingletonController.class);
            if (annot == null) {
                throw new ConfigurationException("only a @" + SingletonController.class.getSimpleName()
                        + " can be annotated with @" + Init.class.getSimpleName());
            }
            initControllers.add(controllerClass);
        }
    }

    /*
     * return true if controller class is a default controller, false otherwise
     */
    private boolean checkForDefaultController(Class<?> controllerClass) {

        Annotation annot = controllerClass.getAnnotation(DefaultController.class);
        if (annot != null) {
            if (defaultControllerClass != null) {
                throw new ConfigurationException("only one controller class can be annotated with @"
                        + DefaultController.class.getSimpleName());
            }
            defaultControllerClass = controllerClass;
            return true;
        }
        return false;
    }

    private void setActionMethodIndicesFor(Class<?> controllerClass, String controllerVariable, 
            boolean isDefaultController) {

        Map<String, ActionSignature> actionMap = new HashMap<String, ActionSignature>();

        Map<String, List<Class<?>>> actionInterceptorsMap = new HashMap<String, List<Class<?>>>();

        Map<HttpMethod, ActionSignature> httpMethodActionMap = new EnumMap<HttpMethod, ActionSignature>(
                HttpMethod.class);

        Map<HttpMethod, List<Class<?>>> httpMethodActionInterceptorsMap = new EnumMap<HttpMethod, List<Class<?>>>(
                HttpMethod.class);

        Method[] methods = getAllMethodsIn(controllerClass);
        
        FastClass fastClass = FastClass.create(controllerClass);
        classToFastClassMap.put(controllerClass, fastClass);

        for (int i = 0; i < methods.length; i++) {

            Annotation ann = methods[i].getAnnotation(Action.class);
            if (ann != null) {
                Action actionAnn = (Action) ann;
                String action = actionAnn.value();
                if (action == null || action.trim().length() == 0) {
                    /*
                     * if no value is provided in the annotation, map the action
                     * to its method name
                     */
                    action = methods[i].getName();
                }
                addActionSignature(action, methods[i], actionMap, fastClass, controllerVariable, isDefaultController);
                setInterceptorsForAction(controllerClass, action, methods[i], actionInterceptorsMap);
                continue;
            }

            if (addHttpMethodActionSignature(controllerClass, methods[i], fastClass, httpMethodActionMap,
                    httpMethodActionInterceptorsMap, controllerVariable, isDefaultController)) {
                continue;
            }

            ann = methods[i].getAnnotation(BeforeAction.class);
            if (ann != null) {
                addBeforeOrAfterActionSignature(controllerClassToBeforeActionMap, BeforeAction.class, controllerClass,
                        methods[i], fastClass);
                continue;
            }

            ann = methods[i].getAnnotation(AfterAction.class);
            if (ann != null) {
                addBeforeOrAfterActionSignature(controllerClassToAfterActionMap, AfterAction.class, controllerClass,
                        methods[i], fastClass);
                continue;
            }

            ann = methods[i].getAnnotation(DefaultAction.class);
            if (ann != null) {
                addDefaultActionSignature(controllerClassToDefaultActionMap, DefaultAction.class, controllerClass,
                        methods[i], fastClass, controllerVariable, isDefaultController);
                setInterceptorsForDefaultAction(controllerClass, methods[i]);
                continue;
            }

            ann = methods[i].getAnnotation(AfterConstruct.class);
            if (ann != null) {
                addAfterContructSignature(controllerClassToAfterConstructMap, AfterConstruct.class, controllerClass,
                        methods[i], fastClass);
            }
        }

        /*
         * validate that there is at least one action or after-construct method
         * in this controller
         */
        if (actionMap.isEmpty() && httpMethodActionMap.isEmpty()
                && controllerClassToDefaultActionMap.get(controllerClass) == null
                && controllerClassToAfterConstructMap.get(controllerClass) == null) {

            throw new ConfigurationException("controller " + controllerClass.getName()
                    + " does not contain any actions or after-construct methods");
        }

        controllerClassToActionMap.put(controllerClass, actionMap);
        controllerClassToActionInterceptorsMap.put(controllerClass, actionInterceptorsMap);
        controllerClassToHttpMethodMap.put(controllerClass, httpMethodActionMap);
        controllerClassToHttpMethodActionInterceptorsMap.put(controllerClass, httpMethodActionInterceptorsMap);
    }
    
    private Method[] getAllMethodsIn(Class<?> clazz) {
        
        List<Method> methodsList = new ArrayList<Method>();
        methodsList.addAll(Arrays.asList(clazz.getDeclaredMethods()));        
        Class<?> superClass = clazz.getSuperclass();        
        while (superClass != Object.class) {
            methodsList.addAll(Arrays.asList(superClass.getDeclaredMethods()));
            superClass = superClass.getSuperclass();
        }
        return methodsList.toArray(new Method[methodsList.size()]);
    }

    private boolean addHttpMethodActionSignature(Class<?> controllerClass, Method actionMethod, FastClass fastClass,
            Map<HttpMethod, ActionSignature> httpMethodActionMap,
            Map<HttpMethod, List<Class<?>>> httpMethodActionInterceptorsMap, 
            String controllerVariable, boolean isDefaultController) {

        boolean annotationFound = false;

        if (actionMethod.isAnnotationPresent(GETAction.class)) {

            addHttpMethodActionSignature(HttpMethod.GET, actionMethod, fastClass, 
                    httpMethodActionMap, controllerVariable, isDefaultController);
            setInterceptorsForHttpMethodAction(controllerClass, HttpMethod.GET, actionMethod,
                    httpMethodActionInterceptorsMap);
            annotationFound = true;
        }

        if (actionMethod.isAnnotationPresent(POSTAction.class)) {

            addHttpMethodActionSignature(HttpMethod.POST, actionMethod, fastClass, 
                    httpMethodActionMap, controllerVariable, isDefaultController);
            setInterceptorsForHttpMethodAction(controllerClass, HttpMethod.POST, actionMethod,
                    httpMethodActionInterceptorsMap);
            annotationFound = true;
        }

        if (actionMethod.isAnnotationPresent(PUTAction.class)) {

            addHttpMethodActionSignature(HttpMethod.PUT, actionMethod, fastClass, 
                    httpMethodActionMap, controllerVariable, isDefaultController);
            setInterceptorsForHttpMethodAction(controllerClass, HttpMethod.PUT, actionMethod,
                    httpMethodActionInterceptorsMap);
            annotationFound = true;
        }

        if (actionMethod.isAnnotationPresent(OPTIONSAction.class)) {

            addHttpMethodActionSignature(HttpMethod.OPTIONS, actionMethod, fastClass,
                    httpMethodActionMap, controllerVariable, isDefaultController);
            setInterceptorsForHttpMethodAction(controllerClass, HttpMethod.OPTIONS, actionMethod,
                    httpMethodActionInterceptorsMap);
            annotationFound = true;
        }

        if (actionMethod.isAnnotationPresent(HEADAction.class)) {

            addHttpMethodActionSignature(HttpMethod.HEAD, actionMethod, fastClass, 
                    httpMethodActionMap, controllerVariable, isDefaultController);
            setInterceptorsForHttpMethodAction(controllerClass, HttpMethod.HEAD, actionMethod,
                    httpMethodActionInterceptorsMap);
            annotationFound = true;
        }

        if (actionMethod.isAnnotationPresent(TRACEAction.class)) {

            addHttpMethodActionSignature(HttpMethod.TRACE, actionMethod, fastClass,
                    httpMethodActionMap, controllerVariable, isDefaultController);
            setInterceptorsForHttpMethodAction(controllerClass, HttpMethod.TRACE, actionMethod,
                    httpMethodActionInterceptorsMap);
            annotationFound = true;
        }

        if (actionMethod.isAnnotationPresent(DELETEAction.class)) {

            addHttpMethodActionSignature(HttpMethod.DELETE, actionMethod, fastClass,
                    httpMethodActionMap, controllerVariable, isDefaultController);
            setInterceptorsForHttpMethodAction(controllerClass, HttpMethod.DELETE, actionMethod,
                    httpMethodActionInterceptorsMap);
            annotationFound = true;
        }

        return annotationFound;
    }

    private void addHttpMethodActionSignature(HttpMethod httpMethod, Method actionMethod,
            FastClass fastClass, Map<HttpMethod, ActionSignature> httpMethodActionMap, 
            String controllerVariable, boolean isDefaultController) {

        validateActionReturnType(actionMethod, fastClass.getJavaClass().getName());

        ActionSignature existingActionSignature = httpMethodActionMap.get(httpMethod);
        if (existingActionSignature != null) {

            throw new ConfigurationException("mulitple action methods for " + httpMethod + " found; "
                    + "only one action method per HTTP method type is allowed in " + 
                    fastClass.getJavaClass().getName());
        }
        
        EntityMarshaller paramMarshaller = getParamEntityMarshaller(actionMethod, 
                fastClass.getJavaClass().getName());

        int fastIndex = fastClass.getIndex(actionMethod.getName(), actionMethod.getParameterTypes());

        ActionSignature sig = new HttpMethodActionSignature(httpMethod, fastIndex, actionMethod.getName(),
                actionMethod.getParameterTypes(), actionMethod.getParameterAnnotations(), paramMarshaller);
        httpMethodActionMap.put(httpMethod, sig);
        
        addRoute(actionMethod, fastClass.getJavaClass().getName(), controllerVariable, 
                null, isDefaultController);
    }

    private void addActionSignature(String action, Method method,
            Map<String, ActionSignature> actionMap, FastClass fastClass, 
            String controllerVariable, boolean isDefaultController) {

        validateActionReturnType(method, fastClass.getJavaClass().getName());
        
        EntityMarshaller paramMarshaller = getParamEntityMarshaller(method, fastClass.getJavaClass().getName());

        int fastIndex = fastClass.getIndex(method.getName(), method.getParameterTypes());

        ActionSignature sig = new BaseActionSignature(fastIndex, method.getName(), method.getParameterTypes(),
                method.getParameterAnnotations(), paramMarshaller);
        actionMap.put(action, sig);
        
        addRoute(method, fastClass.getJavaClass().getName(), controllerVariable, action, isDefaultController);
    }

    private EntityMarshaller getParamEntityMarshaller(Method method, String className) {
        
        EntityMarshaller paramMarshaller = new DefaultEntityMarshaller();
        Annotation[][] paramAnnotations = method.getParameterAnnotations();
        List<Entity> entityAnnotations = new ArrayList<Entity>();
        for (Annotation[] annotationsForParam : paramAnnotations) {
            for (Annotation annotation : annotationsForParam) {
                if (annotation instanceof Entity) {
                    entityAnnotations.add((Entity)annotation);
                }
            }
        }
        if (!entityAnnotations.isEmpty()) {
            
            if (entityAnnotations.size() > 1) {
                throw new ConfigurationException("action " + method.getName() + " in controller "
                        + className + " has more than one " + Entity.class.getName() + " annotation");
            }
            
            Expects expectsAnn = method.getAnnotation(Expects.class);
            if (expectsAnn == null) {
                throw new ConfigurationException("action " + method.getName() + " in controller "
                        + className + " has an " + Entity.class.getName() + " annotation, but no "
                        + Expects.class.getName() + " exists");
            }
            String contentType = expectsAnn.value();
            EntityMarshaller marshaller = entityMarshallerMap.get(contentType);
            if (marshaller != null) {
                paramMarshaller = marshaller;
            } else {
                logger.error("could not find parameter entity marshaller for content type " 
                        + contentType + " for action " + method.getName() + " in controller "
                        + className);
            }
        }
        return paramMarshaller;
    }

    private void validateActionReturnType(Method actionMethod, String className) {

        Class<?> returnType = actionMethod.getReturnType();
        if (!View.class.isAssignableFrom(returnType)) {

            throw new ConfigurationException("action " + actionMethod.getName() + " in controller "
                    + className + " must return " + View.class.getName() + " or one of its subtypes");
        }
    }
    
    private void addRoute(Method method, String controllerClassName, 
            String controllerVariable, String actionVariable, boolean isDefaultController) {
        
        String paramPath = getParamPathIfExists(method, controllerClassName);
        
        Route route = new Route(controllerVariable, actionVariable, paramPath);
        logger.debug("adding route " + route);
        routeMap.add(route);
        if (isDefaultController) {
            route = new Route(null, actionVariable, paramPath);
            logger.debug("adding route " + route);
            routeMap.add(route);
        }
    }

    private String getParamPathIfExists(Method method, String controllerName) {
        
        String paramPath = null;
        ParamPath paramPathAnn = method.getAnnotation(ParamPath.class);
        if (paramPathAnn != null) {
            paramPath = paramPathAnn.value();
            validateParamPath(paramPath, method, controllerName);
        }
        return paramPath;
    }
    
    private void validateParamPath(String paramPath, Method method, String controllerName) {
        
        if (paramPath == null || paramPath.trim().length() == 0) {
            throw new ConfigurationException("Param path for method " + method.getName() + 
                    " in controller " + controllerName + " is empty");
        }
        
        String[] params = ParamPathHelper.getParamNamesFrom(paramPath);
        String[] methodParams = ParamPathHelper.getParamNamesFrom(method);
        Arrays.sort(params);
        Arrays.sort(methodParams);
        if (!Arrays.equals(params, methodParams)) {
            throw new ConfigurationException("Param path " + paramPath + " for method " + method.getName() + 
                    " in controller " + controllerName + " does not match the method params");
        }
    }
    
    private void setInterceptorsFor(Class<?> controllerClass) {

        Annotation annot = controllerClass.getAnnotation(InterceptedBy.class);
        if (annot != null) {

            List<Class<?>> processedInterceptors = processInterceptors(annot, controllerClass);

            controllerClassToInterceptorsMap.put(controllerClass, Collections.unmodifiableList(processedInterceptors));
        }
    }

    private void setInterceptorsForAction(Class<?> controllerClass, String action, Method method,
            Map<String, List<Class<?>>> actionInterceptorsMap) {

        Annotation annot = method.getAnnotation(InterceptedBy.class);
        if (annot != null) {

            List<Class<?>> processedInterceptors = processInterceptors(annot, controllerClass, method);

            actionInterceptorsMap.put(action, Collections.unmodifiableList(processedInterceptors));
        }
    }

    private void setInterceptorsForHttpMethodAction(Class<?> controllerClass, HttpMethod httpMethod, Method method,
            Map<HttpMethod, List<Class<?>>> httpMethodActionInterceptorsMap) {

        Annotation annot = method.getAnnotation(InterceptedBy.class);
        if (annot != null) {

            List<Class<?>> processedInterceptors = processInterceptors(annot, controllerClass, method);

            httpMethodActionInterceptorsMap.put(httpMethod, Collections.unmodifiableList(processedInterceptors));
        }
    }

    private void setInterceptorsForDefaultAction(Class<?> controllerClass, Method method) {

        Annotation annot = method.getAnnotation(InterceptedBy.class);
        if (annot != null) {

            List<Class<?>> processedInterceptors = processInterceptors(annot, controllerClass, method);

            controllerClassToDefaultActionInterceptorsMap.put(controllerClass,
                    Collections.unmodifiableList(processedInterceptors));
        }
    }

    private List<Class<?>> processInterceptors(Annotation inteceptedByAnnot, Class<?> controllerClass) {

        return processInterceptors(inteceptedByAnnot, controllerClass, null);
    }

    private List<Class<?>> processInterceptors(Annotation inteceptedByAnnot, Class<?> controllerClass, Method method) {

        List<Class<?>> foundInterceptors = new ArrayList<Class<?>>();
        Class<?>[] interceptors = ((InterceptedBy) inteceptedByAnnot).value();
        for (Class<?> interceptor : interceptors) {

            if (foundInterceptors.contains(interceptor)) {

                String message = "interceptor " + interceptor.getName() + " has already been declared";
                if (method != null) {
                    message += " in " + method.getName();
                }
                message += " in " + controllerClass.getName();
                throw new ConfigurationException(message);
            }

            addMethodsForInterceptor(interceptor);

            foundInterceptors.add(interceptor);
        }
        return foundInterceptors;
    }

    private void addMethodsForInterceptor(Class<?> interceptorClass) {

        /*
         * only add the methods in this interceptor if it hasn't been already
         * processed
         */
        if (!interceptorClassToAfterActionMap.containsKey(interceptorClass)
                && !interceptorClassToBeforeActionMap.containsKey(interceptorClass)) {

            Method[] methods = getAllMethodsIn(interceptorClass);
            FastClass fastClass = FastClass.create(interceptorClass);
            classToFastClassMap.put(interceptorClass, fastClass);

            for (int i = 0; i < methods.length; i++) {

                Annotation ann = methods[i].getAnnotation(BeforeAction.class);
                if (ann != null) {
                    addBeforeOrAfterActionSignature(interceptorClassToBeforeActionMap, BeforeAction.class,
                            interceptorClass, methods[i], fastClass);
                    continue;
                }

                ann = methods[i].getAnnotation(AfterAction.class);
                if (ann != null) {
                    addBeforeOrAfterActionSignature(interceptorClassToAfterActionMap, AfterAction.class,
                            interceptorClass, methods[i], fastClass);
                }
            }

            ActionSignature before = interceptorClassToBeforeActionMap.get(interceptorClass);
            ActionSignature after = interceptorClassToAfterActionMap.get(interceptorClass);

            if (before == null && after == null) {

                throw new ConfigurationException("interceptor " + interceptorClass.getName()
                        + " must have at least a @" + BeforeAction.class.getSimpleName() + " or a @"
                        + AfterAction.class.getSimpleName());
            }
        }
    }

    private void addBeforeOrAfterActionSignature(Map<Class<?>, ActionSignature> map, Class<?> annotationClass,
            Class<?> clazz, Method method, FastClass fastClass) {

        ActionSignature m = map.get(clazz);
        if (m != null) {
            throw new ConfigurationException("there can be only one @" + annotationClass.getSimpleName()
                    + " method in " + clazz.getName());
        }
        if (method.getParameterTypes().length > 0) {
            if (method.getParameterTypes().length != 1 || !(method.getParameterTypes()[0].equals(RequestContext.class))) {
                throw new ConfigurationException("a @" + annotationClass.getSimpleName()
                        + " method cannot take arguments in " + clazz.getName());
            }
        }

        int fastIndex = fastClass.getIndex(method.getName(), method.getParameterTypes());

        ActionSignature sig = new BaseActionSignature(fastIndex, method.getName(), method.getParameterTypes(),
                new Annotation[][] {});

        map.put(clazz, sig);
    }

    private void addAfterContructSignature(Map<Class<?>, ActionSignature> map, Class<?> annotationClass,
            Class<?> controllerClass, Method method, FastClass fastClass) {

        ActionSignature m = map.get(controllerClass);
        if (m != null) {
            throw new ConfigurationException("there can be only one @" + annotationClass.getSimpleName()
                    + " method in " + controllerClass.getName());
        }
        if (method.getParameterTypes().length > 0) {
            throw new ConfigurationException("a @" + annotationClass.getSimpleName()
                    + " method cannot take arguments in " + controllerClass.getName());
        }

        int fastIndex = fastClass.getIndex(method.getName(), method.getParameterTypes());

        ActionSignature sig = new BaseActionSignature(fastIndex, method.getName(), method.getParameterTypes(),
                new Annotation[][] {});

        map.put(controllerClass, sig);
    }

    private void addDefaultActionSignature(Map<Class<?>, ActionSignature> map, Class<?> annotationClass,
            Class<?> controllerClass, Method method, FastClass fastClass, 
            String controllerVariable, boolean isDefaultController) {

        ActionSignature m = map.get(controllerClass);
        if (m != null) {
            throw new ConfigurationException("there can be only one @" + annotationClass.getSimpleName()
                    + " method in " + controllerClass.getName());
        }

        Class<?> returnType = method.getReturnType();
        if (!View.class.isAssignableFrom(returnType)) {

            throw new ConfigurationException("action " + method.getName() + " in controller "
                    + controllerClass.getName() + " must return " + View.class.getName() + " or one of its subtypes");
        }

        int fastIndex = fastClass.getIndex(method.getName(), method.getParameterTypes());

        ActionSignature sig = new DefaultActionSignature(fastIndex, method.getName(), method.getParameterTypes(),
                method.getParameterAnnotations());

        map.put(controllerClass, sig);
        
        addRoute(method, controllerClass.getName(), controllerVariable, null, isDefaultController);
    }
}
