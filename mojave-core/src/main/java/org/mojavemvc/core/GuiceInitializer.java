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
package org.mojavemvc.core;

import java.util.HashSet;
import java.util.Set;

import net.sf.cglib.reflect.FastClass;

import org.mojavemvc.initialization.AppProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * A Guice Injector is thread-safe, and can be shared by multiple threads. This
 * class creates a single instance of a Guice Injector to be added to the
 * controller context during startup.
 * <p>
 * NOTE: All modules must provide a no-arg contructor.
 * 
 * @author Luis Antunes
 */
public class GuiceInitializer {

    private static final Logger logger = LoggerFactory.getLogger("org.mojavemvc");

    public static final String KEY = Injector.class.getName();

    private final Set<Class<? extends Module>> moduleClasses;
    private final Set<Module> providedModules;
    private final AppProperties appProperties;

    public GuiceInitializer(Set<Class<? extends Module>> moduleClasses, 
            Set<Module> providedModules,
            AppProperties appProperties) {

        this.moduleClasses = moduleClasses;
        this.providedModules = 
                providedModules != null ? providedModules : new HashSet<Module>();
        this.appProperties = appProperties;
    }

    public Injector initializeInjector() throws Exception {

        Module[] modules = new Module[providedModules.size() + moduleClasses.size() + 1];

        logger.debug("adding " + ServletResourceModule.class.getName() + " ...");
        modules[0] = new ServletResourceModule(appProperties);

        int i = 1;
        for (Class<?> moduleClass : moduleClasses) {

            logger.debug("found module class: " + moduleClass.getName());

            modules[i] = (Module) FastClass.create(moduleClass).newInstance();
            i++;
        }
        
        for (Module module : providedModules) {

            logger.debug("found provided module: " + module.getClass().getName());

            modules[i] = module;
            i++;
        }

        return Guice.createInjector(modules);
    }
}
