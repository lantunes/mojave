<html>
    <!--
     Copyright (C) 2011-2013 Mojavemvc.org
     
     Licensed under the Apache License, Version 2.0 (the "License");
     you may not use this file except in compliance with the License.
     You may obtain a copy of the License at
     
     http://www.apache.org/licenses/LICENSE-2.0
     
     Unless required by applicable law or agreed to in writing, software
     distributed under the License is distributed on an "AS IS" BASIS,
     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
     See the License for the specific language governing permissions and
     limitations under the License.
    -->
    <body>
        <p id="file-name"><%=request.getAttribute("file-name") %></p>
        <p id="file-size"><%=request.getAttribute("file-size") %></p>
        <p id="file-inmemory"><%=request.getAttribute("file-inmemory") %></p>
        <p id="file-contenttype"><%=request.getAttribute("file-contenttype") %></p>
        <p id="file-username"><%=request.getAttribute("file-username") %></p>
        <p id="file-content"><%=request.getAttribute("file-content") %></p>
        <p id="query-param"><%=request.getAttribute("query-param") %></p>
    </body>
</html>