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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Test;
import org.mojavemvc.core.DefaultAppProperties;
import org.mojavemvc.core.GuiceInitializer;
import org.mojavemvc.initialization.AppProperties;
import org.mojavemvc.tests.controllers.IInjectableController;
import org.mojavemvc.tests.modules.SomeModule;
import org.mojavemvc.tests.services.SomeService;

import com.google.inject.AbstractModule;
import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.Key;

/**
 * @author Luis Antunes
 */
@SuppressWarnings("unchecked")
public class TestGuiceInitializer {

    @Test
    public void injectorHasBasicBindings() throws Exception {

        Injector injector = initializeInjector();

        Map<Key<?>, Binding<?>> bindings = injector.getAllBindings();

        assertNotNull((Binding<HttpServletRequest>) bindings.get(Key.get(HttpServletRequest.class)));
        assertNotNull((Binding<HttpServletResponse>) bindings.get(Key.get(HttpServletResponse.class)));
        assertNotNull((Binding<HttpSession>) bindings.get(Key.get(HttpSession.class)));
        assertNotNull((Binding<AppProperties>) bindings.get(Key.get(AppProperties.class)));
    }

    @Test
    public void injectorHasBasicAndCustomBindings() throws Exception {

        Injector injector = initializeInjector(SomeModule.class);

        Map<Key<?>, Binding<?>> bindings = injector.getAllBindings();

        assertNotNull((Binding<HttpServletRequest>) bindings.get(Key.get(HttpServletRequest.class)));
        assertNotNull((Binding<HttpServletResponse>) bindings.get(Key.get(HttpServletResponse.class)));
        assertNotNull((Binding<HttpSession>) bindings.get(Key.get(HttpSession.class)));
        assertNotNull((Binding<AppProperties>) bindings.get(Key.get(AppProperties.class)));
        assertNotNull((Binding<SomeService>) bindings.get(Key.get(SomeService.class)));
        assertNotNull((Binding<IInjectableController>) bindings.get(Key.get(IInjectableController.class)));
    }
    
    /*---------------------------------------------*/
    
    private Injector initializeInjector(Class<? extends AbstractModule>...modules) throws Exception {
        
        Set<Class<? extends AbstractModule>> moduleClasses = new HashSet<Class<? extends AbstractModule>>();
        for (Class<? extends AbstractModule> module : modules) {
            moduleClasses.add(module);
        }
        GuiceInitializer guiceInitializer = new GuiceInitializer(moduleClasses, newAppProperties());
        Injector injector = guiceInitializer.initializeInjector();
        return injector;
    }
    
    private AppProperties newAppProperties() {
        
        return new DefaultAppProperties(new HashMap<String, String>());
    }
}
