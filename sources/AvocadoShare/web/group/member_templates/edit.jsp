<%@ page import="ch.avocado.share.model.data.User" %>
<%@ page import="ch.avocado.share.model.data.Group" %>
<%@ page import="ch.avocado.share.common.Encoder" %>
<%@ page import="ch.avocado.share.model.data.AccessLevelEnum" %>
<%@ page import="ch.avocado.share.model.data.AccessControlObjectBase" %><%--
  Created by IntelliJ IDEA.
  User: coffeemakr
  Date: 24.03.16
  Time: 15:13
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    User user = (User) request.getAttribute("MemberUser");
    Group group = (Group) request.getAttribute("MemberGroup");
    AccessControlObjectBase target = (AccessControlObjectBase) request.getAttribute("Target");
    final String name, type, id, ownerFieldName;
    if (user != null) {
        name = user.getFullName();
        type = "Benutzer";
        ownerFieldName = "userId";
        id = user.getId();
    } else {
        name = group.getName();
        type = "Gruppe";
        ownerFieldName = "groupId";
        id = group.getId();
    }
%>
<h2>Rechte bearbeiten</h2>
<form method="post" action="members.jsp">
    <input type="hidden" name="<%=ownerFieldName %>" value="<%=Encoder.forHtmlAttribute(id)%>" />
    <input type="hidden" name="targetId" value="<%=target.getId()%>" />
    <select name="level">
        <option value="<%=AccessLevelEnum.NONE.toString()%>">Keine</option>
        <option selected="selected" value="<%=AccessLevelEnum.READ.toString()%>">Lesen</option>
        <option value="<%=AccessLevelEnum.WRITE.toString()%>">Lesen und Schreiben</option>
    </select>
    <input type="hidden" name="method" value="put" />
    <input type="submit" value="Speichern" />
</form>
