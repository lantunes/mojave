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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mojavemvc.initialization.AppProperties;


/**
 * @author Luis Antunes
 */
public abstract class AbstractResponse<T extends AbstractResponse<T>> extends StreamView {

    private static final String RFC822_DATE = "EEE, dd MMM yyyy HH:mm:ss z";
    private static final String RFC1123_DATE = "EEE, dd MMM yyyy HH:mm:ss zzz";
    
    protected final int status;
    protected String contentType;
    protected byte[] content;
    private final Map<String, String> headers = new HashMap<String, String>();
    
    public AbstractResponse(int status) {
        this.status = status;
    }
    
    protected abstract T self();
    
    public T withContentType(String contentType) {
        this.contentType = contentType;
        return self();
    }
    
    public T withContent(byte[] content) {
        this.content = content;
        return self();
    }
    
    public T withContent(String content) {
        this.content = content.getBytes();
        return self();
    }
    
    public T withLanguage(String language) {
        headers.put(HTTPHeader.CONTENT_LANGUAGE, language);
        return self();
    }
    
    public T withLocation(String location) {
        headers.put(HTTPHeader.LOCATION, location);
        return self();
    }
    
    public T withContentLocation(String contentLocation) {
        headers.put(HTTPHeader.CONTENT_LOCATION, contentLocation);
        return self();
    }
    
    public T withTag(String tag) {
        headers.put(HTTPHeader.ETAG, tag);
        return self();
    }
    
    public T withExpires(Date date) {
        SimpleDateFormat df = new SimpleDateFormat(RFC822_DATE);
        headers.put(HTTPHeader.EXPIRES, df.format(date));
        return self();
    }
    
    public T withLastModified(Date date) {
        SimpleDateFormat df = new SimpleDateFormat(RFC1123_DATE, Locale.US);
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        headers.put(HTTPHeader.LAST_MODIFIED, df.format(date));
        return self();
    }
    
    public T withEncoding(String encoding) {
        headers.put(HTTPHeader.CONTENT_ENCODING, encoding);
        return self();
    }
    
    public T withHeader(String name, String value) {
        headers.put(name, value);
        return self();
    }
    
    /**
     * Returns the associated header value, or null
     * if the header has not been set.
     * 
     * @param headerName the name of the header
     * @return the header value associated with the given header name, or null
     * if the header has not been set
     */
    public String getHeaderValue(String headerName) {
        return headers.get(headerName);
    }
    
    /**
     * @return the HTTP status code
     */
    public int getStatus() {
        return status;
    }
    
    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public byte[] getPayload() {
        return content;
    }
    
    @Override
    public void render(HttpServletRequest request, HttpServletResponse response, 
            AppProperties properties) 
            throws ServletException, IOException {
        
        response.setStatus(status);
        
        for (Entry<String, String> header : headers.entrySet()) {
            response.setHeader(header.getKey(), header.getValue());
        }
        
        super.render(request, response, properties);
    }
}
