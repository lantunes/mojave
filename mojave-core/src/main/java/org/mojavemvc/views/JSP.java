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
package org.mojavemvc.views;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mojavemvc.initialization.AppProperties;

/**
 * An instance of this class is not thread-safe, and should not be cached and/or
 * re-used.
 * <p>
 * JSP files used in the framework must have a .jsp extension. The JSP servlet in
 * the container must not be re-mapped to handle files with other extensions.
 * 
 * @author Luis Antunes
 */
public class JSP implements View {
    
    public static final String PATH_PROPERTY = "mojavemvc-internal-jsp-path";

    private static final String DOT_JSP = ".jsp";
    
    protected final String jsp;

    protected Map<String, Object> attributes = new HashMap<String, Object>();

    /**
     * Requires the name of the underlying JSP file. If the name is not suffixed
     * with .jsp, the suffix will be added.
     * 
     * @param jsp the name of the underlying JSP file represented by this View
     */
    public JSP(String jsp) {

        if (jsp == null || jsp.trim().length() == 0) { 
            throw new IllegalArgumentException("jsp name is null or empty");
        }
        
        if (!jsp.toLowerCase().endsWith(DOT_JSP)) {
            jsp += DOT_JSP;
        }
        
        this.jsp = jsp;
    }

    public JSP(String jsp, String[] keys, Object[] values) {

        this(jsp);
        setAttributesFromPairs(keys, values);
    }

    public Map<String, Object> getAttributes() {

        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {

        this.attributes = attributes;
    }

    public Object getAttribute(String key) {

        return attributes.get(key);
    }

    public JSP withAttribute(String key, Object value) {

        setAttribute(key, value);
        return this;
    }

    public void setAttribute(String key, Object value) {

        attributes.put(key, value);
    }

    public JSP withAttributesFromPairs(String[] keys, Object[] values) {

        setAttributesFromPairs(keys, values);
        return this;
    }

    public void setAttributesFromPairs(String[] keys, Object[] values) {

        for (int i = 0; i < keys.length; i++) {

            attributes.put(keys[i], values[i]);
        }
    }

    public JSP withModel(Object javaBean) {

        setModel(javaBean);
        return this;
    }

    public void setModel(Object javaBean) {

        try {

            BeanInfo info = Introspector.getBeanInfo(javaBean.getClass());
            for (PropertyDescriptor propertyDescriptor : info.getPropertyDescriptors()) {
                String propertyName = propertyDescriptor.getName();
                if (!propertyName.equals("class")) {
                    Method getter = propertyDescriptor.getReadMethod();
                    attributes.put(propertyName, getter.invoke(javaBean, new Object[] {}));
                }
            }

        } catch (Exception e) {
            /* ignore */
        }
    }

    /**
     * <p>
     * This method is meant to facilitate testing by providing a means of easily
     * getting the model representation described in the class's attributes.
     * </p>
     * 
     * @param <T>
     *            the model class type
     * @param modelClass
     *            the model class
     * @return an instance of the model with values set from any attributes
     * @throws Exception
     */
    public <T> T getModel(Class<T> modelClass) throws Exception {

        BeanInfo info = Introspector.getBeanInfo(modelClass);

        T model = modelClass.newInstance();

        if (attributes != null && !attributes.isEmpty()) {

            Map<String, PropertyDescriptor> properties = new HashMap<String, PropertyDescriptor>();
            PropertyDescriptor[] descriptors = info.getPropertyDescriptors();
            for (PropertyDescriptor pd : descriptors) {

                if (properties.get(pd.getName()) != null) {

                    throw new IllegalStateException("property already exists in bean");
                }
                properties.put(pd.getName(), pd);
            }

            for (String key : attributes.keySet()) {

                PropertyDescriptor pd = properties.get(key);
                /* there can be non-model attributes in the map */
                if (pd != null) {

                    Method setter = pd.getWriteMethod();
                    setter.invoke(model, new Object[] { attributes.get(key) });
                }
            }
        }

        return model;
    }

    /**
     * Returns the name of the underlying JSP file.
     * It will be suffixed with .jsp.
     * 
     * @return the jsp page
     */
    public String getJSPName() {

        return jsp;
    }

    @Override
    public void render(HttpServletRequest request, HttpServletResponse response, 
            AppProperties properties) throws ServletException, IOException {

        if (attributes != null && !attributes.isEmpty()) {

            for (String key : attributes.keySet()) {

                request.setAttribute(key, attributes.get(key));
            }
        }

        String jspPath = properties.getProperty(PATH_PROPERTY);
        RequestDispatcher dispatcher = request.getRequestDispatcher(jspPath + jsp);
        dispatcher.forward(request, response);
    }
}
