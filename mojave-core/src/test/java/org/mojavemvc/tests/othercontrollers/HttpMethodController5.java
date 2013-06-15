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
package org.mojavemvc.tests.othercontrollers;

import org.mojavemvc.annotations.DefaultAction;
import org.mojavemvc.annotations.StatelessController;
import org.mojavemvc.annotations.TRACEAction;
import org.mojavemvc.tests.views.HTMLView;
import org.mojavemvc.views.View;

/**
 * 
 * @author Luis Antunes
 */
@StatelessController("httpmethod5")
public class HttpMethodController5 {

    @DefaultAction
    public View defaultAction() {

        return new HTMLView()
            .withH2Content("Hello from " + "default");
    }

    @TRACEAction
    public View doTraceAction() {

        return new HTMLView()
            .withH2Content("Hello from " + "trace");
    }
}
