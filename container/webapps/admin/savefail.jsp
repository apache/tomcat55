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
<!-- Standard Struts Entries -->

<%@ page language="java" contentType="text/html;charset=utf-8" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>

<html:html locale="true">

  <body bgcolor="white" background="images/PaperTexture.gif">

    <%-- Cause our tree control to refresh itself --%>
    <script language="JavaScript">
      <!--
        parent.tree.location='treeControlTest.do';
      -->
    </script>

    <%@ include file="header.jsp" %>
    <center><h2>
    <%-- display warnings if any --%>
    <logic:present name="warning">
            <bean:message key="warning.header"/>
    </h2></center>
    <h3><center>
            <bean:message key='<%= (String) request.getAttribute("warning") %>'/>
            <br>
    </logic:present>
    </h3></center>
    <center><h2>
      <bean:message key="save.fail"/>
    </h2></center>

    <%@ include file="footer.jsp" %>

  </body>

</html:html>
