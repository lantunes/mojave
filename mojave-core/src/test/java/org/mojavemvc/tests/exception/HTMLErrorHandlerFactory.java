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
package org.mojavemvc.tests.exception;

import org.mojavemvc.exception.ErrorHandler;
import org.mojavemvc.exception.ErrorHandlerFactory;
import org.mojavemvc.initialization.AppProperties;
import org.mojavemvc.tests.views.HTMLErrorPage;
import org.mojavemvc.views.View;

/**
 * @author Luis Antunes
 */
public class HTMLErrorHandlerFactory implements ErrorHandlerFactory {

    @Override
    public ErrorHandler createErrorHandler() {
        return new ErrorHandler() {
            @Override
            public View handleError(Throwable e, AppProperties properties) {
                return new HTMLErrorPage();
            }
        };
    }

}
