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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.mojavemvc.core.HttpParameterMapSource;

/**
 * @author Luis Antunes
 */
public class TestHttpParameterMapSource {

    private HttpServletRequest req;
    
    @Before
    public void beforeEachTest() {
        
        req = mock(HttpServletRequest.class);
        when(req.getMethod()).thenReturn("GET");
    }
    
    @Test
    public void parameterMapExtractedFromRegularRequest() {
        
        HashMap<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("name", new String[]{"John"});
        when(req.getParameterMap()).thenReturn(paramMap);
        
        Map<String, Object> extracted = newParamMapSource().getParameterMap();
        
        assertEquals(paramMap, extracted);
    }
    
    /*
     * TODO
     * - set up an HttpServletRequest multipart request
     * to test with, and implement tests for extracting
     * a parameter map from multipart requests
     */
    
    /*----------------------*/
    
    private HttpParameterMapSource newParamMapSource() {
        return new HttpParameterMapSource(req);
    }
}
