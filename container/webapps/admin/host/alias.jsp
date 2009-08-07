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

<html:form method="POST" action="/SaveAlias">

  <html:hidden property="hostName"/>

  <table width="100%" border="0" cellspacing="0" cellpadding="0">
    <tr class="page-title-row">
      <td align="left" nowrap>
        <div class="page-title-text" align="left">
            <bean:message key="actions.alias.create"/>
        </div>
      </td>
    </tr>
  </table>

<br>

 <table border="0" cellspacing="0" cellpadding="0" width="100%">
    <tr> <td> <div class="table-title-text">
        <bean:message key="new.alias"/>
    </div> </td> </tr>
  </table>

  <table class="back-table" border="0" cellspacing="0" cellpadding="1" width="100%">
    <tr>
      <td>
        <controls:table tableStyle="front-table" lineStyle="line-row">
            <controls:row header="true"
                labelStyle="table-header-text" dataStyle="table-header-text">
            <controls:label>
                <bean:message key="service.property"/>
            </controls:label>
            <controls:data>
                <bean:message key="service.value"/>
            </controls:data>
        </controls:row>

        <controls:row labelStyle="table-label-text" dataStyle="table-normal-text" styleId="aliasName">
            <controls:label>
                <bean:message key="host.alias.name"/>:
            </controls:label>
            <controls:data>
              <html:text property="aliasName" size="24" maxlength="128" styleId="aliasName"/>
            </controls:data>
        </controls:row>
      </controls:table>
      </td>
    </tr>
  </table>

  <%@ include file="../buttons.jsp" %>

<br>

<%-- Aliases List --%>
 <table border="0" cellspacing="0" cellpadding="0" width="100%">
    <tr> <td>
        <div class="table-title-text">
            <bean:message key="host.aliases"/>
        </div>
    </td> </tr>
  </table>

  <table class="back-table" border="0" cellspacing="0" cellpadding="1" width="100%">
    <tr> <td>
        <table class="front-table" border="1" cellspacing="0" cellpadding="0" width="100%">
          <tr class="header-row">
            <td width="27%">
              <div class="table-header-text" align="left"><bean:message key="host.alias.name"/> </div>
            </td> </tr>

            <logic:iterate id="aliasVal" name="aliasForm" property="aliasVals">
            <tr> <td width="27%" valign="top" colspan=2>
                <div class="table-normal-text"> <%= aliasVal %> </div>
            </td> </tr>
            </logic:iterate>
         </table>

    </td> </tr>
  </table>

  <%@ include file="../buttons.jsp" %>

</html:form>
</body>

</html:html>
