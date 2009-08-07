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

<!-- Standard Content -->

<%@ include file="header.jsp" %>

<!-- Body -->
<frameset rows="117,685*" cols="*" frameborder="NO" border="3" framespacing="3">
  <frame name="banner" src='<%= response.encodeURL("banner.jsp") %>' scrolling="no" title="commit and logout banner">
  <frameset cols="300,*" frameborder="YES" border="2">
    <frame name="tree" src='<%= response.encodeURL("setUpTree.do") %>' scrolling="auto" title="application navigation tree">
    <frame name="content" src='<%= response.encodeURL("blank.jsp") %>' scrolling="auto" title="content editing">
  </frameset>
</frameset>

<!-- Standard Footer -->

<%@ include file="footer.jsp" %>

</html:html>
