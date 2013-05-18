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
package org.mojavemvc.freemarker;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;

import org.mojavemvc.initialization.AppResources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import freemarker.cache.TemplateLoader;

/**
 * @author Luis Antunes
 */
public class MojaveTemplateLoader implements TemplateLoader {
    
    private static final Logger logger = LoggerFactory.getLogger("org.mojavemvc.freemarker");

    private final AppResources resources;
    private final String path;
    
    public MojaveTemplateLoader(AppResources resources, String path) {
        
        if(path == null) {
            throw new IllegalArgumentException("path is null");
        }
        
        this.path = fixPath(path);
        this.resources = resources;
    }

    private String fixPath(String path) {
        
        String fixedPath = path.replace('\\', '/');
        if(!fixedPath.endsWith("/")) {
            fixedPath += "/";
        }
        if (!fixedPath.startsWith("/")) {
            fixedPath = "/" + fixedPath;
        }
        return fixedPath;
    }

    @Override
    public Object findTemplateSource(String name) throws IOException {
        
        String fullPath = path + name;
        
        FileTemplateSource fileTemplateSource = newFileTemplateSource(fullPath);
        if (fileTemplateSource != null) return fileTemplateSource;
            
        return newURLTemplateSource(fullPath);
    }

    private FileTemplateSource newFileTemplateSource(String fullPath) {
        
        FileTemplateSource templateSource = null;
        String realPath = resources.getRealPath(fullPath);
        if (realPath != null) {
            File file = new File(realPath);
            try {
                if(file.isFile() && file.canRead()) {                    
                    return new FileTemplateSource(file);
                }
            } catch (SecurityException e) {
                /* ignore - the file cannot be read */
            }
        }
        
        return templateSource;
    }
    
    private URLTemplateSource newURLTemplateSource(String fullPath) 
            throws IOException {
        
        URLTemplateSource templateSource = null;
        try {
            
            URL url = resources.getResource(fullPath);
            if (url != null) templateSource = new URLTemplateSource(url);
            
        } catch(MalformedURLException e) {
            logger.warn("could not retrieve resource " + fullPath, e);
            return null;
        }
        
        return templateSource;
    }
    
    @Override
    public long getLastModified(Object templateSource) {

        return ((TemplateSource)templateSource).getLastModified();
    }
    
    @Override
    public Reader getReader(Object templateSource, String encoding)
            throws IOException {

        return ((TemplateSource)templateSource).getReader(encoding);
    }
    
    @Override
    public void closeTemplateSource(Object templateSource) throws IOException {

        ((TemplateSource)templateSource).close();
    }
}
