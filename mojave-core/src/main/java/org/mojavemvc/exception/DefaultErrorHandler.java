/*
 * Copyright (C) 2011-2012 Mojavemvc.org
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
package org.mojavemvc.exception;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import org.mojavemvc.views.PlainText;

/**
 * The default {@link ErrorHandler} for the application. Simply 
 * returns a printed stack trace.
 * 
 * @author Luis Antunes
 */
public class DefaultErrorHandler implements ErrorHandler {

    public PlainText handleError(Throwable e) {
        
        String message = "";

        if (e != null) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PrintWriter writer = new PrintWriter(out);
            try {
                e.printStackTrace(writer);
            } finally {
                try {
                    writer.flush();
                    out.flush();
                    out.close();
                    writer.close();
                } catch (IOException ex) {
                    /* do nothing */
                }
            }
            message = out.toString();
        }
        
        return new PlainText(message);
    }
}
