package org.mojavemvc.tests.marshallers;

import org.mojavemvc.views.PlainText;

public class CSV extends PlainText {

    public CSV(Object entity) {
        super(entity);
    }
    
    @Override
    public String getContentType() {

        return "text/csv";
    }

}
