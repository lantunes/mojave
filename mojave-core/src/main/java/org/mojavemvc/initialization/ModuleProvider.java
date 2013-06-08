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
package org.mojavemvc.initialization;

import java.util.Collection;

import com.google.inject.Module;

/**
 * An instance of this class provides custom modules
 * for the Mojave Guice injector during initialization.
 * <p>
 * A implementation of this interface must provide a 
 * no-arg constructor.
 * 
 * @author Luis Antunes
 */
public interface ModuleProvider {

    Collection<Module> getModules();
}
