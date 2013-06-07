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
package org.mojavemvc.tests.modules;

import java.util.HashSet;
import java.util.Set;

import org.mojavemvc.initialization.ModuleProvider;
import org.mojavemvc.tests.services.SomeProvidedService;
import org.mojavemvc.tests.services.SomeProvidedServiceImpl;

import com.google.inject.AbstractModule;
import com.google.inject.Module;

/**
 * 
 * @author Luis Antunes
 */
public class SomeModuleProvider implements ModuleProvider {

    @Override
    public Set<Module> getModules() {
        
        Set<Module> modules = new HashSet<Module>();
        modules.add(new AbstractModule() {
            @Override
            protected void configure() {
                bind(SomeProvidedService.class).to(SomeProvidedServiceImpl.class);
            }
        });
        return modules;
    }
}
