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

import org.mojavemvc.exception.DefaultJSPErrorHandler;
import org.mojavemvc.initialization.AppPropertyCollector;
import org.mojavemvc.initialization.AppResources;
import org.mojavemvc.initialization.InitParams;
import org.mojavemvc.initialization.Initializer;
import org.mojavemvc.views.JSP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Luis Antunes
 */
public class JSPInitializer implements Initializer {
    
    private static final Logger logger = LoggerFactory.getLogger(JSPInitializer.class);
    
    private static final String JSP_PATH = "jsp-path";
    private static final String JSP_ERROR_FILE = "jsp-error-file";

    @Override
    public void initialize(InitParams initParams, AppResources resources, 
            AppPropertyCollector collector) {
        
        readJSPPath(initParams, collector);
        readJSPErrorFile(initParams, collector);
    }
    
    private void readJSPPath(InitParams initParams, AppPropertyCollector collector) {

        String jspPath = initParams.getParameter(JSP_PATH);
        if (!isEmpty(jspPath)) {

            jspPath = processContextPath(jspPath);
            logger.debug("setting " + JSP_PATH + " to " + jspPath);

        } else {

            jspPath = "";
            logger.debug("no " + JSP_PATH + " init-param specified; setting to \"\"");
        }
        
        collector.addProperty(JSP.PATH_PROPERTY, jspPath);
    }
    
    private String processContextPath(String path) {

        if (path == null || path.trim().length() == 0) {
            return path;
        }
        if (path.charAt(path.length() - 1) != '/') {
            path += "/";
        }
        return path;
    }

    private void readJSPErrorFile(InitParams initParams, AppPropertyCollector collector) {

        String jspErrorFile = initParams.getParameter(JSP_ERROR_FILE);
        if (!isEmpty(jspErrorFile)) {

            logger.debug("setting " + JSP_ERROR_FILE + " to " + jspErrorFile);

        } else {

            jspErrorFile = "";
            logger.debug("no " + JSP_ERROR_FILE + " init-param specified; setting to \"\"");
        }
        
        collector.addProperty(DefaultJSPErrorHandler.JSP_ERROR_FILE_PROPERTY, jspErrorFile);
    }
    
    private boolean isEmpty(String arg) {
        return arg == null || arg.trim().length() == 0;
    }
}
