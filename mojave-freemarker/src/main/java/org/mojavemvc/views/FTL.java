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

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * @author Luis Antunes
 */
public class FTL extends DataModelView<FTL> {

    public static final String CONFIG_PROPERTY = "mojavemvc-internal-ftl-config";
    
    private final String templateName;
    
    public FTL(String templateName) {
        
        if (templateName == null || templateName.trim().length() == 0) {
            throw new IllegalArgumentException("template name cannot be empty");
        }
        this.templateName = templateName;
    }
    
    @Override
    public void render(HttpServletRequest request, HttpServletResponse response, 
            AppProperties properties)
            throws ServletException, IOException {

        Configuration config = (Configuration)properties.getProperty(CONFIG_PROPERTY);
        
        Template template = config.getTemplate(templateName);
        
        try {
            
            template.process(attributes, response.getWriter());
            
        } catch (TemplateException e) {
            throw new RuntimeException("error processing template : " + templateName, e);
        }
    }

}
