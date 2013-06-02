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
package org.mojavemvc.atmosphere;

import org.atmosphere.di.Injector;

/**
 * <p>
 * The Atmosphere framework uses the java.util.ServiceLoader
 * to provide an existing Injector implementation at runtime.
 * To use the java.util.ServiceLoader, we must place a "services"
 * folder in the META-INF folder of the eventual jar. The services
 * folder contains a file with the same name as the interface
 * representing the service which will be loaded--in the case of
 * Atmosphere DI, it is always org.atmosphere.di.Injector.
 * The content of the file is the fully qualified class name
 * of the implementation of the service. Invoking the static 
 * ServiceLoader.load(org.atmosphere.di.Injector.class) will
 * always create a new instance of the underlying 
 * implementation--this class. 
 * </p>
 * 
 * <p>
 * Since all Mojave applications use the Guice injector, we
 * want to have the Atmosphere framework also use the
 * same injector.
 * </p>
 * 
 * @author Luis Antunes
 */
public class GuiceInjector implements Injector {
    
    public static com.google.inject.Injector INJECTOR;

    /*
     * - we must use the Mojave Guice injector here
     * - this class will be instantiated the first time 
     *   Atmosphere calls org.atmosphere.di.InjectorProvider.getInjector(),
     *   and the same instance will be provided thereafter
     * - there must be a no-arg constructor
     */
    
    @Override
    public void inject(Object o) {
        if (INJECTOR == null)
            throw new IllegalStateException("no Guice Injector found");
        INJECTOR.injectMembers(o);
    }
}
