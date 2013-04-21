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

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.mojavemvc.annotations.Model;
import org.mojavemvc.annotations.Param;
import org.mojavemvc.annotations.Resource;
import org.mojavemvc.forms.Submittable;
import org.mojavemvc.forms.UploadedFile;
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
     * someSignature(@Param("p1") String p1, @Model SomeModel Model, 
     *  @Param("p2") Date p2, @Resource InputStream in, @Param("p3") UploadedFile file)
     * 
     * this array will look like:
     * 
     * ["p1"][String.class]
     * [SomeForm.class][List<PropertyDescriptor>-beanProperties]
     * ["p2"][Date.class]
     * [InputStream.class][InputStream.class]
     * ["p3"][UploadedFile.class]
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

    public BaseActionSignature(int fastIndex, String methodName, Class<?>[] paramTypes, 
            Annotation[][] paramAnnotations) {

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
                } else if (annotation instanceof Resource) {
                    paramTypeMap[i] = new Object[] { paramTypes[i], paramTypes[i] };
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
    public Object[] getArgs(Map<String, ?> parametersMap, InputStream servletInputStream) {

        List<Object> args = new ArrayList<Object>();

        for (int i = 0; i < paramTypeMap.length; i++) {
            Object[] mapRow = paramTypeMap[i];
            Object key = mapRow[0];
            Object value = mapRow[1];
            if (key instanceof String && value instanceof Class<?>) {

                Object paramValue = parametersMap.get((String) key);
                
                Parameter param = Parameter.getParameterFromType((Class<?>) value);
                param.populateArgs(args, paramValue);

            } else if (key instanceof Class && value instanceof List) {

                populateArgsForForms((Class<?>) key, parametersMap, (List<PropertyDescriptor>) value, args);
                
            } else if (key instanceof Class && value.equals(InputStream.class)) {
                
                if (servletInputStream == null) {
                    throw new RuntimeException("an InputStream is a requested parameter but none has been provided");
                }
                args.add(servletInputStream);
            }
        }

        return args.toArray();
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
                /* there should be only one parameter type */
                Class<?> setterParamType = setter.getParameterTypes()[0];
                List<Object> setterArgs = new ArrayList<Object>();
                
                Parameter param = Parameter.getParameterFromType(setterParamType);
                param.populateArgs(setterArgs, val);

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

    private static abstract class Parameter {

        public void populateArgs(List<Object> args, Object paramValue) {
            if (paramValue instanceof String[]) {
                populateArgsFromStringArray((String[]) paramValue, args);
            } else {
                populateArgsFromObject(paramValue, args);
            }
        }
        
        protected abstract void populateArgsFromStringArray(String[] paramValues, List<Object> args);
        
        protected void populateArgsFromObject(Object paramValue, List<Object> args) {
            args.add(paramValue == null ? null : paramValue);
        }
        
        public static Parameter getParameterFromType(Class<?> paramType) {
            
            if (paramType.equals(String.class)) {
                return new StringParameter();
            } else if (paramType.equals(String[].class)) {
                return new StringArrayParameter();
            } else if (paramType.equals(Integer.class) || paramType.equals(int.class)) {
                return new IntegerParameter();
            } else if (paramType.equals(Integer[].class)) {
                return new IntegerArrayParameter();
            } else if (paramType.equals(int[].class)) {
                return new PrimitiveIntegerArrayParameter();
            } else if (paramType.equals(Long.class) || paramType.equals(long.class)) {
                return new LongParameter();
            } else if (paramType.equals(Long[].class)) {
                return new LongArrayParameter();
            } else if (paramType.equals(long[].class)) {
                return new PrimitiveLongArrayParameter();
            } else if (paramType.equals(Double.class) || paramType.equals(double.class)) {
                return new DoubleParameter();
            } else if (paramType.equals(Double[].class)) {
                return new DoubleArrayParameter();
            } else if (paramType.equals(double[].class)) {
                return new PrimitiveDoubleArrayParameter();
            } else if (paramType.equals(Date.class)) {
                return new DateParameter();
            } else if (paramType.equals(Date[].class)) {
                return new DateArrayParameter();
            } else if (paramType.equals(Boolean.class) || paramType.equals(boolean.class)) {
                return new BooleanParameter();
            } else if (paramType.equals(Boolean[].class)) {
                return new BooleanArrayParameter();
            } else if (paramType.equals(boolean[].class)) {
                return new PrimitiveBooleanArrayParameter();
            } else if (paramType.equals(BigDecimal.class)) {
                return new BigDecimalParameter();
            } else if (paramType.equals(BigDecimal[].class)) {
                return new BigDecimalArrayParameter();
            } else if (paramType.equals(UploadedFile.class)) {
                return new UploadedFileParameter();
            } else {
                return new UnknownParameter(paramType);
            }
        }
    }
    
    private static abstract class ArrayParameter extends Parameter {
        
        protected void populateArgsFromStringArray(String[] paramValues, List<Object> args) {
            if (paramValues != null) {
                Object vals = convertParamValues(paramValues);
                args.add(vals);
            } else {
                args.add(null);
            }
        }
        
        protected abstract Object convertParamValues(String[] paramValues);
    }
    
    private static class UnknownParameter extends Parameter {
        
        private Class<?> paramType;
        
        public UnknownParameter(Class<?> paramType) {
            this.paramType = paramType;
        }
        
        protected void populateArgsFromStringArray(String[] paramValues, List<Object> args) {
            throw new UnsupportedOperationException("unsupported parameter type: " + paramType.getName());
        }
    }
    
    private static class StringParameter extends Parameter {
        protected void populateArgsFromStringArray(String[] paramValues, List<Object> args) {
            args.add(paramValues == null ? null : paramValues[0]);
        }
    }
    
    private static class StringArrayParameter extends ArrayParameter {
        
        protected Object convertParamValues(String[] paramValues) {
            return paramValues;
        }
    }
    
    private static class IntegerParameter extends Parameter {
        
        protected void populateArgsFromStringArray(String[] paramValues, List<Object> args) {
            args.add(paramValues == null ? 0 : Integer.parseInt(paramValues[0]));
        }
        
        protected void populateArgsFromObject(Object paramValue, List<Object> args) {
            args.add(paramValue == null ? 0 : paramValue);
        }
    }
    
    private static class IntegerArrayParameter extends ArrayParameter {
        
        protected Object convertParamValues(String[] paramValues) {
            Integer[] vals = new Integer[paramValues.length];
            for (int k = 0; k < paramValues.length; vals[k] = Integer.parseInt(paramValues[k++]));
            return vals;
        }
    }
    
    private static class PrimitiveIntegerArrayParameter extends ArrayParameter {
        
        protected Object convertParamValues(String[] paramValues) {
            int[] vals = new int[paramValues.length];
            for (int k = 0; k < paramValues.length; vals[k] = Integer.parseInt(paramValues[k++]));
            return vals;
        }
    }
    
    private static class LongParameter extends Parameter {
        
        protected void populateArgsFromStringArray(String[] paramValues, List<Object> args) {
            args.add(paramValues == null ? 0 : Long.parseLong(paramValues[0]));
        }
        
        protected void populateArgsFromObject(Object paramValue, List<Object> args) {
            args.add(paramValue == null ? 0 : paramValue);
        }
    }
    
    private static class LongArrayParameter extends ArrayParameter {
        
        protected Object convertParamValues(String[] paramValues) {
            Long[] vals = new Long[paramValues.length];
            for (int k = 0; k < paramValues.length; vals[k] = Long.parseLong(paramValues[k++]));
            return vals;
        }
    }
    
    private static class PrimitiveLongArrayParameter extends ArrayParameter {
        
        protected Object convertParamValues(String[] paramValues) {
            long[] vals = new long[paramValues.length];
            for (int k = 0; k < paramValues.length; vals[k] = Long.parseLong(paramValues[k++]));
            return vals;
        }
    }
    
    private static class DoubleParameter extends Parameter {
        
        protected void populateArgsFromStringArray(String[] paramValues, List<Object> args) {
            args.add(paramValues == null ? 0 : Double.parseDouble(paramValues[0]));
        }
        
        protected void populateArgsFromObject(Object paramValue, List<Object> args) {
            args.add(paramValue == null ? 0 : paramValue);
        }
    }
    
    private static class DoubleArrayParameter extends ArrayParameter {
        
        protected Object convertParamValues(String[] paramValues) {
            Double[] vals = new Double[paramValues.length];
            for (int k = 0; k < paramValues.length; vals[k] = Double.parseDouble(paramValues[k++]));
            return vals;
        }
    }
    
    private static class PrimitiveDoubleArrayParameter extends ArrayParameter {
        
        protected Object convertParamValues(String[] paramValues) {
            double[] vals = new double[paramValues.length];
            for (int k = 0; k < paramValues.length; vals[k] = Double.parseDouble(paramValues[k++]));
            return vals;
        }
    }
    
    private static class DateParameter extends Parameter {
        protected void populateArgsFromStringArray(String[] paramValues, List<Object> args) {
            args.add(paramValues == null ? null : Date.valueOf(paramValues[0]));
        }
    }
    
    private static class DateArrayParameter extends ArrayParameter {
        
        protected Object convertParamValues(String[] paramValues) {
            Date[] vals = new Date[paramValues.length];
            for (int k = 0; k < paramValues.length; vals[k] = Date.valueOf(paramValues[k++]));
            return vals;
        }
    }
    
    private static class BooleanParameter extends Parameter {
        
        protected void populateArgsFromStringArray(String[] paramValues, List<Object> args) {
            args.add(paramValues == null ? false : getBooleanValue(paramValues[0]));
        }
        
        protected void populateArgsFromObject(Object paramValue, List<Object> args) {
            args.add(paramValue == null ? false : paramValue);
        }
    }
    
    private static class BooleanArrayParameter extends ArrayParameter {
        
        protected Object convertParamValues(String[] paramValues) {
            Boolean[] vals = new Boolean[paramValues.length];
            for (int k = 0; k < paramValues.length; vals[k] = getBooleanValue(paramValues[k++]));
            return vals;
        }
    }
    
    private static class PrimitiveBooleanArrayParameter extends ArrayParameter {
        
        protected Object convertParamValues(String[] paramValues) {
            boolean[] vals = new boolean[paramValues.length];
            for (int k = 0; k < paramValues.length; vals[k] = getBooleanValue(paramValues[k++]));
            return vals;
        }
    }
    
    private static class UploadedFileParameter extends Parameter {
        @Override
        protected void populateArgsFromStringArray(String[] paramValues, List<Object> args) {
            throw new UnsupportedOperationException("unsupported operation for an uploaded file parameter");
        }
    }
    
    private static class BigDecimalParameter extends Parameter {
        
        protected void populateArgsFromStringArray(String[] paramValues, List<Object> args) {
            args.add(paramValues == null ? BigDecimal.ZERO : new BigDecimal(paramValues[0]));
        }
        
        protected void populateArgsFromObject(Object paramValue, List<Object> args) {
            args.add(paramValue == null ? BigDecimal.ZERO : paramValue);
        }
    }
    
    private static class BigDecimalArrayParameter extends ArrayParameter {
        
        protected Object convertParamValues(String[] paramValues) {
            BigDecimal[] vals = new BigDecimal[paramValues.length];
            for (int k = 0; k < paramValues.length; vals[k] = new BigDecimal(paramValues[k++]));
            return vals;
        }
    }
    
    private static boolean getBooleanValue(String val) {
        if (val != null && val.trim().length() != 0) {
            val = val.toLowerCase();
            if (val.equals("true") || val.equals("t") || val.equals("1")) {
                return true;
            } else if (val.equals("false") || val.equals("f") || val.equals("0")) {
                return false;
            }
        }
        throw new IllegalArgumentException("boolean value not in correct format('t','true','1';'f','false','0'): "
                + (val == null ? "null" : val));
    }
}