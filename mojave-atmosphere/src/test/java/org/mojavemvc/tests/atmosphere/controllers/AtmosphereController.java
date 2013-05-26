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
package org.mojavemvc.tests.atmosphere.controllers;

import org.atmosphere.annotation.Broadcast;
import org.atmosphere.annotation.Suspend;
import org.mojavemvc.annotations.GETAction;
import org.mojavemvc.annotations.InterceptedBy;
import org.mojavemvc.annotations.POSTAction;
import org.mojavemvc.annotations.Returns;
import org.mojavemvc.annotations.StatelessController;
import org.mojavemvc.atmosphere.AtmosphereInterceptor;

/**
 * @author Luis Antunes
 */
@StatelessController("atmosphere")
/*
 * TODO during creation of controller database,
 * if class contains Atmosphere annotations, should
 * automatically attach interceptor?--implies a hook
 * exists into controller database process
 * 
 * or, just have an @AtmosphereController, if such
 * controllers must be singletons, etc.
 */
@InterceptedBy(AtmosphereInterceptor.class)
public class AtmosphereController {

    @Suspend(contentType = "application/json")
    @GETAction
    @Returns("text/plain")
    public String suspend() {
        return "";
    }
    
    @Broadcast(writeEntity = false)
    @POSTAction
    @Returns("text/plain")
    public String broadcast(String message) {
        return message;
    }
}
