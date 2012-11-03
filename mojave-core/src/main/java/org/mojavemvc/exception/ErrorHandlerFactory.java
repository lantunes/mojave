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
package org.mojavemvc.exception;

/**
 * <p>
 * Users can provide their own implementation of this interface. They must,
 * however, provide a public no-arg constructor in the implementation. The
 * custom implementation is made available to the application through the
 * 'error-handler-factory' init parameter. See
 * {@link org.mojavemvc.FrontController}.
 * </p>
 * 
 * <p>
 * An instance of a class implementing this interface is created once during
 * FrontController initialization, and placed in the front controller context.
 * Therefore, users should make the class thread-safe, as it will be accessed
 * simultaneously by multiple threads.
 * </p>
 * 
 * @author Luis Antunes
 */
public interface ErrorHandlerFactory {

    public static final String KEY = ErrorHandlerFactory.class.getName();

    /**
     * <p>
     * This method will be invoked once per request, before the action is
     * processed.
     * </p>
     * 
     * <p>
     * Users should note that any exceptions thrown from this method are not
     * guaranteed to be caught in the FrontController. They may escape to the
     * requestor. Users should handle any exceptions inside the implementation
     * of this method if different behaviour is desired.
     * </p>
     * 
     * @return an instance of an ErrorHandler
     */
    public ErrorHandler createErrorHandler();
}
