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

import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

/**
 * @author Luis Antunes
 */
public class MustacheView extends DataModelView<MustacheView> {

    public static final String CONFIG_PROPERTY = "mojavemvc-internal-mustache-config";
    
    private final String mustacheName;
    
    public MustacheView(String mustacheName) {
        
        if (mustacheName == null || mustacheName.trim().length() == 0) {
            throw new IllegalArgumentException("mustache name cannot be empty");
        }
        this.mustacheName = mustacheName;
    }
    
    @Override
    public void render(HttpServletRequest request, HttpServletResponse response, 
            AppProperties properties)
            throws ServletException, IOException {

        MustacheFactory mf = (MustacheFactory)properties.getProperty(CONFIG_PROPERTY);
        
        /*
         * the DefaultMustacheFactory caches the template once compiled
         */
        Mustache mustache = mf.compile(mustacheName);
        
        try {
            
            mustache.execute(response.getWriter(), attributes);
            
        } catch (Exception e) {
            throw new RuntimeException("error processing mustache : " + mustacheName, e);
        }
    }
}
