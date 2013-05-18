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
package org.mojavemvc.freemarker;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * @author Luis Antunes
 */
public class FileTemplateSource implements TemplateSource {

    private final File file;
    
    public FileTemplateSource(File file) {
        
        this.file = file;
    }
    
    @Override
    public void close() throws IOException {
        /* do nothing */
    }

    @Override
    public Reader getReader(String encoding) throws IOException {

        return new InputStreamReader(new FileInputStream(file), encoding);
    }

    @Override
    public long getLastModified() {

        return file.lastModified();
    }
    
    @Override
    public boolean equals(Object o) {
        
        if (o == null) return false;
        if (o == this) return true;
        
        if (o instanceof FileTemplateSource) {
            return file.equals(((FileTemplateSource) o).file);
        }
        
        return false;
    }

    @Override
    public int hashCode() {
        
        return file.hashCode();
    }

    @Override
    public String toString() {
        
        return file.toString();
    }
}