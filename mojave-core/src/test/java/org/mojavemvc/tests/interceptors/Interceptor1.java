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
package org.mojavemvc.tests.interceptors;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.mojavemvc.annotations.AfterAction;
import org.mojavemvc.annotations.BeforeAction;
import org.mojavemvc.tests.services.SomeService;

import com.google.inject.Inject;

/**
 * @author Luis Antunes
 */
public class Interceptor1 {

    @Inject
    private HttpServletRequest req;

    @Inject
    private HttpServletResponse resp;

    @Inject
    private HttpSession sess;

    @Inject
    private SomeService someService;

    /*
     * used to test that state is maintained between calls to the before and
     * after actions
     */
    private String someStatefulVar = "";

    public static List<String> invocationList;

    @BeforeAction
    public void beforeAction() {

        StringBuffer sb = new StringBuffer();
        sb.append(req != null ? ":req" : ":null");
        sb.append(resp != null ? ":resp" : ":null");
        sb.append(sess != null ? ":sess" : ":null");
        sb.append(someService != null ? ":someService" : ":null");
        invocationList.add("interceptor1-beforeAction" + sb.toString());

        someStatefulVar = "ok";
    }

    @AfterAction
    public void afterAction() {

        StringBuffer sb = new StringBuffer();
        sb.append(req != null ? ":req" : ":null");
        sb.append(resp != null ? ":resp" : ":null");
        sb.append(sess != null ? ":sess" : ":null");
        sb.append(someService != null ? ":someService" : ":null");
        sb.append(":" + someStatefulVar);
        invocationList.add("interceptor1-afterAction" + sb.toString());
    }
}
