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

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.mojavemvc.annotations.Model;
import org.mojavemvc.annotations.Param;
import org.mojavemvc.forms.Submittable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * There is one instance of this class in the application per &#064;Action
 * method per controller. It is immutable once created, and all of its public
 * methods are thread-safe.
 * 
 * @author Luis Antunes
 */
public class BaseActionSignature implements ActionSignature {

    protected static final Logger logger = LoggerFactory.getLogger(BaseActionSignature.class);

    /*
     * the method name of the @Action method
     */
    private final String methodName;

    /*
     * Given the following signature:
     * 
     * someSignature( @Param("p1") String p1, @Form SomeForm form, @Param("p2")
     * Date p2 )
     * 
     * this array will look like:
     * 
     * ["p1"][String.class]
     * [SomeForm.class][List<PropertyDescriptor>-beanProperties]
     * ["p1"][Date.class]
     */
    private final Object[][] paramTypeMap;

    /*
     * an array of the method's parameter types in the order in which they are
     * declared
     */
    private final Class<?>[] parameterTypes;

    /*
     * the cglib method index
     */
    private final int fastIndex;

    public BaseActionSignature(int fastIndex, String methodName, Class<?>[] paramTypes, Annotation[][] paramAnnotations) {

        this.fastIndex = fastIndex;
        this.methodName = methodName;
        this.paramTypeMap = new Object[paramTypes.length][2];

        int i = 0;
        for (Annotation[] annotationsForParam : paramAnnotations) {
            for (Annotation annotation : annotationsForParam) {
                if (annotation instanceof Param) {
                    paramTypeMap[i] = new Object[] { ((Param) annotation).value(), paramTypes[i] };
                } else if (annotation instanceof Model) {
                    paramTypeMap[i] = new Object[] { paramTypes[i], getFormTypes(paramTypes[i]) };
                }
                i++;
            }
        }

        parameterTypes = paramTypes;
    }

    public String methodName() {

        return methodName;
    }

    public Class<?>[] parameterTypes() {

        return parameterTypes;
    }

    public int fastIndex() {

        return fastIndex;
    }

    public List<Class<?>> getInterceptorClasses(ControllerDatabase controllerDb, Class<?> controllerClass, String action) {

        return controllerDb.getInterceptorsForAction(controllerClass, action);
    }

    @SuppressWarnings("unchecked")
    public Object[] getArgs(Map<String, ?> parametersMap) {

        List<Object> args = new ArrayList<Object>();

        for (int i = 0; i < paramTypeMap.length; i++) {
            Object[] mapRow = paramTypeMap[i];
            Object key = mapRow[0];
            Object value = mapRow[1];
            if (key instanceof String && value instanceof Class<?>) {

                Object paramValue = parametersMap.get((String) key);
                if (paramValue instanceof String[]) {
                    populateArgsFromStringArray((Class<?>) value, (String[]) paramValue, args);
                } else {
                    populateArgsFromObject((Class<?>) value, paramValue, args);
                }

            } else if (key instanceof Class && value instanceof List) {

                populateArgsForForms((Class<?>) key, parametersMap, (List<PropertyDescriptor>) value, args);
            }
        }

        return args.toArray();
    }

    private void populateArgsFromObject(Class<?> paramType, Object paramValue, List<Object> args) {

        if (paramType.equals(Integer.class) || paramType.equals(int.class)) {
            args.add(paramValue == null ? 0 : paramValue);
        } else if (paramType.equals(Double.class) || paramType.equals(double.class)) {
            args.add(paramValue == null ? 0 : paramValue);
        } else if (paramType.equals(Boolean.class) || paramType.equals(boolean.class)) {
            args.add(paramValue == null ? false : paramValue);
        } else {
            args.add(paramValue == null ? null : paramValue);
        }
    }

    private void populateArgsFromStringArray(Class<?> paramType, String[] paramValues, List<Object> args) {

        if (paramType.equals(String.class)) {
            args.add(paramValues == null ? null : paramValues[0]);
        } else if (paramType.equals(String[].class)) {
            args.add(paramValues == null ? null : paramValues);
        } else if (paramType.equals(Integer.class) || paramType.equals(int.class)) {
            args.add(paramValues == null ? 0 : Integer.parseInt(paramValues[0]));
        } else if (paramType.equals(Integer[].class)) {
            if (paramValues != null) {
                Integer[] ints = new Integer[paramValues.length];
                for (int k = 0; k < paramValues.length; k++) {
                    ints[k] = Integer.parseInt(paramValues[k]);
                }
                args.add(ints);
            } else {
                args.add(null);
            }
        } else if (paramType.equals(int[].class)) {
            if (paramValues != null) {
                int[] ints = new int[paramValues.length];
                for (int k = 0; k < paramValues.length; k++) {
                    ints[k] = Integer.parseInt(paramValues[k]);
                }
                args.add(ints);
            } else {
                args.add(null);
            }
        } else if (paramType.equals(Double.class) || paramType.equals(double.class)) {
            args.add(paramValues == null ? 0 : Double.parseDouble(paramValues[0]));
        } else if (paramType.equals(Double[].class)) {
            if (paramValues != null) {
                Double[] vals = new Double[paramValues.length];
                for (int k = 0; k < paramValues.length; k++) {
                    vals[k] = Double.parseDouble(paramValues[k]);
                }
                args.add(vals);
            } else {
                args.add(null);
            }
        } else if (paramType.equals(double[].class)) {
            if (paramValues != null) {
                double[] vals = new double[paramValues.length];
                for (int k = 0; k < paramValues.length; k++) {
                    vals[k] = Double.parseDouble(paramValues[k]);
                }
                args.add(vals);
            } else {
                args.add(null);
            }
        } else if (paramType.equals(Date.class)) {
            args.add(paramValues == null ? null : Date.valueOf(paramValues[0]));
        } else if (paramType.equals(Date[].class)) {
            if (paramValues != null) {
                Date[] vals = new Date[paramValues.length];
                for (int k = 0; k < paramValues.length; k++) {
                    vals[k] = Date.valueOf(paramValues[k]);
                }
                args.add(vals);
            } else {
                args.add(null);
            }
        } else if (paramType.equals(Boolean.class) || paramType.equals(boolean.class)) {
            args.add(paramValues == null ? false : getBooleanValue(paramValues[0]));
        } else if (paramType.equals(Boolean[].class)) {
            if (paramValues != null) {
                Boolean[] vals = new Boolean[paramValues.length];
                for (int k = 0; k < paramValues.length; k++) {
                    vals[k] = getBooleanValue(paramValues[k]);
                }
                args.add(vals);
            } else {
                args.add(null);
            }
        } else if (paramType.equals(boolean[].class)) {
            if (paramValues != null) {
                boolean[] vals = new boolean[paramValues.length];
                for (int k = 0; k < paramValues.length; k++) {
                    vals[k] = getBooleanValue(paramValues[k]);
                }
                args.add(vals);
            } else {
                args.add(null);
            }
        }
    }

    private void populateArgsForForms(Class<?> formType, Map<String, ?> parametersMap, List<PropertyDescriptor> params,
            List<Object> args) {

        try {

            Object formBean = formType.newInstance();

            /* set the properties on the bean */
            for (PropertyDescriptor propertyDescriptor : params) {
                /* val can be a String or String[] */
                Object val = parametersMap.get(propertyDescriptor.getName());
                /* get the write method */
                Method setter = propertyDescriptor.getWriteMethod();
                Class<?> setterParamType = setter.getParameterTypes()[0]; /*
                                                                           * there
                                                                           * should
                                                                           * be
                                                                           * only
                                                                           * one
                                                                           */
                List<Object> setterArgs = new ArrayList<Object>();
                if (val instanceof String[]) {
                    populateArgsFromStringArray(setterParamType, (String[]) val, setterArgs);
                } else {
                    populateArgsFromObject(setterParamType, val, setterArgs);
                }

                setter.invoke(formBean, setterArgs.toArray());
            }

            if (formBean instanceof Submittable) {
                ((Submittable) formBean).onSubmit();
            }

            args.add(formBean);

        } catch (Exception e) {
            logger.error("error populating args for forms", e);
        }
    }

    private List<PropertyDescriptor> getFormTypes(Class<?> formType) {

        List<PropertyDescriptor> params = new ArrayList<PropertyDescriptor>();

        try {

            BeanInfo info = Introspector.getBeanInfo(formType);
            for (PropertyDescriptor propertyDescriptor : info.getPropertyDescriptors()) {
                if (!propertyDescriptor.getName().equals("class")) {
                    params.add(propertyDescriptor);
                }
            }

        } catch (IntrospectionException e) {
            /* ignore */
        }

        return params;
    }

    private boolean getBooleanValue(String val) {
        if (val != null && val.trim().length() != 0) {
            val = val.toLowerCase();
            if (val.equals("true") || val.equals("t") || val.equals("1")) {
                return true;
            } else if (val.equals("false") || val.equals("f") || val.equals("0")) {
                return false;
            }
        }
        throw new IllegalArgumentException("boolean value not in correct " + "format('t','true','1';'f','false','0'): "
                + (val == null ? "null" : val));
    }
}