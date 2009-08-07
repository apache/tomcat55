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

<% // Force the initialization of "action" servlet
   getServletContext().getNamedDispatcher("action").include(request,response);
%> 

<html:html locale="true">

<!-- Make sure window is not in a frame -->

<script language="JavaScript" type="text/javascript">

  <!--
    if (window.self != window.top) {
      window.open(".", "_top");
    }
  // -->

</script>

<!-- Standard Content -->

<%@ include file="header.jsp" %>

<!-- Body -->

<body background="images/LoginBackgroundTile.gif">

<center>

<!-- Login -->

<form method="POST" action='<%= response.encodeURL("j_security_check") %>'
 name="loginForm">
  <table border="0" cellspacing="5" background="images/LoginBackgroundTile.gif">

    <tr>
    <!-- banner -->
     <td height="183">
        <div align="center"><img src="images/Login.jpg" alt="Tomcat Web Server Administration Tool" width="490" height="228"></div>
      </td>
    </tr>

    <!-- username password prompts fields layout -->
    <tr>
    <td background="images/LoginBackgroundTile.gif">
     <table width="100%" border="0" cellspacing="2" cellpadding="5">
     <tr>
      <th align="right">
        <font color="#FFFFFF"><label for="username"><bean:message key="prompt.username"/></label></font>
      </th>
      <td align="left">
        <input type="text" name="j_username" size="16" id="username"/>
      </td>
    </tr>
    <p>
    <tr>
      <th align="right">
        <font color="#FFFFFF"><label for="password"><bean:message key="prompt.password"/></label></font>
      </th>
      <td align="left">
        <input type="password" name="j_password" size="16" id="password"/>
      </td>
    </tr>

    <tr>
      <td width="50%" valign="top"> <div align="right"></div> </td>
      <td width="55%" valign="top">&nbsp;</td>
     </tr>

    <!-- login reset buttons layout -->
    <tr>
       <td width="50%" valign="top">
            <div align="right">
               <input type="submit" value='<bean:message key="button.login"/>'>&nbsp;&nbsp;
            </div>
       </td>
       <td width="55%" valign="top">
          &nbsp;&nbsp;<input type="reset" value='<bean:message key="button.reset"/>'>
       </td>
     </tr>
  </table>
  <p> &nbsp;
  </td>
  </tr>
 </table>
</form>

<script language="JavaScript" type="text/javascript">
  <!--
    document.forms["loginForm"].elements["j_username"].focus()
  // -->
</script>

</body>

<!-- Standard Footer -->

<%@ include file="footer.jsp" %>

</html:html>
