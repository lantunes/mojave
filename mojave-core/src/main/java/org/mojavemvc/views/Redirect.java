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
package org.mojavemvc.views;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mojavemvc.initialization.AppProperties;

/**
 * Sends a redirect to the given path, through the servlet response.
 * 
 * @author Luis Antunes
 */
public class Redirect implements View {

    private final String path;

    public Redirect(String path) {

        this.path = path;
    }

    @Override
    public void render(HttpServletRequest request, HttpServletResponse response, 
            AppProperties properties) throws ServletException, IOException {

        response.sendRedirect(path);
    }

    @Override
    public String toString() {
        return path;
    }
}
