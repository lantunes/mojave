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
package org.mojavemvc.tests.controllers;

import org.mojavemvc.annotations.Action;
import org.mojavemvc.annotations.DefaultAction;
import org.mojavemvc.annotations.Entity;
import org.mojavemvc.annotations.Expects;
import org.mojavemvc.annotations.Marshall;
import org.mojavemvc.annotations.Param;
import org.mojavemvc.annotations.ParamPath;
import org.mojavemvc.annotations.Returns;
import org.mojavemvc.annotations.StatelessController;
import org.mojavemvc.marshalling.Marshallable;
import org.mojavemvc.tests.views.HTMLPage;
import org.mojavemvc.views.View;

/**
 * @author Luis Antunes
 */
@StatelessController("marshalling")
public class MarshallingController {

    @DefaultAction
    @Returns("text/csv")
    @Expects("application/json")
    public CSVPojo defaultReturnXMLExpectJSON(@Entity SimplePojo pojo) {
        return new CSVPojo(new String[]{"marshalled-default", pojo.getVal()});
    }
    
    @Action("expects/plaintext/string")
    @Expects("text/plain")
    public View expectPlainTextWithString(@Entity String name) {
        return new HTMLPage()
            .withH2Content(name);
    }
    
    @Action("expects/plaintext/pojo")
    @Expects("text/plain")
    public View expectPlainTextWithPojo(@Entity SimplePojo pojo) {
        return new HTMLPage()
            .withH2Content(pojo.getVal());
    }
    
    @Action("expects/json")
    @Expects("application/json")
    public View expectJSON(@Entity SimplePojo pojo) {
        return new HTMLPage()
            .withH2Content(pojo.getVal());
    }
    
    @Action("expects/xml")
    @Expects("application/xml")
    public View expectXML(@Entity SimplePojo pojo) {
        return new HTMLPage()
            .withH2Content(pojo.getVal());
    }
    
    /*
     * NOTE: tests custom entity marshaller loading
     */
    @Action("expects/csv")
    @Expects("text/csv")
    public View expectCSV(@Entity CSVPojo pojo) {
        
        String[] vals = pojo.getVals();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < vals.length; i++) {
            sb.append(vals[i]);
            if (i+1 < vals.length) sb.append(",");
        }
        
        return new HTMLPage()
            .withH2Content(sb.toString());
    }
    
    @Action("returns/plaintext/string")
    @Returns("text/plain")
    public String returnPlainTextString() {
        return "marshalledStringText";
    }
    
    @Action("returns/plaintext/pojo")
    @Returns("text/plain")
    public SimplePojo returnPlainTextPojo() {
        return new SimplePojo("marshalledStringPojo");
    }
    
    @Action("returns/json")
    @Returns("application/json")
    public SimplePojo returnJSON() {
        return new SimplePojo("marshalledJSON");
    }
    
    @Action("returns/xml")
    @Returns("application/xml")
    public SimplePojo returnXML() {
        return new SimplePojo("marshalledXML");
    }
    
    @Action("returns/csv/expects/json")
    @Returns("text/csv")
    @Expects("application/json")
    public CSVPojo returnXMLExpectJSON(@Entity SimplePojo pojo) {
        return new CSVPojo(new String[]{"marshalledCSV", pojo.getVal()});
    }
    
    @Action("expects")
    @ParamPath("plaintext/pojo/with/params")
    @Expects("text/plain")
    public View expectPlainTextWithPojoWithParams(@Entity SimplePojo pojo, 
            @Param("p1") String p1) {
        return new HTMLPage()
            .withH2Content(p1 + ", " + pojo.getVal());
    }
    
    /* marshalling an embedded entity */
    
    @Action("returns/embedded/plaintext/pojo")
    @Returns("text/plain")
    public MarshallablePojo<SimplePojo> returnEmbeddedPlainTextPojo() {
        return new MarshallablePojo<SimplePojo>(
                new SimplePojo("marshalledStringPojo"));
    }
    
    @Action("returns/embedded/json")
    @Returns("application/json")
    public MarshallablePojo<SimplePojo> returnEmbeddedJSON() {
        return new MarshallablePojo<SimplePojo>(
                new SimplePojo("marshalledJSON"));
    }
    
    @Action("returns/embedded/xml")
    @Returns("application/xml")
    public AnnotatedPojo returnEmbeddedXML() {
        return new AnnotatedPojo(new SimplePojo("marshalledXML"));
    }
    
    
    /*------------------------------------*/
    
    public static class SimplePojo {
        private String val;
        
        public SimplePojo() {}
        
        public SimplePojo(String val) {
            this.val = val;
        }
        
        public String getVal() {
            return val;
        }
        
        public void setVal(String val) {
            this.val = val;
        }
        
        @Override
        public String toString() {
            return val;
        }
    }
    
    public static class MarshallablePojo<E> implements Marshallable<E> {

        private E entity;
        
        public MarshallablePojo(E entity) {
            this.entity = entity;
        }
        
        @Override
        public E getEntity() {
            return entity;
        }
    }
    
    public static class AnnotatedPojo {
        
        private Object entity;
        
        public AnnotatedPojo(Object entity) {
            this.entity = entity;
        }
        
        @Marshall
        public Object getReturnMessage() {
            return entity;
        }
    }
    
    public static class CSVPojo {
        
        private String[] vals;
        
        public CSVPojo(String[] vals) {
            this.vals = vals;
        }
        
        public String[] getVals() {
            return vals;
        }
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for(int i = 0; i < vals.length; i++) {
                sb.append(vals[i]);
                if (i + 1 < vals.length) sb.append(",");
            }
            return sb.toString();
        }
    }
}
