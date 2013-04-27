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
import org.mojavemvc.annotations.Entity;
import org.mojavemvc.annotations.Expects;
import org.mojavemvc.annotations.StatelessController;
import org.mojavemvc.views.JSP;
import org.mojavemvc.views.View;

/**
 * @author Luis Antunes
 */
@StatelessController("marshalling")
public class MarshallingController {

    @Action("expects/plaintext/string")
    @Expects("text/plain")
    public View handlePlainTextWithString(@Entity String name) {
        return new JSP("param").withAttribute("var", name);
    }
    
    @Action("expects/plaintext/pojo")
    @Expects("text/plain")
    public View handlePlainTextWithPojo(@Entity SimplePojo pojo) {
        return new JSP("param").withAttribute("var", pojo.getVal());
    }
    
    @Action("expects/json")
    @Expects("application/json")
    public View handleJSON(@Entity SimplePojo pojo) {
        return new JSP("param").withAttribute("var", pojo.getVal());
    }
    
    @Action("expects/xml")
    @Expects("application/xml")
    public View handleXML(@Entity SimplePojo pojo) {
        return new JSP("param").withAttribute("var", pojo.getVal());
    }
    
    /*
     * NOTE: tests custom entity marshaller loading
     */
    @Action("expects/csv")
    @Expects("text/csv")
    public View handleCSV(@Entity CSVPojo pojo) {
        
        String[] vals = pojo.getVals();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < vals.length; i++) {
            sb.append(vals[i]);
            if (i+1 < vals.length) sb.append(",");
        }
        
        return new JSP("param").withAttribute("var", sb.toString());
    }
    
    /* TODO - once bug is fixed so that a @ParamPath can be specified with 
     * unrelated @Param arg
    @Action("expects")
    @ParamPath("plaintext/pojo/with/params")
    @Expects("text/plain")
    public View handlePlainTextWithPojoWithParams(@Entity SimplePojo pojo, 
            @Param("p1") String p1) {
        return new JSP("params2")
            .withAttribute("p1", p1)
            .withAttribute("p2", pojo.getVal());
    }
    */
    
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
    
    public static class CSVPojo {
        
        private String[] vals;
        
        public CSVPojo(String[] vals) {
            this.vals = vals;
        }
        
        public String[] getVals() {
            return vals;
        }
    }
}
