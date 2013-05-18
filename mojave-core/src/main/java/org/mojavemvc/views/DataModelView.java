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
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Luis Antunes
 */
public abstract class DataModelView<T extends DataModelView<T>> implements View {

    protected Map<String, Object> attributes = new HashMap<String, Object>();
    
    public Map<String, Object> getAttributes() {

        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {

        this.attributes = attributes;
    }

    public Object getAttribute(String key) {

        return attributes.get(key);
    }

    @SuppressWarnings("unchecked")
    public T withAttribute(String key, Object value) {

        setAttribute(key, value);
        return (T)this;
    }

    public void setAttribute(String key, Object value) {

        attributes.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public T withAttributesFromPairs(String[] keys, Object[] values) {

        setAttributesFromPairs(keys, values);
        return (T)this;
    }

    public void setAttributesFromPairs(String[] keys, Object[] values) {

        for (int i = 0; i < keys.length; i++) {

            attributes.put(keys[i], values[i]);
        }
    }

    @SuppressWarnings("unchecked")
    public T withModel(Object javaBean) {

        setModel(javaBean);
        return (T)this;
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
     * @param <M>
     *            the model class type
     * @param modelClass
     *            the model class
     * @return an instance of the model with values set from any attributes
     * @throws Exception
     */
    public <M> M getModel(Class<M> modelClass) throws Exception {

        BeanInfo info = Introspector.getBeanInfo(modelClass);

        M model = modelClass.newInstance();

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
}
