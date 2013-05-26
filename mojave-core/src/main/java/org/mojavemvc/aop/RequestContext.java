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
package org.mojavemvc.aop;

import java.lang.annotation.Annotation;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 * Supplied as an optional parameter to &#064;BeforeAction and &#064;AfterAction
 * methods.
 * </p>
 * 
 * <p>
 * An instance of this class can be made available to intercepting action
 * methods, simply by writing the method signature with it as a parameter. It
 * contains a reference to the servlet request and response objects, as well as
 * to the Java parameters sent in the request, and to the String names of the
 * action and controller being invoked in this request.
 * </p>
 * 
 * <p>
 * For example, the following method demonstrates its usage:
 * </p>
 * 
 * <pre>
 * &#064;BeforeAction
 * public void doSomethingBefore(RequestContext ctx) {
 *     // do something
 * }
 * </pre>
 * 
 * <p>
 * Developers should note that while all the fields are declared final, the
 * class is not completely immutable, as it exports state through the request,
 * response, and paramters objects. When an accessor is called for one of these
 * fields, a reference to the actual object, not to a copy, is returned.
 * </p>
 * 
 * @author Luis Antunes
 */
public class RequestContext {

    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final Object[] parameters;
    private final String action;
    private final String controller;
    private final Annotation[] actionAnnotations;

    public RequestContext(HttpServletRequest request, HttpServletResponse response, 
            Object[] parameters, String action, String controller, 
            Annotation[] actionAnnotations) {

        this.request = request;
        this.response = response;
        this.parameters = parameters;
        this.action = action;
        this.controller = controller;
        this.actionAnnotations = actionAnnotations;
    }

    /**
     * Gets the HttpServletRequest associated with the request.
     * 
     * @return the HttpServletRequest associated with the request
     */
    public HttpServletRequest getRequest() {
        return request;
    }

    /**
     * Gets the HttpServletResponse associated with the request.
     * 
     * @return the HttpServletResponse associated with the request
     */
    public HttpServletResponse getResponse() {
        return response;
    }

    /**
     * Gets the parameters associated with the request, as their Java types as
     * defined in the Action signature.
     * 
     * @return the parameters associated with the request
     */
    public Object[] getParameters() {
        return parameters;
    }

    /**
     * Gets the action invoked for this request, or an empty String if the
     * default action was invoked.
     * 
     * @return the action parameter, or an empty String if the default action
     *         was invoked
     */
    public String getAction() {
        return action;
    }

    /**
     * Gets the controller invoked for this request, or an empty String if the
     * default controller was invoked.
     * 
     * @return the controller parameter, or an empty String if the default
     *         controller was invoked
     */
    public String getController() {
        return controller;
    }
    
    /**
     * Gets the annotations present on the action invoked for this
     * request, or an empty array if there are no annotations.
     * 
     * @return the annotations present on the action method
     */
    public Annotation[] getActionAnnotations() {
        
        return actionAnnotations;
    }
    
    /**
     * Gets the annotation specified, or null if it does not exist.
     * 
     * @param annotationClass
     * @return the annotation for the specified annotation type if present, 
     * otherwise null 
     */
    @SuppressWarnings("unchecked")
    public <T extends Annotation> T getActionAnnotation(Class<T> annotationClass) {
        
        Annotation[] annotations = getActionAnnotations();
        for (Annotation annotation : annotations) {
            if (annotation.annotationType().equals(annotationClass)) {
                return (T)annotation;
            }
        }
        return null;
    }
}
