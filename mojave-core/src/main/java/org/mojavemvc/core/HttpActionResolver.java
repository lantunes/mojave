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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.sf.cglib.reflect.FastClass;

import org.mojavemvc.annotations.DefaultController;
import org.mojavemvc.annotations.SingletonController;
import org.mojavemvc.annotations.StatefulController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Injector;

/**
 * 
 * @author Luis Antunes
 */
public class HttpActionResolver implements ActionResolver {

    private static final Logger logger = LoggerFactory.getLogger(HttpActionResolver.class);

    private final ControllerContext context;
    private final HttpServletRequest request;
    private final HttpMethod httpMethod;
    private final ControllerDatabase controllerDb;
    private final Injector injector;

    private Object actionController;
    private ActionSignature actionSignature;
    private Class<?> controllerClass;

    public HttpActionResolver(ControllerContext context, HttpServletRequest req, HttpMethod httpMethod,
            ControllerDatabase controllerDb, Injector injector) {

        this.context = context;
        this.request = req;
        this.httpMethod = httpMethod;
        this.controllerDb = controllerDb;
        this.injector = injector;
    }

    public void resolve(final String controller, final String action) throws Exception {

        resolveControllerClass(controller);
        createActionController();
        resolveActionSignature(action);
    }

    private void resolveControllerClass(final String controller) {

        if (controller != null) {
            controllerClass = controllerDb.getControllerClass(controller);
            if (controllerClass == null) {
                throw new RuntimeException("no controller defined for controller " + "variable: " + controller);
            }
        } else {
            controllerClass = controllerDb.getDefaultControllerClass();
            if (controllerClass == null) {
                throw new UnsupportedOperationException("no controller variable was specified, " + "and no @"
                        + DefaultController.class.getSimpleName() + " exists");
            }
        }
        logger.debug("received request for " + controllerClass.getName() + "; processing...");
    }

    private void createActionController() throws Exception {

        Annotation annot = controllerClass.getAnnotation(StatefulController.class);
        if (annot != null) {

            /*
             * a StatefulController
             */
            HttpSession sess = request.getSession();
            Object actionConrollerInSession = sess.getAttribute(controllerClass.getName());
            if (actionConrollerInSession != null) {
                /* re-inject any Guice-managed dependencies */
                injector.injectMembers(actionConrollerInSession);
                actionController = actionConrollerInSession;
            } else {
                createNewActionController();
                request.getSession().setAttribute(controllerClass.getName(), actionController);
            }

        } else {

            annot = controllerClass.getAnnotation(SingletonController.class);
            if (annot != null) {

                /*
                 * a SingletonController
                 */
                Object actionControllerInCtx = context.getAttribute(controllerClass.getName());
                if (actionControllerInCtx != null) {
                    /* re-inject any Guice-managed dependencies */
                    injector.injectMembers(actionControllerInCtx);
                    actionController = actionControllerInCtx;
                } else {
                    createNewActionController();
                    context.setAttribute(controllerClass.getName(), actionController);
                }

            } else {

                /*
                 * must be a StatelessController
                 */
                createNewActionController();
            }
        }
    }

    private void createNewActionController() throws Exception {

        /* use the Guice Injector */
        actionController = injector.getInstance(controllerClass);

        invokeAfterConstructIfRequired();
    }

    private void invokeAfterConstructIfRequired() throws Exception {

        ActionSignature afterConstructSig = controllerDb.getAfterConstructMethodFor(controllerClass);
        if (afterConstructSig != null) {
            /*
             * we've already validated that there are no method parameters when
             * creating the controller database
             */
            FastClass actionFastClass = controllerDb.getFastClass(controllerClass);
            actionFastClass.invoke(afterConstructSig.fastIndex(), actionController, new Object[] {});
            logger.debug("invoked after construct action for " + controllerClass.getName());
        }
    }

    private void resolveActionSignature(final String action) {

        if (action == null || action.trim().length() == 0) {

            actionSignature = controllerDb.getHttpMethodActionSignature(controllerClass, httpMethod);

            if (actionSignature == null) {

                actionSignature = controllerDb.getDefaultActionMethodFor(controllerClass);

                if (actionSignature == null) {
                    throw new UnsupportedOperationException("no default action found in " + controllerClass.getName());
                }
            }

        } else {

            actionSignature = controllerDb.getActionMethodSignature(controllerClass, action);

            /*
             * if the actionSignature is null, an invalid action parameter was
             * given
             */
            if (actionSignature == null) {
                throw new IllegalArgumentException("no action '" + action + "' defined for " + controllerClass);
            }
        }
    }

    public ActionSignature getActionSignature() {

        return actionSignature;
    }

    public Object getActionController() {

        return actionController;
    }

    public String getControllerClassName() {

        return controllerClass != null ? controllerClass.getName() : "<unknown>";
    }
}
