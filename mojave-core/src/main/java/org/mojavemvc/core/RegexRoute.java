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

import java.util.regex.Pattern;

/**
 * @author Luis Antunes
 */
public class RegexRoute {

    private final Route route;
    private final Pattern pattern;
    
    public RegexRoute(Route route) {
        this.route = route;
        this.pattern = compilePattern();
    }
    
    private Pattern compilePattern() {
        String[] tokens = route.toString().substring(1).split(PATH_ELEMENT_SEPARATOR);
        StringBuilder routeRegex = new StringBuilder("^").append(PATH_ELEMENT_SEPARATOR);
        for (int i = 0; i < tokens.length; i++) {
            if (i > 0) routeRegex.append(PATH_ELEMENT_SEPARATOR);
            String currentToken = tokens[i];
            if (currentToken.startsWith(PARAM_PREFIX)) {
                currentToken = currentToken.substring(1);
                int customRegexIdx = currentToken.indexOf(CUSTOM_REGEX_START);
                if (customRegexIdx == -1) {
                    routeRegex.append("([^").append(PATH_ELEMENT_SEPARATOR).append("]+)");
                } else {
                    String customRegex = currentToken.substring(customRegexIdx + 1, 
                            currentToken.indexOf(CUSTOM_REGEX_END));
                    routeRegex.append("(").append(customRegex).append(")");
                }
                
            } else {
                routeRegex.append(currentToken);
            }
        }
        routeRegex.append("$");
        return Pattern.compile(routeRegex.toString());
    }
    
    public Pattern pattern() {
        return pattern;
    }
    
    public Route getRoute() {
        return route;
    }
    
    public String toString() {
        return pattern.toString();
    }
    
    public int hashCode() {
        return route.hashCode();
    }
    
    public boolean equals(Object o) {
        if (o == null) return false;
        if (o == this) return true;
        if (!(o instanceof RegexRoute)) return false;
        RegexRoute that = (RegexRoute)o;
        return this.route.equals(that.route);
    }
}
