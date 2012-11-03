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
package org.mojavemvc.tests.interceptors;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mojavemvc.annotations.AfterAction;
import org.mojavemvc.annotations.BeforeAction;
import org.mojavemvc.aop.RequestContext;

/**
 * @author Luis Antunes
 */
public class Interceptor10 {

    public static List<String> invocationList;

    @BeforeAction
    public void beforeAction(RequestContext ctx) {

        HttpServletRequest req = ctx.getRequest();
        if (req == null) {
            throw new RuntimeException("request null");
        }
        HttpServletResponse resp = ctx.getResponse();
        if (resp == null) {
            throw new RuntimeException("response null");
        }
        Object[] parameters = ctx.getParameters();
        if (parameters == null || parameters.length != 1) {
            throw new RuntimeException("parameters not set");
        }
        if (!(parameters[0] instanceof String)) {
            throw new RuntimeException("parameter is of wrong type");
        }
        String action = ctx.getAction();
        if (!"param".equals(action)) {
            throw new RuntimeException("action incorrect");
        }
        String controller = ctx.getController();
        if (!"intercepted12".equals(controller)) {
            throw new RuntimeException("controller incorrect");
        }

        invocationList.add("interceptor10-beforeAction");
    }

    @AfterAction
    public void afterAction(RequestContext ctx) {

        HttpServletRequest req = ctx.getRequest();
        if (req == null) {
            throw new RuntimeException("request null");
        }
        HttpServletResponse resp = ctx.getResponse();
        if (resp == null) {
            throw new RuntimeException("response null");
        }
        Object[] parameters = ctx.getParameters();
        if (parameters == null || parameters.length != 1) {
            throw new RuntimeException("parameters not set");
        }
        if (!(parameters[0] instanceof String)) {
            throw new RuntimeException("parameter is of wrong type");
        }
        String action = ctx.getAction();
        if (!"param".equals(action)) {
            throw new RuntimeException("action incorrect");
        }
        String controller = ctx.getController();
        if (!"intercepted12".equals(controller)) {
            throw new RuntimeException("controller incorrect");
        }

        invocationList.add("interceptor10-afterAction");
    }
}
