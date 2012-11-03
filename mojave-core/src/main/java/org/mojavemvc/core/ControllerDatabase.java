/*
 * Copyright (C) 2011 Mojavemvc.org
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

import java.util.List;
import java.util.Set;

import net.sf.cglib.reflect.FastClass;

/**
 * @author Luis Antunes
 */
public interface ControllerDatabase {

    public static final String KEY = ControllerDatabase.class.getName();

    /**
     * Get the conroller Class associated with the given controller variable
     * name.
     * 
     * @param controllerVariable
     * @return the controller class associated with the variable name
     */
    public Class<?> getControllerClass(String controllerVariable);

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
    public ActionSignature getActionMethodSignature(Class<?> controllerClass, String action);

    /**
     * Get the ActionSignature annotated with @BeforeAction for the given
     * controller class. ActionSignature is thread-safe.
     * 
     * @param controllerClass
     *            the controller class
     * @return the ActionSignature, or null if there is no ActionSignature
     *         annotated with @BeforeAction
     */
    public ActionSignature getBeforeActionMethodFor(Class<?> controllerClass);

    /**
     * Get the ActionSignature annotated with @AfterAction for the given
     * controller class. ActionSignature is thread-safe.
     * 
     * @param controllerClass
     *            the controller class
     * @return the ActionSignature, or null if there is no ActionSignature
     *         annotated with @AfterAction
     */
    public ActionSignature getAfterActionMethodFor(Class<?> controllerClass);

    /**
     * Get the ActionSignature annotated with @DefaultAction for the given
     * controller class. ActionSignature is thread-safe.
     * 
     * @param controllerClass
     *            the controller class
     * @return the ActionSignature, or null if there is no method annotated with @DefaultAction
     */
    public ActionSignature getDefaultActionMethodFor(Class<?> controllerClass);

    /**
     * Get the ActionSignature annotated with @AfterConstruct for the given
     * controller class. ActionSignature is thread-safe.
     * 
     * @param controllerClass
     *            the controller class
     * @return the ActionSignature, or null if there is no ActionSignature
     *         annotated with @AfterConstruct
     */
    public ActionSignature getAfterConstructMethodFor(Class<?> controllerClass);

    /**
     * Get an unmodifiable set of singleton conroller classes that are marked
     * for creation during the FrontController init().
     * 
     * @return the singleton controller class
     */
    public Set<Class<?>> getInitControllers();

    /**
     * Get an unmodifiable list of Classes that represent interceptors for the
     * given class, in the order in which they are declared in the @InterceptedBy
     * annotation.
     * 
     * @param controllerClass
     *            the controller class
     * @return the classes of interceptors associated with the given class
     */
    public List<Class<?>> getInterceptorsFor(Class<?> controllerClass);

    /**
     * Get the ActionSignature annotated with @AfterAction for the given
     * interceptor class. ActionSignature is thread-safe.
     * 
     * @param interceptorClass
     *            the interceptor class
     * @return the ActionSignature, or null if there is no ActionSignature
     *         annotated with @AfterAction
     */
    public ActionSignature getAfterActionMethodForInterceptor(Class<?> interceptorClass);

    /**
     * Get the ActionSignature annotated with @BeforeAction for the given
     * interceptor class. Method is thread-safe.
     * 
     * @param interceptorClass
     *            the interceptor class
     * @return the ActionSignature, or null if there is no ActionSignature
     *         annotated with @BeforeAction
     */
    public ActionSignature getBeforeActionMethodForInterceptor(Class<?> interceptorClass);

    /**
     * Get the default controller for the application, if specified.
     * 
     * @return the default controller class, or null if no @DefaultController is
     *         specified
     */
    public Class<?> getDefaultControllerClass();

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
    public List<Class<?>> getInterceptorsForAction(Class<?> controllerClass, String action);

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
    public List<Class<?>> getInterceptorsForDefaultAction(Class<?> controllerClass);

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
    public List<Class<?>> getInterceptorsForHttpMethodAction(Class<?> controllerClass, HttpMethod httpMethod);

    /**
     * Get the cached FastClass version of the given controller or interceptor
     * class. FastClass is thread-safe.
     * 
     * @param clazz
     *            the controller or interceptor class
     * @return the FastClass version of the class
     */
    public FastClass getFastClass(Class<?> clazz);

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
    public ActionSignature getHttpMethodActionSignature(Class<?> controllerClass, HttpMethod httpMethod);
}
