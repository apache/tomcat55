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
<%@ page language="java" import="java.net.URLEncoder" contentType="text/html;charset=utf-8" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/controls.tld" prefix="controls" %>

<html:html locale="true">

<%@ include file="header.jsp" %>

<!-- Body -->
<body bgcolor="white" background="../images/PaperTexture.gif">

<!--Form -->

<html:errors/>

<html:form action="/users/listUsers">

  <table width="100%" border="0" cellspacing="0" cellpadding="0">
    <tr bgcolor="7171A5">
      <td width="81%">
        <div class="page-title-text" align="left">
          <bean:message key="users.deleteUsers.title"/>
        </div>
      </td>
      <td width="19%">
        <div align="right">
          <%@ include file="listUsers.jspf" %>
        </div>
      </td>
    </tr>
  </table>

</html:form>

<br>
<bean:define id="checkboxes" scope="page" value="true"/>
<html:form action="/users/deleteUsers">
  <%@ include file="../buttons.jsp" %>
  <br>
  <html:hidden property="databaseName"/>
  <%@ include file="users.jspf" %>
  <%@ include file="../buttons.jsp" %>
</html:form>
<br>

<%@ include file="footer.jsp" %>

</body>
</html:html>
