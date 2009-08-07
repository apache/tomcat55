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

        // Write a FAILED message that should get replaced by the error text
        out.println("ErrorPage08 FAILED - Original response returned");

        // Throw the specified exception
        String type = request.getParameter("type");
        if ("Arithmetic".equals(type)) {
            throw new ArithmeticException
                ("ErrorPage08 Threw ArithmeticException");
        } else if ("Array".equals(type)) {
            throw new ArrayIndexOutOfBoundsException
                ("ErrorPage08 Threw ArrayIndexOutOfBoundsException");
        } else if ("Number".equals(type)) {
            throw new NumberFormatException
                ("ErrorPage08 Threw NumberFormatException");
        }

%>
