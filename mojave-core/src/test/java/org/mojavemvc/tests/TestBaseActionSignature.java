/*
 * Copyright (C) 2011 Mojavemvc.org
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
package org.mojavemvc.tests;

import static org.junit.Assert.*;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.mojavemvc.annotations.Param;
import org.mojavemvc.core.ActionSignature;
import org.mojavemvc.core.BaseActionSignature;

/**
 * 
 * @author Luis Antunes
 */
public class TestBaseActionSignature {

    @Test
    public void noParameters() {

        ActionSignature sig = new BaseActionSignature(1, "testAction", new Class[] {}, new Annotation[][] {});

        assertEquals(0, sig.parameterTypes().length);
        assertEquals(0, sig.getArgs(new HashMap<String, Object>()).length);
    }

    @Test
    public void withParameters1() {

        ActionSignature sig = new BaseActionSignature(1, "testAction", new Class[] { String.class, Integer.class,
                Double.class, Date.class }, new Annotation[][] { { createParam("p1") }, { createParam("p2") },
                { createParam("p3") }, { createParam("p4") } });

        Map<String, String[]> parameterMap = new HashMap<String, String[]>();
        parameterMap.put("p1", new String[] { "hello" });
        parameterMap.put("p2", new String[] { "123" });
        parameterMap.put("p3", new String[] { "1.23" });
        parameterMap.put("p4", new String[] { "2011-03-01" });

        Class<?>[] paramTypes = sig.parameterTypes();
        assertEquals(4, paramTypes.length);
        assertEquals(String.class, paramTypes[0]);
        assertEquals(Integer.class, paramTypes[1]);
        assertEquals(Double.class, paramTypes[2]);
        assertEquals(Date.class, paramTypes[3]);
        Object[] args = sig.getArgs(parameterMap);
        assertEquals(4, args.length);
        assertEquals("hello", args[0]);
        assertEquals(123, args[1]);
        assertEquals(1.23, args[2]);
        assertEquals(Date.valueOf("2011-03-01"), args[3]);
    }

    @Test
    public void withParameters2() {

        ActionSignature sig = new BaseActionSignature(1, "testAction", new Class[] { String.class, Integer.class,
                Double.class, Date.class, SomeUserDefinedType.class }, new Annotation[][] { { createParam("p1") },
                { createParam("p2") }, { createParam("p3") }, { createParam("p4") }, { createParam("p5") } });

        SomeUserDefinedType userDefinedType = new SomeUserDefinedType();

        Map<String, Object> parameterMap = new HashMap<String, Object>();
        parameterMap.put("p1", "hello");
        parameterMap.put("p2", 123);
        parameterMap.put("p3", 1.23);
        parameterMap.put("p4", Date.valueOf("2011-03-01"));
        parameterMap.put("p5", userDefinedType);

        Class<?>[] paramTypes = sig.parameterTypes();
        assertEquals(5, paramTypes.length);
        assertEquals(String.class, paramTypes[0]);
        assertEquals(Integer.class, paramTypes[1]);
        assertEquals(Double.class, paramTypes[2]);
        assertEquals(Date.class, paramTypes[3]);
        assertEquals(SomeUserDefinedType.class, paramTypes[4]);
        Object[] args = sig.getArgs(parameterMap);
        assertEquals(5, args.length);
        assertEquals("hello", args[0]);
        assertEquals(123, args[1]);
        assertEquals(1.23, args[2]);
        assertEquals(Date.valueOf("2011-03-01"), args[3]);
        assertEquals(userDefinedType, args[4]);
    }

    @SuppressWarnings("serial")
    private class SomeUserDefinedType implements Serializable {
    }

    private Param createParam(final String val) {
        return new Param() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return Param.class;
            }

            @Override
            public String value() {
                return val;
            }
        };
    }
}
