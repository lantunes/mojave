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
package org.mojavemvc.tests;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Test;
import org.mojavemvc.core.GuiceInitializer;
import org.mojavemvc.initialization.AppProperties;
import org.mojavemvc.tests.controllers.IInjectableController;
import org.mojavemvc.tests.modules.SomeModule;
import org.mojavemvc.tests.services.SomeService;

import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;

/**
 * @author Luis Antunes
 */
@SuppressWarnings("unchecked")
public class TestGuiceInitializer {

    @Test
    public void injectorHasBasicBindings() throws Exception {

        Injector injector = initializeInjector();

        Map<Key<?>, Binding<?>> bindings = injector.getAllBindings();

        assertBindingExistsFor(HttpServletRequest.class, bindings);
        assertBindingExistsFor(HttpServletResponse.class, bindings);
        assertBindingExistsFor(HttpSession.class, bindings);
        assertBindingExistsFor(AppProperties.class, bindings);
    }

    @Test
    public void injectorHasBasicAndCustomBindings() throws Exception {

        Injector injector = initializeInjector(SomeModule.class);

        Map<Key<?>, Binding<?>> bindings = injector.getAllBindings();

        assertBindingExistsFor(HttpServletRequest.class, bindings);
        assertBindingExistsFor(HttpServletResponse.class, bindings);
        assertBindingExistsFor(HttpSession.class, bindings);
        assertBindingExistsFor(AppProperties.class, bindings);
        assertBindingExistsFor(SomeService.class, bindings);
        assertBindingExistsFor(IInjectableController.class, bindings);
    }
    
    /*---------------------------------------------*/
    
    private Injector initializeInjector(Class<? extends Module>...modules) throws Exception {
        
        Set<Class<? extends Module>> moduleClasses = new HashSet<Class<? extends Module>>();
        for (Class<? extends Module> module : modules) {
            moduleClasses.add(module);
        }
        GuiceInitializer guiceInitializer = 
                new GuiceInitializer(moduleClasses, null, newAppProperties());
        Injector injector = guiceInitializer.initializeInjector();
        return injector;
    }
    
    private AppProperties newAppProperties() {
        
        return new AppProperties() {
            @Override
            public Object getProperty(String name) {
                return null;
            }
        };
    }
    
    private <T> void assertBindingExistsFor(Class<T> clazz, Map<Key<?>, Binding<?>> bindings) {
        assertNotNull((Binding<T>) bindings.get(Key.get(clazz)));
    }
}
