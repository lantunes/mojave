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
package org.mojavemvc.forms;

import java.io.InputStream;

/**
 * @author Luis Antunes
 */
public class UploadedFile {

    private final String fileName;
    private final InputStream uploadedInputStream;
    private final String contentType;
    private final boolean isInMemory;
    private final long sizeInBytes;
    
    public UploadedFile(String fileName, InputStream uploadedInputStream, 
            String contentType, boolean isInMemory, long sizeInBytes) {
        
        this.fileName = fileName;
        this.uploadedInputStream = uploadedInputStream;
        this.contentType = contentType;
        this.isInMemory = isInMemory;
        this.sizeInBytes = sizeInBytes;
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public InputStream getInputStream() {
        return uploadedInputStream;
    }
    
    public String getContentType() {
        return contentType;
    }
    
    /**
     * Returns whether the file has been saved 
     * to a temporary location or is in memory.
     * 
     * @return true if the file is in memory
     */
    public boolean isInMemory() {
        return isInMemory;
    }
    
    /**
     * The size of the file in bytes.
     * 
     * @return size in bytes
     */
    public long getSize() {
        return sizeInBytes;
    }
}
