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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author Luis Antunes
 */
public class URLTemplateSource implements TemplateSource {
    
    private final URL url;
    private URLConnection conn;
    private InputStream in;

    URLTemplateSource(URL url) throws IOException {
        
        this.url = url;
        this.conn = url.openConnection();
    }

    @Override
    public boolean equals(Object o) {
        
        if (o == null) return false;
        if (o == this) return true;
        
        if (o instanceof URLTemplateSource) {
            return url.equals(((URLTemplateSource) o).url);
        }
        
        return false;
    }

    @Override
    public int hashCode() {
        
        return url.hashCode();
    }

    @Override
    public String toString() {
        
        return url.toString();
    }
    
    @Override
    public long getLastModified() {
        
        return conn.getLastModified();
    }

    @Override
    public void close() throws IOException {
        
        try {
          if (in != null) in.close();
        } finally {
          in = null;
          conn = null;
        }
    }

    @Override
    public Reader getReader(String encoding) throws IOException {
        
        in = conn.getInputStream();
        return new InputStreamReader(in, encoding);
    }
}