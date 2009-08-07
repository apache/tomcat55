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

--%><%
  String specials[] =
    { "javax.servlet.include.request_uri",
      "javax.servlet.include.context_path",
      "javax.servlet.include.servlet_path",
      "javax.servlet.include.path_info",
      "javax.servlet.include.query_string" };

  StringBuffer sb = new StringBuffer();
  if (request.getAttribute("Forward03") == null)
    sb.append(" Cannot retrieve forwarded attribute/");
  request.setAttribute("Forward03b", "This is our very own attribute");
  if (request.getAttribute("Forward03b") == null)
    sb.append(" Cannot retrieve our own attribute/");

  for (int i = 0; i < specials.length; i++) {
    if (request.getAttribute(specials[i]) != null) {
      sb.append(" Exposed attribute ");
      sb.append(specials[i]);
      sb.append("/");
    }
  }

  if (sb.length() < 1) {
    out.println("Forward03 PASSED");
  } else {
    out.print("Forward03 FAILED - ");
    out.println(sb.toString());
  }
%>
