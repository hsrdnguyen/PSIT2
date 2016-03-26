<%@ page import="ch.avocado.share.common.Encoder" %>
<%@ page import="ch.avocado.share.model.data.Group" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<% Group group = (Group) request.getAttribute("Group"); %>
<h1>Gruppe <%=Encoder.forHtml(group.getName()) %></h1>
<a href="?action=edit&id=<%=Encoder.forUrlAttribute(group.getId())%>">Edit</a>