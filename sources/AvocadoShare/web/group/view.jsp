<%@ page import="ch.avocado.share.controller.UserSession" %>
<%@ page import="ch.avocado.share.common.Encoder" %>
<%@ page import="ch.avocado.share.model.data.AccessLevelEnum" %>
<%@ page import="ch.avocado.share.model.data.User" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:useBean id="group" class="ch.avocado.share.controller.GroupBean"/>
<jsp:setProperty name="group" property="name"/>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
UserSession userSession = new UserSession(request);
if(!group.load()) {
    response.sendError(response.SC_NOT_FOUND);
} else if(!userSession.hasAccess(AccessLevelEnum.READ, group.getGroup())) {
    // TODO: access request
    response.sendError(response.SC_FORBIDDEN);
} else {
%>
<h1>Gruppe <%=Encoder.encodeForHTMLBody(group.getName())%></h1>
<h2>Mitglieder</h2>
<% for (User member: group.getMembers()) { %>
    <div>
        <span class="name">
            <%=Encoder.encodeForHTMLBody(member.getPrename() + " " + member.getSurname())%>
        </span>
    </div>
<% } // members %>
<% } %>