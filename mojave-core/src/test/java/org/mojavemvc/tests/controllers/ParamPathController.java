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

import org.mojavemvc.annotations.Action;
import org.mojavemvc.annotations.DefaultAction;
import org.mojavemvc.annotations.Param;
import org.mojavemvc.annotations.ParamPath;
import org.mojavemvc.annotations.StatelessController;
import org.mojavemvc.tests.views.HTMLView;
import org.mojavemvc.views.View;

@StatelessController("parampath")
public class ParamPathController {

    @DefaultAction
    @ParamPath("say/:name")
    public View defaultAction(@Param("name") String name) {
        return new HTMLView()
            .withH2Content("Hello from " + name);
    }
    
    @Action("regex")
    @ParamPath(":name<[a-z]+>")
    public View regexAction(@Param("name") String name) {
        return new HTMLView()
            .withH2Content("Hello from " + name);        
    }
    
    @Action("multi")
    @ParamPath(":name/:id<[0-9]+>")
    public View multiParamAction(@Param("name") String name, @Param("id") int id) {
        return new HTMLView()
            .withH2Content("Hello from " + name + ", " + id);
        
    }
    
    @Action("encoded")
    @ParamPath("a+b/:name/:id<[0-9]+>")
    public View encodedAction(@Param("name") String name, @Param("id") int id) {
        return new HTMLView()
            .withH2Content("Hello from " + name + ", " + id);        
    }
}
