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

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mojavemvc.initialization.AppProperties;

/**
 * An instance of this class is not thread-safe, and should not be cached and/or
 * re-used.
 * <p>
 * JSP files used in the framework must have a .jsp extension. The JSP servlet in
 * the container must not be re-mapped to handle files with other extensions.
 * 
 * @author Luis Antunes
 */
public class JSP extends DataModelView<JSP> {
    
    public static final String PATH_PROPERTY = "mojavemvc-internal-jsp-path";

    private static final String DOT_JSP = ".jsp";
    
    protected final String jsp;

    /**
     * Requires the name of the underlying JSP file. If the name is not suffixed
     * with .jsp, the suffix will be added.
     * 
     * @param jsp the name of the underlying JSP file represented by this View
     */
    public JSP(String jsp) {

        if (jsp == null || jsp.trim().length() == 0) { 
            throw new IllegalArgumentException("jsp name is null or empty");
        }
        
        if (!jsp.toLowerCase().endsWith(DOT_JSP)) {
            jsp += DOT_JSP;
        }
        
        this.jsp = jsp;
    }

    public JSP(String jsp, String[] keys, Object[] values) {

        this(jsp);
        setAttributesFromPairs(keys, values);
    }

    /**
     * Returns the name of the underlying JSP file.
     * It will be suffixed with .jsp.
     * 
     * @return the jsp page
     */
    public String getJSPName() {

        return jsp;
    }

    @Override
    public void render(HttpServletRequest request, HttpServletResponse response, 
            AppProperties properties) throws ServletException, IOException {

        if (attributes != null && !attributes.isEmpty()) {

            for (String key : attributes.keySet()) {

                request.setAttribute(key, attributes.get(key));
            }
        }

        String jspPath = (String)properties.getProperty(PATH_PROPERTY);
        RequestDispatcher dispatcher = request.getRequestDispatcher(jspPath + jsp);
        dispatcher.forward(request, response);
    }
}
