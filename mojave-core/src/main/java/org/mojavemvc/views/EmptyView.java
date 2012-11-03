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
package org.mojavemvc.views;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 * This view does nothing during the call to render(). It is used when the
 * content of the response is specified in the response headers, instead of the
 * response body.
 * </p>
 * 
 * @author Luis Antunes
 */
public class EmptyView implements View {

    @Override
    public void render(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        /*
         * do nothing to render the view
         */
    }

}
