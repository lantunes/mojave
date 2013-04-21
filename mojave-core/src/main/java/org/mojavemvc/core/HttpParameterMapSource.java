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
package org.mojavemvc.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.mojavemvc.forms.UploadedFile;

/**
 * @author Luis Antunes
 */
public class HttpParameterMapSource implements ParameterMapSource {
    
    private final HttpServletRequest req;
    
    public HttpParameterMapSource(HttpServletRequest req) {
        this.req = req;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getParameterMap() {
        
        Map<String, Object> paramMap = null;
        boolean isMultipart = ServletFileUpload.isMultipartContent(req);
        
        if (isMultipart) {
            
            paramMap = getMapFromMultipartRequest(req);
            
        } else {
            /*
             * the map from the request may be unmodifiable,
             * so create a new map with the contents of the
             * request map
             */
            paramMap = new HashMap<String, Object>(req.getParameterMap());
        }
        
        return paramMap;
    }
    
    @SuppressWarnings("unchecked")
    private Map<String, Object> getMapFromMultipartRequest(HttpServletRequest req) {
        
        Map<String, Object> paramMap = new HashMap<String, Object>();
        try {
            ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory());
            List<FileItem> items = upload.parseRequest(req);
            for (FileItem item : items) {
                if (item.isFormField()) {
                    processFormField(item, paramMap);
                } else {
                    processUploadedFile(item, paramMap);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("error getting parameters from multipart request: ", e);
        }
        
        /* 
         * there may be parameters that are not part of the request body, 
         * such as from a query string; NOTE: if these share the same name as
         * a form parameter, the form parameter will be overwritten 
         */
        paramMap.putAll(req.getParameterMap());
        
        return paramMap;
    }
    
    private void processFormField(FileItem item, Map<String, Object> paramMap) {
        
        String name = item.getFieldName();
        String value = item.getString();
        /* handle multiple items with the same name; add to array */
        String[] valueArray = (String[])paramMap.get(name);
        if (valueArray != null) {
            List<String> valueList = Arrays.asList(valueArray);
            valueList.add(value);
            valueArray = valueList.toArray(new String[valueList.size()]);
        } else {
            valueArray = new String[]{value};
        }
        paramMap.put(name, valueArray);
    }
    
    private void processUploadedFile(FileItem item, 
            Map<String, Object> paramMap) throws IOException {
        
        String fieldName = item.getFieldName();
        String fileName = item.getName();
        String contentType = item.getContentType();
        boolean isInMemory = item.isInMemory();
        long sizeInBytes = item.getSize();
        InputStream uploadedStream = item.getInputStream();
        
        paramMap.put(fieldName, 
                new UploadedFile(fileName, uploadedStream, 
                        contentType, isInMemory, sizeInBytes));
    }
}
