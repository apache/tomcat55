<%@ page contentType="text/plain" %>This is before the include
<jsp:include page="<%= request.getParameter(\"path\") %>" flush="false"/>
This is after the include

