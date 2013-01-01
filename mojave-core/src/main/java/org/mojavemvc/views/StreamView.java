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
package org.mojavemvc.views;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * An instance of this class obtains the servlet's OutputStream, and writes the
 * payload to that stream. It sets the content type and content length before
 * writing the content to the stream.
 * 
 * @author Luis Antunes
 */
public abstract class StreamView implements View {

    /**
     * Gets the content type of the response to be streamed back to the
     * requestor.
     * 
     * @return the content type of the response
     */
    protected abstract String getContentType();

    /**
     * Gets the content of the response to be streamed back to the requestor.
     * 
     * @return the payload content
     */
    protected abstract byte[] getPayload();

    @Override
    public void render(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String contentType = getContentType();
        if (contentType != null && contentType.trim().length() != 0) {
            response.setContentType(contentType);
        }
        
        byte[] buf = getPayload();
        if (buf != null) {
            response.setContentLength(buf.length);
            ServletOutputStream servletOut = response.getOutputStream();
            servletOut.write(buf);
        }
    }
}
