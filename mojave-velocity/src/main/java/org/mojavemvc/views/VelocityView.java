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

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.mojavemvc.initialization.AppProperties;

/**
 * @author Luis Antunes
 */
public class VelocityView extends DataModelView<VelocityView> {

    public static final String CONFIG_PROPERTY = "mojavemvc-internal-vm-config";
    
    private final String templateName;
    
    public VelocityView(String templateName) {
        
        if (templateName == null || templateName.trim().length() == 0) {
            throw new IllegalArgumentException("template name cannot be empty");
        }
        this.templateName = templateName;
    }
    
    @Override
    public void render(HttpServletRequest request, HttpServletResponse response, 
            AppProperties properties)
            throws ServletException, IOException {

        VelocityEngine engine = (VelocityEngine)properties.getProperty(CONFIG_PROPERTY);
        
        Template template = engine.getTemplate(templateName);
        
        try {
            
            template.merge(new VelocityContext(attributes), response.getWriter());
            
        } catch (Exception e) {
            throw new RuntimeException("error processing template : " + templateName, e);
        }
    }
}
