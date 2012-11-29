/*
 * Copyright (C) 2011-2012 Mojavemvc.org
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

import static org.mojavemvc.util.RouteHelper.*;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.mojavemvc.core.Route.PathParameterElement;
import org.mojavemvc.exception.NoMatchingRouteException;

/**
 * @author Luis Antunes
 */
public class HttpRequestRouter implements RequestRouter {

    private final HttpServletRequest req;
    private final RouteMap routeMap;
    
    public HttpRequestRouter(HttpServletRequest req, RouteMap routeMap) {
        this.req = req;
        this.routeMap = routeMap;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public RoutedRequest route() {
        
        String controller = null;
        String action = null;
        String path = req.getPathInfo();
        Map<String, Object> paramMap = (Map<String, Object>) req.getParameterMap();
        
        if (path != null && path.startsWith(PATH_ELEMENT_SEPARATOR)) {
            
            Route route = routeMap.getRoute(path);
            
            if (route == null) {
                throw new NoMatchingRouteException(
                        "no matching routes were found for " + path);
            }
            
            controller = route.getController();
            action = route.getAction();
            handleParameters(path, paramMap, route);
        }
        
        return new RoutedRequest(controller, action, paramMap);
    }

    private void handleParameters(String path, 
            Map<String, Object> paramMap, Route route) {
        
        List<PathParameterElement> paramElements = 
                route.pathParameterElements();
        if (!paramElements.isEmpty()) {
            
            /*
             * we matched the path, so we can assume that there
             * are the required number of tokens in the request
             * path as there are in the route path
             */
            String[] pathTokens = getPathElements(path);
            for (PathParameterElement elem : paramElements) {
                paramMap.put(elem.name(), pathTokens[elem.index()]);
            }
        }
    }
}
