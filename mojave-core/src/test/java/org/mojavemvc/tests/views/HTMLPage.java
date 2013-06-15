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
package org.mojavemvc.tests.views;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mojavemvc.initialization.AppProperties;
import org.mojavemvc.views.DataModelView;

/**
 * @author Luis Antunes
 */
public class HTMLPage extends DataModelView<HTMLPage> {

    private String h1Content;
    private String h2Content;
    private final List<Paragraph> paragraphs = new ArrayList<Paragraph>();
    
    public HTMLPage withH1Content(String h1Content) {
        this.h1Content = h1Content;
        return this;
    }
    
    public HTMLPage withH2Content(String h2Content) {
        this.h2Content = h2Content;
        return this;
    }
    
    public String getH1Content() {
        return h1Content;
    }
    
    public String getH2Content() {
        return h2Content;
    }
    
    public HTMLPage withParagraph(String id, String content) {
        this.paragraphs.add(new Paragraph(id, content));
        return this;
    }
    
    @Override
    public void render(HttpServletRequest request, HttpServletResponse response, 
            AppProperties properties)
            throws ServletException, IOException {

        PrintWriter out = response.getWriter();
        out.println("<html>");
        out.println("<body>");
        if (h1Content != null) {
            out.println("<h1>" + h1Content + "</h1>");
        }
        if (h2Content != null) {
            out.println("<h2>" + h2Content + "</h2>");
        }
        for (Paragraph p : paragraphs) {
            out.println("<p id=\"" + p.id + "\">" + p.content + "</p>");
        }
        out.println("</body>");
        out.println("</html>");
    }
    
    private class Paragraph {
        String id;
        String content;
        
        public Paragraph(String id, String content) {
            this.id = id;
            this.content = content;
        }
    }
}
