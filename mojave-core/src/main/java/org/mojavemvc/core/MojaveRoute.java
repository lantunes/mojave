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

import static org.bigtesting.routd.RouteHelper.*;

import org.bigtesting.routd.Route;

/**
 * @author Luis Antunes
 */
public class MojaveRoute extends Route {
    
    private final String controller;
    private final String action;
    private final String paramPath;
    
    public MojaveRoute(String controller, String action, String paramPath) {
        
        super(toRouteString(controller, action, paramPath));
        this.controller = controller;
        this.action = action;
        this.paramPath = paramPath;
    }
    
    private static String toRouteString(String controller, String action, String paramPath) {
        
        StringBuilder sb = new StringBuilder(PATH_ELEMENT_SEPARATOR);
        int appended = 0;
        appended = append(controller, sb, appended);
        appended = append(action, sb, appended);
        appended = append(paramPath, sb, appended);
        return sb.toString();
    }
    
    private static int append(String s, StringBuilder sb, int appended) {
        if (s != null && s.trim().length() > 0) {
            if (appended > 0) sb.append(PATH_ELEMENT_SEPARATOR);
            sb.append(s);
            appended++;
        }
        return appended;
    }
    
    public String getController() {
        return controller;
    }
    
    public String getAction() {
        return action;
    }
    
    public String getParamPath() {
        return paramPath;
    }
    
    public int hashCode() {
        int hash = 1;
        hash = hash * 17 + (controller == null ? 0 : controller.hashCode());
        hash = hash * 31 + (action == null ? 0 : action.hashCode());
        hash = hash * 13 + (paramPath == null ? 0 : paramPath.hashCode());
        return hash;
    }
    
    public boolean equals(Object o) {
        if (o == null) return false;
        if (o == this) return true;
        if (!(o instanceof MojaveRoute)) return false;
        MojaveRoute that = (MojaveRoute)o;
        return 
                (this.controller == null ? that.controller == null : 
                    this.controller.equals(that.controller)) && 
                (this.action == null ? that.action == null : 
                    this.action.equals(that.action)) && 
                (this.paramPath == null ? that.paramPath == null : 
                    this.paramPath.equals(that.paramPath));
    }
}
