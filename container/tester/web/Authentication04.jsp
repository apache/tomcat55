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
  StringBuffer results = new StringBuffer();
  String remoteUser = request.getRemoteUser();
  if (remoteUser == null) {
    results.append(" Not Authenticated/");
  } else if (!"tomcat".equals(remoteUser)) {
    results.append(" Authenticated as '");
    results.append(remoteUser);
    results.append("'/");
  }
  if (!request.isUserInRole("tomcat")) {
    results.append(" Not in role 'tomcat'/");
  }
  if (!request.isUserInRole("alias")) {
    results.append(" Not in role 'alias'/");
  }
  if (request.isUserInRole("unknown")) {
    results.append(" In role 'unknown'/");
  }
  if (results.length() < 1) {
    out.println("Authentication04 PASSED");
  } else {
    out.print("Authentication04 FAILED -");
    out.println(results.toString());
  }
%>
