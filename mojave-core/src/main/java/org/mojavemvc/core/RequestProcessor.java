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

import org.mojavemvc.exception.ErrorHandler;
import org.mojavemvc.views.View;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An instance of this class is not thread-safe and should not be shared by
 * multiple threads.
 * 
 * @author Luis Antunes
 */
public class RequestProcessor {

    private static final Logger logger = LoggerFactory.getLogger(RequestProcessor.class);

    private final ActionResolver resolver;
    private final ActionInvoker invoker;
    private final ErrorHandler errorHandler;

    private String controllerClassName;

    public RequestProcessor(ActionResolver resolver, ActionInvoker invoker, ErrorHandler errorHandler) {

        this.resolver = resolver;
        this.invoker = invoker;
        this.errorHandler = errorHandler;
    }

    public View process(String controller, String action) {

        View view;

        try {

            resolver.resolve(controller, action);

            controllerClassName = resolver.getControllerClassName();
            Object actionController = resolver.getActionController();
            ActionSignature actionSignature = resolver.getActionSignature();

            view = invoker.invokeAction(actionController, actionSignature);

        } catch (Throwable e) {

            logger.error("error invoking action controller: ", e);
            view = errorHandler.handleError(e);
        }

        return view;
    }

    public String getControllerClassName() {

        return controllerClassName;
    }
}
