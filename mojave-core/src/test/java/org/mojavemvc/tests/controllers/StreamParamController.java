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

import java.io.InputStream;

import javax.servlet.ServletInputStream;

import org.mojavemvc.annotations.Action;
import org.mojavemvc.annotations.Param;
import org.mojavemvc.annotations.Resource;
import org.mojavemvc.annotations.StatelessController;
import org.mojavemvc.views.JSP;
import org.mojavemvc.views.View;

@StatelessController("stream-param")
public class StreamParamController {

    @Action
    public View inputStreamAlone(@Resource InputStream in) {
        String inputStreamClassName = getInputStreamClassName(in);
        return new JSP("some-controller").withAttribute("var", inputStreamClassName);
    }
    
    @Action
    public View inputStreamWithParam(@Param("p1") String p1, @Resource InputStream in) {
        String inputStreamClassName = getInputStreamClassName(in);
        return new JSP("some-controller").withAttribute("var", p1 + "-" + inputStreamClassName);
    }
    
    private String getInputStreamClassName(InputStream in) {
        String inputStreamClassName = "null";
        if (in != null && in instanceof ServletInputStream) {
            inputStreamClassName = "ServletInputStream";
        }
        return inputStreamClassName;
    }
}
