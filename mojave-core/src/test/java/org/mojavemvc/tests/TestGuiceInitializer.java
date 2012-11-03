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
