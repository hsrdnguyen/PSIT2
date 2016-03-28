<%@ page import="ch.avocado.share.model.data.Group" %>
<%@ page import="ch.avocado.share.common.Encoder" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<% Group group = (Group) request.getAttribute("Group"); %>
<form method="post" action="?id=<%=Encoder.forHtmlAttribute(group.getId())%>">
    <input type="text" name="name" value="<%=Encoder.forHtmlAttribute(group.getName())%>"/>
    <input type="text" name="description" value="<%=Encoder.forHtmlAttribute(group.getDescription())%>"/>
    <input type="hidden" name="method" value="patch" />
    <input type="submit" value="Speichern" />
</form>