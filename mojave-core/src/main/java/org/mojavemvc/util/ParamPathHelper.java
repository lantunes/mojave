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
package org.mojavemvc.util;

import static org.bigtesting.routd.RouteHelper.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.mojavemvc.annotations.Param;

/**
 * 
 * @author Luis Antunes
 */
public class ParamPathHelper {

    public static String[] getParamNamesFrom(String paramPath) {
        
        List<String> params = new ArrayList<String>();
        paramPath = CUSTOM_REGEX_PATTERN.matcher(paramPath).replaceAll("");
        String[] tokens = paramPath.split(PATH_ELEMENT_SEPARATOR);
        for (int i = 0; i < tokens.length; i++) {
            String currentElement = tokens[i];
            if (currentElement.startsWith(PARAM_PREFIX)) {
                currentElement = currentElement.substring(1);
                params.add(currentElement);
            }
        }
        
        return params.toArray(new String[params.size()]);
    }
    
    public static String[] getParamNamesFrom(Method method) {
        
        List<String> methodParamsList = new ArrayList<String>();
        Annotation[][] paramAnnotations = method.getParameterAnnotations();
        for (Annotation[] annotationsForParam : paramAnnotations) {
            for (Annotation annotation : annotationsForParam) {
                if (annotation instanceof Param) {
                    methodParamsList.add(((Param) annotation).value());
                }
            }
        }
        return methodParamsList.toArray(new String[methodParamsList.size()]);
    }
}
