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
package org.mojavemvc.exception;

import org.mojavemvc.views.View;


/**
 * <p>
 * Users can implement this interface in their own classes
 * to define their own error handling behaviour. This class
 * is created using an ErrorHandlerFactory. The factory
 * is invoked once per request, before the action is processed.
 * See {@link ErrorHandlerFactory}.
 * </p>
 * 
 * @author Luis Antunes
 */
public interface ErrorHandler {

	/**
	 * <p>
	 * This method is invoked when an error occurs during action
	 * processing. Specifically, if a controller or action cannot
	 * be resolved, or the action cannot be invoked, or the original 
	 * View cannot be rendered, this method is called, and the 
	 * returned View is rendered.
	 * </p>
	 * 
	 * @param e the Throwable thrown during action processing
	 * @return a View that will be rendered instead
	 */
	public View handleError( Throwable e );
}
