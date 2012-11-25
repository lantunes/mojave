/*
 * Copyright (C) 2011-2012 Mojavemvc.org
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
package org.mojavemvc.tests.controllers;

import org.mojavemvc.annotations.Action;
import org.mojavemvc.annotations.Param;
import org.mojavemvc.annotations.StatelessController;
import org.mojavemvc.views.JSP;
import org.mojavemvc.views.View;

/**
 * This class demonstrates the basic pattern for injecting a controller into
 * other controllers:
 * <p>
 * 1. give the controller an interface that exposes the public methods of
 * interest
 * <p>
 * 2. in the application's Guice module, bind the interface to the controller
 * implementation
 * <p>
 * 3. inject the controller interface into other conrollers
 * <p>
 * Controllers that are to be injected into other controllers don't have to
 * implement an interface, but this is a best practice, as it facilitates
 * testing, and decouples objects.
 * 
 * @author Luis Antunes
 */
@StatelessController("injectable")
public class InjectableController implements IInjectableController {

    @Action("test")
    public View testAction(@Param("var") String var) {

        String processed = process(var);
        return new JSP("injectable").withAttribute("var", processed);
    }

    public String process(String arg) {

        return "injected-" + arg;
    }
}
