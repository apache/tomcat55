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

--%><%@ page contentType="text/plain" isErrorPage="true" %><%

        // Accumulate all the reasons this request might fail
        StringBuffer sb = new StringBuffer();
        Object value = null;

        if (exception == null) {
            sb.append(" exception is missing/");
        } else {
            if (!(exception instanceof java.lang.ArrayIndexOutOfBoundsException)) {
                sb.append(" exception class is ");
                sb.append(exception.getClass().getName());
                sb.append("/");
            }
            if (!"ErrorPage09 Threw ArrayIndexOutOfBoundsException".equals(exception.getMessage())) {
                sb.append(" exception message is ");
                sb.append(exception.getMessage());
                sb.append("/");
            }
        }

        // Report ultimate success or failure
        if (sb.length() < 1)
            out.println("ErrorPage10 PASSED");
        else
            out.println("ErrorPage10 FAILED -" + sb.toString());

%>
<%
  out.println("EXCEPTION:  " + exception);
%>
