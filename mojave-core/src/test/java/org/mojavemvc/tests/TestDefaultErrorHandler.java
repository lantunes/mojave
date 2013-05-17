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
package org.mojavemvc.tests;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.mojavemvc.exception.DefaultErrorHandler;
import org.mojavemvc.exception.ErrorHandler;
import org.mojavemvc.initialization.AppProperties;
import org.mojavemvc.views.PlainText;
import org.mojavemvc.views.View;

/**
 * @author Luis Antunes
 */
public class TestDefaultErrorHandler {
    
    private AppProperties properties;

    @Before
    public void beforeEachTest() {
        properties = mock(AppProperties.class);
    }
    
    @Test
    public void handleError_NullException() throws Exception {

        ErrorHandler errorHandler = new DefaultErrorHandler();
        View view = errorHandler.handleError(null, properties);

        assertTrue(view instanceof PlainText);
        PlainText stackTrace = (PlainText) view;
        assertEquals("", stackTrace.toString());
    }
    
    @Test
    public void handleError_WithException() throws Exception {

        Exception e = new Exception("testing");
        
        ErrorHandler errorHandler = new DefaultErrorHandler();
        View view = errorHandler.handleError(e, properties);

        assertTrue(view instanceof PlainText);
        PlainText stackTrace = (PlainText) view;
        assertTrue(stackTrace.toString().startsWith("java.lang.Exception: testing"));
    }
}
