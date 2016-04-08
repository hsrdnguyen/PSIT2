<%@ page import="ch.avocado.share.model.data.User" %>
<%@include file="../../includes/header.jsp"%>
<%
    boolean isOwner = false;

    User user = (User) request.getAttribute("User");
    String urlId = Encoder.forUrlAttribute(user.getId());
    if(userSession.getUser() != null && userSession.getUser().getId().equals(user.getId())) {
        isOwner = true;
    }
    String userName = Encoder.forHtml(user.getFullName());
    String description = Encoder.forHtml(user.getDescription());
%>
<h2><%=userName%></h2>
<% if(isOwner) { %>
<a class="btn btn-primary" href="?action=edit&id=<%=urlId %>">Bearbeiten</a>
<% } %>
<p>
    <%=description %>
</p>

<%@include file="../../includes/footer.jsp"%>