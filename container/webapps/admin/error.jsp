<!-- Standard Struts Entries -->

<%@ page language="java" contentType="text/html;charset=utf-8" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>

<html:html locale="true">

<!-- Standard Content -->

<%@ include file="header.jsp" %>

<!-- Body -->

<body bgcolor="white" background="images/PaperTexture.gif">

<center>

<h2>
  <bean:message key="error.login"/>
  <br>
  <bean:message key="error.tryagain"/>
  <html:link page="/">
    <bean:message key="error.here"/>
  </html:link>
</h2>

</center>

</body>

<!-- Standard Footer -->

<%@ include file="footer.jsp" %>

</html:html>
