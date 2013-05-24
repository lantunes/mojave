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

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;

/**
 * <p>
 * A path specifying the location of the FreeMarker templates
 * can be of the form /WEB-INF/ftl/, for example, or, it
 * can be prefixed with &quot;classpath:&quot; to indicate
 * that the templates should be loaded from the classpath. If
 * the templates are loaded from the classpath, the class
 * that will provide the classloader must be specified as well,
 * through the ftl-path-class init param.
 * </p>
 * 
 * @author Luis Antunes
 */
public class FTLInitializer implements Initializer {

    private static final Logger logger = LoggerFactory.getLogger("org.mojavemvc.freemarker");
    
    private static final String FTL_PATH_INIT_PARAM = "ftl-path";
    private static final String FTL_PATH_CLASS_INIT_PARAM = "ftl-path-class";
    private static final String FTL_ERROR_FILE_INIT_PARAM = "ftl-error-file";
    
    private static final String CLASSPATH_PATH_PREFIX = "classpath:"; 
    
    @Override
    public void initialize(InitParams initParams, AppResources resources, 
            AppPropertyCollector collector) {
        
        String ftlPath = getFTLPath(initParams);
        Configuration config = initFTLConfig(initParams, resources, ftlPath);
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
    
    private Configuration initFTLConfig(InitParams initParams, 
            AppResources resources, String ftlPath) {
        
        logger.debug("initializing freemarker Configuration...");
        Configuration config = new Configuration();
        TemplateLoader templateLoader = getTemplateLoader(initParams, resources, ftlPath);
        config.setTemplateLoader(templateLoader);
        config.setObjectWrapper(new DefaultObjectWrapper());
        return config;
    }
    
    private TemplateLoader getTemplateLoader(InitParams initParams, 
            AppResources resources, String ftlPath) {
        
        if (ftlPath.startsWith(CLASSPATH_PATH_PREFIX)) {
            Class<?> clazz = getFTLPathClass(initParams);
            return new ClassTemplateLoader(clazz, 
                    ftlPath.replaceFirst(CLASSPATH_PATH_PREFIX, ""));
        }
        
        return new MojaveTemplateLoader(resources, ftlPath);
    }
    
    private Class<?> getFTLPathClass(InitParams initParams) {
        
        String ftlPathClassName = initParams.getParameter(FTL_PATH_CLASS_INIT_PARAM);
        Class<?> ftlPathClass = null;
        if (ftlPathClassName != null && ftlPathClassName.trim().length() != 0) {
            try {
                ftlPathClass = Class.forName(ftlPathClassName);
            } catch (ClassNotFoundException e) {
                logger.error("could not find class for FTL template loading: " + 
                        ftlPathClassName, e);
            }
        }
        
        if (ftlPathClass == null) {
            logger.error("an " + FTL_PATH_INIT_PARAM + " has been specified as a " +
                    "classpath-based path, but no " + FTL_PATH_CLASS_INIT_PARAM + 
                    " was provided, or the class " + ftlPathClassName + 
                    " could not be found");
        }
        return ftlPathClass;
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
