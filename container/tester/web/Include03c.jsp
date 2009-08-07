<%--
  Licensed to the Apache Software Foundation (ASF) under one or more
  contributor license agreements.  See the NOTICE file distributed with
  this work for additional information regarding copyright ownership.
  The ASF licenses this file to You under the Apache License, Version 2.0
  (the "License"); you may not use this file except in compliance with
  the License.  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.

--%><%@ page contentType="text/plain" %><%
  // Duplicate logic from "Include03.java"
  StringBuffer sb = new StringBuffer();
  String path = request.getParameter("path");
  if (path == null)
    path = "/Include03a";
  RequestDispatcher rd =
    getServletContext().getRequestDispatcher(path);
  if (rd == null) {
    sb.append(" No RequestDispatcher returned/");
  } else {
    rd.include(request, response);
  }
  response.resetBuffer();
  String value = null;
  try {
    value = (String) request.getAttribute(path.substring(1));
  } catch (ClassCastException e) {
      sb.append(" Returned attribute not of type String/");
  }
  if ((sb.length() < 1) && (value == null)) {
      sb.append(" No includee-created attribute was returned/");
  }
  if (sb.length() < 1)
    out.println("Include03c.jsp PASSED");
  else {
    out.print("Include03c.jsp FAILED -");
    out.println(sb.toString());
  }
%>
