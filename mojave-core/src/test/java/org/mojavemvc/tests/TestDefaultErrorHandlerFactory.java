package org.mojavemvc.tests;

import static org.junit.Assert.*;

import org.junit.Test;
import org.mojavemvc.exception.DefaultErrorHandlerFactory;
import org.mojavemvc.exception.DefaultJspErrorHandler;
import org.mojavemvc.exception.ErrorHandler;

public class TestDefaultErrorHandlerFactory {

    @Test
    public void createErrorHandler() throws Exception {

        DefaultErrorHandlerFactory factory = new DefaultErrorHandlerFactory();
        ErrorHandler errorHandler = factory.createErrorHandler();

        assertTrue(errorHandler instanceof DefaultJspErrorHandler);
    }
}
