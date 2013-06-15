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
package org.mojavemvc.tests.jsp;

import static org.junit.Assert.*;

import org.junit.Test;
import org.mojavemvc.views.JSP;

/**
 * @author Luis Antunes
 */
public class TestJSPView {

    @Test
    public void testJspView_getJSPName() throws Exception {

        JSP view = new JSP("test.jsp");
        assertEquals("test.jsp", view.getJSPName());
    }

    @Test
    public void testJspView_getJSPName_noSuffix() throws Exception {

        JSP view = new JSP("test");
        assertEquals("test.jsp", view.getJSPName());
    }
}
