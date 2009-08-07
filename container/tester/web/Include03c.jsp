<%@ page contentType="text/plain" %><%
  // Duplicate logic from "Include03.java"
  StringBuffer sb = new StringBuffer();
  String path = request.getParameter("path");
  if (path == null)
    path = "/Include03a";
  RequestDispatcher rd =
    getServletContext().getRequestDispatcher(path);
  if (rd == null) {
    sb.append(" No RequestDispatcher returned/");
  } else {
    rd.include(request, response);
  }
  response.resetBuffer();
  String value = null;
  try {
    value = (String) request.getAttribute(path.substring(1));
  } catch (ClassCastException e) {
      sb.append(" Returned attribute not of type String/");
  }
  if ((sb.length() < 1) && (value == null)) {
      sb.append(" No includee-created attribute was returned/");
  }
  if (sb.length() < 1)
    out.println("Include03c.jsp PASSED");
  else {
    out.print("Include03c.jsp FAILED -");
    out.println(sb.toString());
  }
%>
