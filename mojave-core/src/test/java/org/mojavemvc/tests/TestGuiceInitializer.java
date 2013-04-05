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
import org.mojavemvc.tests.controllers.IInjectableController;
import org.mojavemvc.tests.modules.SomeModule;
import org.mojavemvc.tests.services.SomeService;

import com.google.inject.AbstractModule;
import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.Key;

public class TestGuiceInitializer {

    @Test
    @SuppressWarnings("unchecked")
    public void testInitializeInjector() throws Exception {

        Set<Class<? extends AbstractModule>> moduleClasses = new HashSet<Class<? extends AbstractModule>>();
        GuiceInitializer guiceInitializer = new GuiceInitializer(moduleClasses);
        Injector injector = guiceInitializer.initializeInjector();

        Map<Key<?>, Binding<?>> bindings = injector.getAllBindings();

        Key<HttpServletRequest> reqBindingKey = Key.get(HttpServletRequest.class);
        Binding<HttpServletRequest> reqBinding = (Binding<HttpServletRequest>) bindings.get(reqBindingKey);
        assertNotNull(reqBinding);

        Key<HttpServletResponse> respBindingKey = Key.get(HttpServletResponse.class);
        Binding<HttpServletResponse> respBinding = (Binding<HttpServletResponse>) bindings.get(respBindingKey);
        assertNotNull(respBinding);

        Key<HttpSession> sessBindingKey = Key.get(HttpSession.class);
        Binding<HttpSession> sessBinding = (Binding<HttpSession>) bindings.get(sessBindingKey);
        assertNotNull(sessBinding);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testInitializeInjector2() throws Exception {

        Set<Class<? extends AbstractModule>> moduleClasses = new HashSet<Class<? extends AbstractModule>>();
        moduleClasses.add(SomeModule.class);
        GuiceInitializer guiceInitializer = new GuiceInitializer(moduleClasses);
        Injector injector = guiceInitializer.initializeInjector();

        Map<Key<?>, Binding<?>> bindings = injector.getAllBindings();

        Key<HttpServletRequest> reqBindingKey = Key.get(HttpServletRequest.class);
        Binding<HttpServletRequest> reqBinding = (Binding<HttpServletRequest>) bindings.get(reqBindingKey);
        assertNotNull(reqBinding);

        Key<HttpServletResponse> respBindingKey = Key.get(HttpServletResponse.class);
        Binding<HttpServletResponse> respBinding = (Binding<HttpServletResponse>) bindings.get(respBindingKey);
        assertNotNull(respBinding);

        Key<HttpSession> sessBindingKey = Key.get(HttpSession.class);
        Binding<HttpSession> sessBinding = (Binding<HttpSession>) bindings.get(sessBindingKey);
        assertNotNull(sessBinding);

        Key<SomeService> someServiceBindingKey = Key.get(SomeService.class);
        Binding<SomeService> someServiceBinding = (Binding<SomeService>) bindings.get(someServiceBindingKey);
        assertNotNull(someServiceBinding);

        Key<IInjectableController> controllerBindingKey = Key.get(IInjectableController.class);
        Binding<IInjectableController> controllerBinding = (Binding<IInjectableController>) bindings
                .get(controllerBindingKey);
        assertNotNull(controllerBinding);
    }
}
