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
package org.mojavemvc.tests.controllers;

import org.mojavemvc.annotations.DELETEAction;
import org.mojavemvc.annotations.GETAction;
import org.mojavemvc.annotations.POSTAction;
import org.mojavemvc.annotations.Param;
import org.mojavemvc.annotations.ParamPath;
import org.mojavemvc.annotations.StatelessController;
import org.mojavemvc.tests.views.HTMLView;
import org.mojavemvc.views.View;

@StatelessController("parampath-http")
public class ParamPathHttpMethodController {

    @GETAction
    @ParamPath("say/:name")
    public View getAction(@Param("name") String name) {
        return new HTMLView()
            .withH2Content("Hello from " + name + ", " + "GET");
    }
    
    @POSTAction
    @ParamPath("say/:name")
    public View postAction(@Param("name") String name) {
        return new HTMLView()
            .withH2Content("Hello from " + name + ", " + "POST");
    }
    
    @DELETEAction
    @ParamPath("say/:name")
    public View deleteAction(@Param("name") String name) {
        return new HTMLView()
            .withH2Content("Hello from " + name + ", " + "DELETE");
    }
}
