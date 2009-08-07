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

<%@ include file="../users/header.jsp" %>

<!-- Body -->
<body bgcolor="white" background="../images/PaperTexture.gif">

<!--Form -->

<html:errors/>

<html:form action="/resources/listDataSources">

  <bean:define id="resourcetypeInfo" type="java.lang.String"
               name="dataSourcesForm" property="resourcetype"/>
  <html:hidden property="resourcetype"/>

  <bean:define id="pathInfo" type="java.lang.String"
               name="dataSourcesForm" property="path"/>
  <html:hidden property="path"/>

  <bean:define id="hostInfo" type="java.lang.String"
               name="dataSourcesForm" property="host"/>
  <html:hidden property="host"/>

  <bean:define id="domainInfo" type="java.lang.String"
               name="dataSourcesForm" property="domain"/>
  <html:hidden property="domain"/>

  <table width="100%" border="0" cellspacing="0" cellpadding="0">
    <tr bgcolor="7171A5">
      <td width="81%">
        <div class="page-title-text" align="left">
          <bean:message key="resources.actions.datasrc"/>
        </div>
      </td>
      <td width="19%">
        <div align="right">
          <%@ include file="listDataSources.jspf" %>
        </div>
      </td>
    </tr>
  </table>

<br>

<%@ include file="dataSources.jspf" %>

<br>
</html:form>

</body>
</html:html>
