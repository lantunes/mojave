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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.cglib.reflect.FastClass;

import org.mojavemvc.aop.RequestContext;
import org.mojavemvc.views.EmptyView;
import org.mojavemvc.views.View;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Injector;

/**
 * @author Luis Antunes
 */
public class HttpActionInvoker implements ActionInvoker {

    private static final Logger logger = LoggerFactory.getLogger(HttpActionInvoker.class);

    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final ControllerDatabase controllerDb;
    private final Injector injector;
    private final String controller;
    private final String action;
    private final Map<String, Object> parameterMap;

    private Object actionController;
    private Class<?> actionControllerClass;
    private ActionSignature actionSignature;

    public HttpActionInvoker(HttpServletRequest request, HttpServletResponse response, ControllerDatabase controllerDb,
            RoutedRequest routed, Injector injector) {

        this.request = request;
        this.response = response;
        this.controllerDb = controllerDb;
        this.injector = injector;

        String cntrl = routed.getController();
        controller = (cntrl == null || cntrl.trim().length() == 0) ? "" : cntrl;

        String actn = routed.getAction();
        action = (actn == null || actn.trim().length() == 0) ? "" : actn;
        
        this.parameterMap = routed.getParameterMap();
    }

    public View invokeAction(Object actionController, ActionSignature actionSignature) throws Exception {

        this.actionController = actionController;
        actionControllerClass = actionController.getClass();
        this.actionSignature = actionSignature;

        logInitMessage();

        return invokeActionMethod();
    }

    private void logInitMessage() {

        String message = "";
        if (actionSignature != null) {
            message = "invoking action " + actionSignature.methodName();
        } else {
            message = "invoking default action";
        }
        message += " for " + actionControllerClass.getName();
        logger.debug(message);
    }

    private View invokeActionMethod() throws Exception {

        View view = null;

        Object[] args = actionSignature.getArgs(parameterMap);

        List<Object> classInterceptors = createInterceptors(controllerDb.getInterceptorsFor(actionControllerClass));

        List<Object> methodInterceptors = createInterceptorsForAction();

        for (Object interceptor : classInterceptors) {

            view = invokeBeforeActionIfRequired(interceptor,
                    controllerDb.getBeforeActionMethodForInterceptor(interceptor.getClass()), args);
            if (view != null) {
                return view;
            }
        }

        for (Object interceptor : methodInterceptors) {

            view = invokeBeforeActionIfRequired(interceptor,
                    controllerDb.getBeforeActionMethodForInterceptor(interceptor.getClass()), args);
            if (view != null) {
                return view;
            }
        }

        view = invokeBeforeActionIfRequired(actionController,
                controllerDb.getBeforeActionMethodFor(actionControllerClass), args);

        if (view != null) {
            return view;
        }

        FastClass actionFastClass = controllerDb.getFastClass(actionControllerClass);
        view = (View) actionFastClass.invoke(actionSignature.fastIndex(), actionController, args);
        logger.debug("invoked " + actionSignature.methodName() + " for " + actionControllerClass.getName());

        View afterActionView = invokeAfterActionIfRequired(actionController,
                controllerDb.getAfterActionMethodFor(actionControllerClass), args);

        if (afterActionView != null) {
            view = afterActionView;
        }

        for (Object interceptor : methodInterceptors) {

            View interceptorView = invokeAfterActionIfRequired(interceptor,
                    controllerDb.getAfterActionMethodForInterceptor(interceptor.getClass()), args);
            if (interceptorView != null) {
                view = interceptorView;
                break;
            }
        }

        for (Object interceptor : classInterceptors) {

            View interceptorView = invokeAfterActionIfRequired(interceptor,
                    controllerDb.getAfterActionMethodForInterceptor(interceptor.getClass()), args);
            if (interceptorView != null) {
                view = interceptorView;
                break;
            }
        }

        return view;
    }

    private List<Object> createInterceptorsForAction() throws Exception {

        List<Class<?>> interceptorClasses = null;
        interceptorClasses = actionSignature.getInterceptorClasses(controllerDb, actionControllerClass, action);
        return createInterceptors(interceptorClasses);
    }

    private List<Object> createInterceptors(List<Class<?>> interceptorClasses) throws Exception {

        List<Object> interceptors = new ArrayList<Object>();
        if (interceptorClasses != null) {
            for (Class<?> interceptorClass : interceptorClasses) {

                /* use the Guice Injector */
                Object interceptor = injector.getInstance(interceptorClass);
                interceptors.add(interceptor);
            }
        }
        return interceptors;
    }

    private View invokeBeforeActionIfRequired(Object instance, ActionSignature actionMethod, Object[] args)
            throws Exception {

        return invokeBeforeOrAfterActionIfRequired(instance, actionMethod, args, "before");
    }

    private View invokeAfterActionIfRequired(Object instance, ActionSignature actionMethod, Object[] args)
            throws Exception {

        return invokeBeforeOrAfterActionIfRequired(instance, actionMethod, args, "after");
    }

    private View invokeBeforeOrAfterActionIfRequired(Object instance, ActionSignature actionMethod, Object[] args,
            String which) throws Exception {

        View view = null;

        if (actionMethod != null) {
            /*
             * we've already validated that there are no method parameters when
             * creating the controller database
             */

            FastClass actionFastClass = controllerDb.getFastClass(instance.getClass());
            Object returnObj = actionFastClass.invoke(actionMethod.fastIndex(), instance,
                    getBeforeOrAfterActionArgs(actionMethod.parameterTypes(), args));

            if (returnObj != null && returnObj instanceof View) {
                view = (View) returnObj;
            }
            logger.debug("invoked " + which + " action for " + instance.getClass().getName());

            /*
             * if there is a before or after action method, it is possible that
             * another view may have been dispatched to from that method;
             * therefore if the response has been committed, we should return an
             * empty view NOTE: this will not work if the servlet container does
             * not set the response to being committed after the dispatch
             */
            if (response.isCommitted())
                return new EmptyView();
        }

        return view;
    }

    private Object[] getBeforeOrAfterActionArgs(Class<?>[] paramterTypes, Object[] actionArgs) {

        Object[] args = new Object[] {};

        if (paramterTypes != null && paramterTypes.length == 1 && paramterTypes[0].equals(RequestContext.class)) {

            args = new Object[1];
            args[0] = new RequestContext(request, response, actionArgs, action, controller);
        }

        return args;
    }
}
