<%@ page contentType="text/plain" %>This is before the include
<jsp:include page="<%= request.getParameter(\"path\") %>" flush="true"/>
This is after the include

