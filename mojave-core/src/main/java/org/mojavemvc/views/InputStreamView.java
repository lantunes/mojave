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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * An instance of this class obtains the servlet's OutputStream, and copies 
 * the contents of the supplied InputStream to that stream. It sets the content type and 
 * content length (if available) before writing the content to the stream. It copies the
 * contents using a buffer. This class is suitable for returning larger entities, such
 * as files.
 * 
 * @author Luis Antunes
 */
public class InputStreamView implements View {

    private final InputStream in;
    private final String contentType;
    
    private int contentLength = -1;
    private int bufferSize = 1024 * 4;
    
    public InputStreamView(InputStream in, String contentType) {
        if (in == null) {
            throw new IllegalArgumentException("InputStream cannot be null");
        }
        this.in = in;
        this.contentType = contentType;
    }
    
    /**
     * Set the content length if it is known beforehand.
     * 
     * @param contentLength
     */
    public void setContentLength(int contentLength) {
        this.contentLength = contentLength;
    }
    
    /**
     * Set the buffer size to use a different buffer size
     * from the default (1024 * 4).
     * 
     * @param bufferSize
     */
    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }
    
    @Override
    public void render(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        if (contentType != null && contentType.trim().length() != 0) {
            response.setContentType(contentType);
        }
        
        if (contentLength != -1) {
            response.setContentLength(contentLength);
        }
        
        OutputStream out = response.getOutputStream();
        byte[] buffer = new byte[bufferSize];
        int n = 0;
        while (-1 != (n = in.read(buffer))) {
            out.write(buffer, 0, n);
        }
    }
}
