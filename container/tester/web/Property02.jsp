<!--
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
-->
<html>
<head>
<title>Property02.jsp - Negative PropertyEditor Test</title>
</head>
<body bgcolor="white">
<jsp:useBean id="bean" scope="request"
          class="org.apache.tester.SessionBean"/>
<jsp:setProperty name="bean" property="dateProperty"
                value="07/25/200A"/>
Date property is '<jsp:getProperty name="bean" property="dateProperty"/>'.
</body>
</html>
