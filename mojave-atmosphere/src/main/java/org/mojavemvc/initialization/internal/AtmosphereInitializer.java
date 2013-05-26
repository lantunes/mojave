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

import org.atmosphere.cpr.AtmosphereFramework;
import org.mojavemvc.atmosphere.AtmosphereInterceptor;
import org.mojavemvc.atmosphere.MojaveAtmosphereHandler;
import org.mojavemvc.initialization.AppPropertyCollector;
import org.mojavemvc.initialization.AppResources;
import org.mojavemvc.initialization.InitParams;
import org.mojavemvc.initialization.Initializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luis Antunes
 */
public class AtmosphereInitializer implements Initializer {

    private static final Logger logger = LoggerFactory.getLogger("org.mojavemvc.atmosphere");

    @Override
    public void initialize(InitParams initParams, AppResources resources, AppPropertyCollector collector) {

        logger.info("initializing Atmosphere framework...");
        
        AtmosphereFramework framework = new AtmosphereFramework(false, true);
        
        //TODO does the "/" mapping have any significance in this case?
        framework.addAtmosphereHandler("/", new MojaveAtmosphereHandler());
        
        //TODO call init() after configure class using setters or
        // call framework.init(servletConfig), and add support for passing along ServletConfig
        framework.init();
        
        collector.addProperty(AtmosphereInterceptor.ATMOSPHERE_FRAMEWORK, framework);
    }
}
