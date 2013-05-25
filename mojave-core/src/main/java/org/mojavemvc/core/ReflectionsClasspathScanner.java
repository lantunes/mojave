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

import java.util.List;
import java.util.Set;

import org.mojavemvc.annotations.SingletonController;
import org.mojavemvc.annotations.StatefulController;
import org.mojavemvc.annotations.StatelessController;
import org.mojavemvc.initialization.Initializer;
import org.mojavemvc.marshalling.EntityMarshaller;
import org.reflections.Reflections;

import com.google.inject.AbstractModule;
import com.google.inject.Module;

/**
 * @author Luis Antunes
 */
public class ReflectionsClasspathScanner implements ClasspathScanner {

    @Override
    public Set<Class<? extends Initializer>> scanInitializers(List<String> packages) {
        
        Reflections reflections = new Reflections(packages.toArray());
        Set<Class<? extends Initializer>> initializers = 
                reflections.getSubTypesOf(Initializer.class);
        return initializers;
    }

    @Override
    public Set<Class<? extends Module>> scanModules(List<String> packages) {
        
        Reflections reflections = new Reflections(packages.toArray());
        Set<Class<? extends Module>> modules = reflections.getSubTypesOf(Module.class);
        /* we have to check for AbstractModule explicitly because users may have either
         * implemented Module or sub-classed AbstractModule */
        modules.addAll(reflections.getSubTypesOf(AbstractModule.class));
        /* make sure the framework's module is never included in the scan results */
        modules.remove(ServletResourceModule.class);
        return modules;
    }

    @Override
    public Set<Class<?>> scanControllers(List<String> packages) {
        
        Reflections reflections = new Reflections(packages.toArray());
        Set<Class<?>> controllerClasses = reflections.getTypesAnnotatedWith(StatelessController.class);
        controllerClasses.addAll(reflections.getTypesAnnotatedWith(StatefulController.class));
        controllerClasses.addAll(reflections.getTypesAnnotatedWith(SingletonController.class));
        return controllerClasses;
    }

    @Override
    public Set<Class<? extends EntityMarshaller>> scanEntityMarshallers(List<String> packages) {
        
        Reflections reflections = new Reflections(packages.toArray());
        Set<Class<? extends EntityMarshaller>> customMarshallers = 
                reflections.getSubTypesOf(EntityMarshaller.class);
        return customMarshallers;
    }

}
