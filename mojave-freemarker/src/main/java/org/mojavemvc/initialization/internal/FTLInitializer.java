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

import org.mojavemvc.exception.DefaultFTLErrorHandler;
import org.mojavemvc.exception.DefaultFTLErrorHandlerFactory;
import org.mojavemvc.exception.ErrorHandlerFactory;
import org.mojavemvc.freemarker.MojaveTemplateLoader;
import org.mojavemvc.initialization.AppPropertyCollector;
import org.mojavemvc.initialization.AppResources;
import org.mojavemvc.initialization.InitParams;
import org.mojavemvc.initialization.Initializer;
import org.mojavemvc.views.FTL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;

/**
 * @author Luis Antunes
 */
public class FTLInitializer implements Initializer {

    private static final Logger logger = LoggerFactory.getLogger("org.mojavemvc.freemarker");
    
    private static final String FTL_PATH_INIT_PARAM = "ftl-path";
    private static final String FTL_ERROR_FILE_INIT_PARAM = "ftl-error-file";
    
    @Override
    public void initialize(InitParams initParams, AppResources resources, 
            AppPropertyCollector collector) {
        
        String ftlPath = getFTLPath(initParams);
        Configuration config = initFTLConfig(resources, ftlPath);
        collector.addProperty(FTL.CONFIG_PROPERTY, config);
        readFTLErrorFile(initParams, collector);
    }

    private String getFTLPath(InitParams initParams) {
        
        String ftlPath = initParams.getParameter(FTL_PATH_INIT_PARAM);
        if (ftlPath == null || ftlPath.trim().length() == 0) {
            ftlPath = "/";
            logger.debug("no " + FTL_PATH_INIT_PARAM + " init-param specified");
        }
        logger.debug("setting " + FTL_PATH_INIT_PARAM + " to " + ftlPath);
        return ftlPath;
    }
    
    private Configuration initFTLConfig(AppResources resources, String ftlPath) {
        
        logger.debug("initializing freemarker Configuration...");
        Configuration config = new Configuration();
        config.setTemplateLoader(new MojaveTemplateLoader(resources, ftlPath));
        config.setObjectWrapper(new DefaultObjectWrapper());
        return config;
    }
    
    private void readFTLErrorFile(InitParams initParams, AppPropertyCollector collector) {

        String ftlErrorFile = initParams.getParameter(FTL_ERROR_FILE_INIT_PARAM);
        if (!isEmpty(ftlErrorFile)) {

            logger.debug("setting " + FTL_ERROR_FILE_INIT_PARAM + " to " + ftlErrorFile);
            collector.addProperty(DefaultFTLErrorHandler.FTL_ERROR_FILE, ftlErrorFile);
            collector.addProperty(ErrorHandlerFactory.DEFAULT_FACTORY, 
                    DefaultFTLErrorHandlerFactory.class.getName());
        }
    }
    
    private boolean isEmpty(String arg) {
        return arg == null || arg.trim().length() == 0;
    }
}
