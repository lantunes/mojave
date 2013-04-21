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

import java.math.BigDecimal;
import java.sql.Date;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mojavemvc.forms.UploadedFile;

/**
 * @author Luis Antunes
 */
public class SignatureParameters {
    
    private static final Map<Class<?>, Parameter> PARAMETER_TYPE_MAP;
    
    static {
        Map<Class<?>, Parameter> map = new HashMap<Class<?>, Parameter>();
        
        /*
         * each of the Parameter instances are stateless, immutable and 
         * thus thread-safe, so the same instance can be shared by multiple threads
         */
        map.put(String.class       ,new StringParameter());
        map.put(String[].class     ,new StringArrayParameter());
        map.put(Integer.class      ,new IntegerParameter());
        map.put(int.class          ,new IntegerParameter());
        map.put(Integer[].class    ,new IntegerArrayParameter());
        map.put(int[].class        ,new PrimitiveIntegerArrayParameter());
        map.put(Long.class         ,new LongParameter());
        map.put(long.class         ,new LongParameter());
        map.put(Long[].class       ,new LongArrayParameter());
        map.put(long[].class       ,new PrimitiveLongArrayParameter());
        map.put(Double.class       ,new DoubleParameter());
        map.put(double.class       ,new DoubleParameter());
        map.put(Double[].class     ,new DoubleArrayParameter());
        map.put(double[].class     ,new PrimitiveDoubleArrayParameter());
        map.put(Date.class         ,new DateParameter());
        map.put(Date[].class       ,new DateArrayParameter());
        map.put(Boolean.class      ,new BooleanParameter());
        map.put(boolean.class      ,new BooleanParameter());
        map.put(Boolean[].class    ,new BooleanArrayParameter());
        map.put(boolean[].class    ,new PrimitiveBooleanArrayParameter());
        map.put(BigDecimal.class   ,new BigDecimalParameter());
        map.put(BigDecimal[].class ,new BigDecimalArrayParameter());
        map.put(UploadedFile.class ,new UploadedFileParameter());
        
        PARAMETER_TYPE_MAP = Collections.unmodifiableMap(map);
    }

    public static Parameter getParameterFromType(Class<?> paramType) {
        
        Parameter parameter = PARAMETER_TYPE_MAP.get(paramType);
        return parameter != null ? parameter : new UnknownParameter(paramType);
    }
    
    /*
     * an instance of this class must be stateless to permit safe concurrent access
     */
    public static abstract class Parameter {

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
    }
    
    public static abstract class ArrayParameter extends Parameter {
        
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
    
    /*
     * NOTE: a new instance must be created for each request
     */
    public static class UnknownParameter extends Parameter {
        
        private final Class<?> paramType;
        
        public UnknownParameter(Class<?> paramType) {
            this.paramType = paramType;
        }
        
        protected void populateArgsFromStringArray(String[] paramValues, List<Object> args) {
            throw new UnsupportedOperationException("unsupported parameter type: " + paramType.getName());
        }
    }
    
    public static class StringParameter extends Parameter {
        protected void populateArgsFromStringArray(String[] paramValues, List<Object> args) {
            args.add(paramValues == null ? null : paramValues[0]);
        }
    }
    
    public static class StringArrayParameter extends ArrayParameter {
        
        protected Object convertParamValues(String[] paramValues) {
            return paramValues;
        }
    }
    
    public static class IntegerParameter extends Parameter {
        
        protected void populateArgsFromStringArray(String[] paramValues, List<Object> args) {
            args.add(paramValues == null ? 0 : Integer.parseInt(paramValues[0]));
        }
        
        protected void populateArgsFromObject(Object paramValue, List<Object> args) {
            args.add(paramValue == null ? 0 : paramValue);
        }
    }
    
    public static class IntegerArrayParameter extends ArrayParameter {
        
        protected Object convertParamValues(String[] paramValues) {
            Integer[] vals = new Integer[paramValues.length];
            for (int k = 0; k < paramValues.length; vals[k] = Integer.parseInt(paramValues[k++]));
            return vals;
        }
    }
    
    public static class PrimitiveIntegerArrayParameter extends ArrayParameter {
        
        protected Object convertParamValues(String[] paramValues) {
            int[] vals = new int[paramValues.length];
            for (int k = 0; k < paramValues.length; vals[k] = Integer.parseInt(paramValues[k++]));
            return vals;
        }
    }
    
    public static class LongParameter extends Parameter {
        
        protected void populateArgsFromStringArray(String[] paramValues, List<Object> args) {
            args.add(paramValues == null ? 0 : Long.parseLong(paramValues[0]));
        }
        
        protected void populateArgsFromObject(Object paramValue, List<Object> args) {
            args.add(paramValue == null ? 0 : paramValue);
        }
    }
    
    public static class LongArrayParameter extends ArrayParameter {
        
        protected Object convertParamValues(String[] paramValues) {
            Long[] vals = new Long[paramValues.length];
            for (int k = 0; k < paramValues.length; vals[k] = Long.parseLong(paramValues[k++]));
            return vals;
        }
    }
    
    public static class PrimitiveLongArrayParameter extends ArrayParameter {
        
        protected Object convertParamValues(String[] paramValues) {
            long[] vals = new long[paramValues.length];
            for (int k = 0; k < paramValues.length; vals[k] = Long.parseLong(paramValues[k++]));
            return vals;
        }
    }
    
    public static class DoubleParameter extends Parameter {
        
        protected void populateArgsFromStringArray(String[] paramValues, List<Object> args) {
            args.add(paramValues == null ? 0 : Double.parseDouble(paramValues[0]));
        }
        
        protected void populateArgsFromObject(Object paramValue, List<Object> args) {
            args.add(paramValue == null ? 0 : paramValue);
        }
    }
    
    public static class DoubleArrayParameter extends ArrayParameter {
        
        protected Object convertParamValues(String[] paramValues) {
            Double[] vals = new Double[paramValues.length];
            for (int k = 0; k < paramValues.length; vals[k] = Double.parseDouble(paramValues[k++]));
            return vals;
        }
    }
    
    public static class PrimitiveDoubleArrayParameter extends ArrayParameter {
        
        protected Object convertParamValues(String[] paramValues) {
            double[] vals = new double[paramValues.length];
            for (int k = 0; k < paramValues.length; vals[k] = Double.parseDouble(paramValues[k++]));
            return vals;
        }
    }
    
    public static class DateParameter extends Parameter {
        protected void populateArgsFromStringArray(String[] paramValues, List<Object> args) {
            args.add(paramValues == null ? null : Date.valueOf(paramValues[0]));
        }
    }
    
    public static class DateArrayParameter extends ArrayParameter {
        
        protected Object convertParamValues(String[] paramValues) {
            Date[] vals = new Date[paramValues.length];
            for (int k = 0; k < paramValues.length; vals[k] = Date.valueOf(paramValues[k++]));
            return vals;
        }
    }
    
    public static class BooleanParameter extends Parameter {
        
        protected void populateArgsFromStringArray(String[] paramValues, List<Object> args) {
            args.add(paramValues == null ? false : getBooleanValue(paramValues[0]));
        }
        
        protected void populateArgsFromObject(Object paramValue, List<Object> args) {
            args.add(paramValue == null ? false : paramValue);
        }
    }
    
    public static class BooleanArrayParameter extends ArrayParameter {
        
        protected Object convertParamValues(String[] paramValues) {
            Boolean[] vals = new Boolean[paramValues.length];
            for (int k = 0; k < paramValues.length; vals[k] = getBooleanValue(paramValues[k++]));
            return vals;
        }
    }
    
    public static class PrimitiveBooleanArrayParameter extends ArrayParameter {
        
        protected Object convertParamValues(String[] paramValues) {
            boolean[] vals = new boolean[paramValues.length];
            for (int k = 0; k < paramValues.length; vals[k] = getBooleanValue(paramValues[k++]));
            return vals;
        }
    }
    
    public static class UploadedFileParameter extends Parameter {
        @Override
        protected void populateArgsFromStringArray(String[] paramValues, List<Object> args) {
            throw new UnsupportedOperationException("unsupported operation for an uploaded file parameter");
        }
    }
    
    public static class BigDecimalParameter extends Parameter {
        
        protected void populateArgsFromStringArray(String[] paramValues, List<Object> args) {
            args.add(paramValues == null ? BigDecimal.ZERO : new BigDecimal(paramValues[0]));
        }
        
        protected void populateArgsFromObject(Object paramValue, List<Object> args) {
            args.add(paramValue == null ? BigDecimal.ZERO : paramValue);
        }
    }
    
    public static class BigDecimalArrayParameter extends ArrayParameter {
        
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
