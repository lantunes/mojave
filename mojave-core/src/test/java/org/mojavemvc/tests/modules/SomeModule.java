/*
 * Copyright (C) 2011 Mojavemvc.org
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

import org.mojavemvc.tests.controllers.IInjectableController;
import org.mojavemvc.tests.controllers.InjectableController;
import org.mojavemvc.tests.services.SomeService;
import org.mojavemvc.tests.services.SomeServiceImpl;

import com.google.inject.AbstractModule;

/**
 * 
 * @author Luis Antunes
 */
public class SomeModule extends AbstractModule {
    @Override
    protected void configure() {

        bind(SomeService.class).to(SomeServiceImpl.class);
        bind(IInjectableController.class).to(InjectableController.class);
    }
}
