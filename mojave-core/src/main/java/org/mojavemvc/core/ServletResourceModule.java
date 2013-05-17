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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.mojavemvc.initialization.AppProperties;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

/**
 * 
 * @author Luis Antunes
 */
public class ServletResourceModule extends AbstractModule {

    private static final ThreadLocal<HttpServletRequest> threadRequest = new ThreadLocal<HttpServletRequest>();

    private static final ThreadLocal<HttpServletResponse> threadResponse = new ThreadLocal<HttpServletResponse>();

    /*
     * an instance of this class is read-only, and is created during 
     * application initialization; it is essentially global in scope,
     * and has no thread-specific information; therefore, it does
     * not need its own ThreadLocal
     */
    private final AppProperties appProperties;
    
    public ServletResourceModule(AppProperties appProperties) {
        
        this.appProperties = appProperties;
    }
    
    @Override
    protected void configure() {
    }
    
    @Provides
    AppProperties providesAppProperties() {
        
        return appProperties;
    }

    @Provides
    HttpServletRequest provideRequest() {

        return threadRequest.get();
    }

    @Provides
    HttpServletResponse provideResponse() {

        return threadResponse.get();
    }

    @Provides
    HttpSession provideSession() {

        HttpServletRequest request = threadRequest.get();
        if (request != null) {
            return request.getSession();
        }
        return null;
    }

    public static void set(HttpServletRequest req, HttpServletResponse resp) {

        threadRequest.set(req);
        threadResponse.set(resp);
    }
}
