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

import java.util.Vector;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.FileResourceLoader;
import org.mojavemvc.initialization.AppPropertyCollector;
import org.mojavemvc.initialization.AppResources;
import org.mojavemvc.initialization.InitParams;
import org.mojavemvc.initialization.Initializer;
import org.mojavemvc.views.VelocityView;

/**
 * @author Luis Antunes
 */
public class VelocityInitializer implements Initializer {

    //private static final Logger logger = LoggerFactory.getLogger("org.mojavemvc.velocity");
    
    //private static final String VM_PATH = "vm-path";
    
    @Override
    public void initialize(InitParams initParams, AppResources resources, 
            AppPropertyCollector collector) {
        
        //String ftlPath = getVMPath(initParams); //TODO use path
        
        VelocityEngine engine = new VelocityEngine();
        engine.setProperty("resource.loader", "mojave"); 
        //TODO replace with MojaveResourceLoader
        // see org.apache.velocity.tools.view.servlet.WebappLoader
        engine.setProperty("mojave.resource.loader.instance", new FileResourceLoader());
        Vector<String> path = new Vector<String>();
        path.add("src/test/resources/standard/WEB-INF/vm");
        engine.setProperty("mojave.resource.loader.path", path);
        engine.init();
        
        collector.addProperty(VelocityView.CONFIG_PROPERTY, engine);
    }

    /*
    private String getVMPath(InitParams initParams) {
        
        String vmPath = initParams.getParameter(VM_PATH);
        if (vmPath == null || vmPath.trim().length() == 0) {
            vmPath = "/";
            logger.debug("no " + VM_PATH + " init-param specified");
        }
        logger.debug("setting " + VM_PATH + " to " + vmPath);
        return vmPath;
    }
    */
}
