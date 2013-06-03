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
package org.mojavemvc.exception;

import org.mojavemvc.initialization.AppProperties;
import org.mojavemvc.views.MustacheView;
import org.mojavemvc.views.View;

/**
 * The default Mustache template {@link ErrorHandler} for the application. Simply returns a
 * {@link MustacheView} based on the 'mustache-error-file' init parameter (see
 * {@link org.mojavemvc.FrontController}).
 * 
 * @author Luis Antunes
 */
public class DefaultMustacheErrorHandler implements ErrorHandler {

    public static final String MUSTACHE_ERROR_FILE = "mojavemvc-internal-mustache-error-file";

    public View handleError(Throwable e, AppProperties properties) {

        String errorFile = (String)properties.getProperty(MUSTACHE_ERROR_FILE);
        return new MustacheView(errorFile);
    }
}
