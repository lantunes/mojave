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
package org.mojavemvc;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mojavemvc.core.HttpMethod;
import org.mojavemvc.core.MojaveFramework;

/**
 * The RequestFilter uses the Mojave framework to handle
 * requests. Either this filter or the {@link FrontController} servlet must
 * be used in a Mojave Web MVC application, but not both. This filter
 * does <b>not</b> call the next filter in the filter chain.
 * 
 * @author Luis Antunes
 */
public class RequestFilter implements Filter {

    private MojaveFramework framework;
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

        framework = new MojaveFramework();
        framework.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) 
            throws IOException, ServletException {

        HttpServletRequest servletReq = (HttpServletRequest) req;
        HttpServletResponse servletRes = (HttpServletResponse) res;
        
        HttpMethod httpMethod = getHttpMethod(servletReq);
        
        framework.handleRequest(servletReq, servletRes, httpMethod, 
                servletReq.getServletPath());
    }
    
    private HttpMethod getHttpMethod(HttpServletRequest req) {
        
        if (req.getMethod().equalsIgnoreCase("GET")) {
            
            return HttpMethod.GET;
            
        } else if (req.getMethod().equalsIgnoreCase("POST")) {
            
            return HttpMethod.POST;
            
        } else if (req.getMethod().equalsIgnoreCase("PUT")) {
            
            return HttpMethod.PUT;
            
        } else if (req.getMethod().equalsIgnoreCase("DELETE")) {
            
            return HttpMethod.DELETE;
            
        } else if (req.getMethod().equalsIgnoreCase("HEAD")) {
            
            return HttpMethod.HEAD;
            
        } else if (req.getMethod().equalsIgnoreCase("OPTIONS")) {
            
            return HttpMethod.OPTIONS;
            
        } else if (req.getMethod().equalsIgnoreCase("TRACE")) {
            
            return HttpMethod.TRACE;
            
        }
            
        throw new RuntimeException("unknown or unsupported " +
                "request method: " + req.getMethod());
    }

    @Override
    public void destroy() {
    }
}
