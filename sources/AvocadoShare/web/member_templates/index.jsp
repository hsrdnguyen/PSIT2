<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<% switch (request.getParameter("edit")) {%>
<%
    case "edit_members":
%>
<jsp:include page="edit.jsp" />
<%
    break;
    case "create_member":
%>
<jsp:include page="create.jsp" />
<%
    break;
    default:
%>
<jsp:include page="list.jsp" />
<% } %>
