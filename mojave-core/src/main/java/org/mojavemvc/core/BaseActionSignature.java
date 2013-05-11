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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.mojavemvc.annotations.Entity;
import org.mojavemvc.annotations.Model;
import org.mojavemvc.annotations.Param;
import org.mojavemvc.annotations.Resource;
import org.mojavemvc.core.SignatureParameters.Parameter;
import org.mojavemvc.forms.Submittable;
import org.mojavemvc.marshalling.DefaultEntityMarshaller;
import org.mojavemvc.marshalling.EntityMarshaller;
import org.mojavemvc.views.View;
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
     * [Resource.class][InputStream.class]
     * ["p3"][UploadedFile.class]
     * [Entity.class][SomePojo.class]
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
    
    private final EntityMarshaller paramMarshaller;
    
    private final EntityMarshaller viewMarshaller;

    public BaseActionSignature(int fastIndex, String methodName, Class<?>[] paramTypes, 
            Annotation[][] paramAnnotations) {
        this(fastIndex, methodName, paramTypes, paramAnnotations, null, new DefaultEntityMarshaller());
    }
    
    public BaseActionSignature(int fastIndex, String methodName, Class<?>[] paramTypes, 
            Annotation[][] paramAnnotations, EntityMarshaller paramMarshaller, 
            EntityMarshaller viewMarshaller) {

        this.fastIndex = fastIndex;
        this.methodName = methodName;
        this.paramTypeMap = new Object[paramTypes.length][2];
        this.paramMarshaller = paramMarshaller;
        this.viewMarshaller = viewMarshaller;

        int i = 0;
        for (Annotation[] annotationsForParam : paramAnnotations) {
            for (Annotation annotation : annotationsForParam) {
                if (annotation instanceof Param) {
                    paramTypeMap[i] = new Object[] { ((Param) annotation).value(), paramTypes[i] };
                } else if (annotation instanceof Model) {
                    paramTypeMap[i] = new Object[] { paramTypes[i], getFormTypes(paramTypes[i]) };
                } else if (annotation instanceof Resource) {
                    paramTypeMap[i] = new Object[] { Resource.class, paramTypes[i] };
                } else if (annotation instanceof Entity) {
                    paramTypeMap[i] = new Object[] { Entity.class, paramTypes[i] };
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
    
    public View marshall(Object entity) {
        return viewMarshaller.marshall(entity);
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
                
                Parameter param = SignatureParameters.getParameterFromType((Class<?>) value);
                param.populateArgs(args, paramValue);

            } else if (key instanceof Class && value instanceof List) {

                populateArgsForForms((Class<?>) key, parametersMap, (List<PropertyDescriptor>) value, args);
                
            } else if (key.equals(Resource.class)) {
                
                if (servletInputStream == null) {
                    throw new RuntimeException("an InputStream is a requested parameter but none has been provided");
                }
                args.add(servletInputStream);
                
            } else if (key.equals(Entity.class)) {
                
                if (paramMarshaller == null) {
                    throw new RuntimeException("a parameter entity needs to be unmarshalled but no param marshaller exists");
                }
                args.add(paramMarshaller.unmarshall(servletInputStream, (Class<?>)value));
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
                
                Parameter param = SignatureParameters.getParameterFromType(setterParamType);
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
}