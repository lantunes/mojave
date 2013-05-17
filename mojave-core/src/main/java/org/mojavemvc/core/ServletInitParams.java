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
package org.mojavemvc.core;

import java.util.HashMap;
import java.util.Map;

import org.mojavemvc.initialization.InitParams;

/**
 * A copy of the ServletConfig init parameters.
 * 
 * @author Luis Antunes
 */
public class ServletInitParams implements InitParams {

    private final Map<String, String> params;
    
    public ServletInitParams(Map<String, String> params) {
        
        this.params = new HashMap<String, String>(params);
    }
    
    /**
     * Return the value of the init parameter, or null
     * if it does not exist
     * 
     * @param name
     * @return the value of the specified parameter, or null
     * if it does not exist
     */
    public String getParameter(String name) {
        
        return params.get(name);
    }
}
