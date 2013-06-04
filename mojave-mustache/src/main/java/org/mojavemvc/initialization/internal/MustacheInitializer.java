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
package org.mojavemvc.initialization.internal;

import java.io.File;

import org.mojavemvc.exception.DefaultMustacheErrorHandler;
import org.mojavemvc.exception.DefaultMustacheErrorHandlerFactory;
import org.mojavemvc.exception.ErrorHandlerFactory;
import org.mojavemvc.initialization.AppPropertyCollector;
import org.mojavemvc.initialization.AppResources;
import org.mojavemvc.initialization.InitParams;
import org.mojavemvc.initialization.Initializer;
import org.mojavemvc.views.MustacheView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.MustacheFactory;

/**
 * @author Luis Antunes
 */
public class MustacheInitializer implements Initializer {

    private static final Logger logger = LoggerFactory.getLogger("org.mojavemvc.mustache");
    
    private static final String MUSTACHE_PATH_INIT_PARAM = "mustache-path";
    private static final String MUSTACHE_ERROR_FILE_INIT_PARAM = "mustache-error-file";
    
    private static final String CLASSPATH_PATH_PREFIX = "classpath:";
    
    @Override
    public void initialize(InitParams initParams, AppResources resources, 
            AppPropertyCollector collector) {
        
        String mustachePath = getMustachePath(initParams);

        MustacheFactory mf = getMustacheFactory(mustachePath, resources);
        
        collector.addProperty(MustacheView.CONFIG_PROPERTY, mf);
        
        readMustacheErrorFile(initParams, collector);
    }

    private String getMustachePath(InitParams initParams) {
        
        String mustachePath = initParams.getParameter(MUSTACHE_PATH_INIT_PARAM);
        if (mustachePath == null || mustachePath.trim().length() == 0) {
            mustachePath = "/";
            logger.debug("no " + MUSTACHE_PATH_INIT_PARAM + " init-param specified");
        }
        logger.debug("setting " + MUSTACHE_PATH_INIT_PARAM + " to " + mustachePath);
        return mustachePath;
    }
    
    private MustacheFactory getMustacheFactory(String path, AppResources resources) {
        
        if (path.startsWith(CLASSPATH_PATH_PREFIX)) {
            
            return new DefaultMustacheFactory(path.replaceFirst(CLASSPATH_PATH_PREFIX, ""));
        }
        
        String realPath = resources.getRealPath(fixPath(path));
        return new DefaultMustacheFactory(new File(realPath));
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
    
    private void readMustacheErrorFile(InitParams initParams, AppPropertyCollector collector) {

        String mustacheErrorFile = initParams.getParameter(MUSTACHE_ERROR_FILE_INIT_PARAM);
        if (!isEmpty(mustacheErrorFile)) {

            logger.debug("setting " + MUSTACHE_ERROR_FILE_INIT_PARAM + " to " + mustacheErrorFile);
            collector.addProperty(DefaultMustacheErrorHandler.MUSTACHE_ERROR_FILE, mustacheErrorFile);
            collector.addProperty(ErrorHandlerFactory.DEFAULT_FACTORY, 
                    DefaultMustacheErrorHandlerFactory.class.getName());
        }
    }
    
    private boolean isEmpty(String arg) {
        return arg == null || arg.trim().length() == 0;
    }
}
