<%@ page import="ch.avocado.share.common.Encoder" %>
<%@ page import="ch.avocado.share.model.data.Group" %>
<%@ page import="ch.avocado.share.servlet.resources.base.DetailViewConfig" %>
<%@ page import="ch.avocado.share.servlet.resources.base.HtmlRenderer" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    DetailViewConfig viewConfig = (DetailViewConfig) request.getAttribute(HtmlRenderer.ATTRIBUTE_DETAIL_VIEW_CONFIG);
    Group group = viewConfig.getObject(Group.class);
    String name = Encoder.forHtml(group.getName());
    String description = Encoder.forHtml(group.getDescription());
%>
<div class="list-group">
    <div class="list-group-item list-group-header">
        <h2><%=name %></h2>
        <a class="btn btn-secondary" href="?action=edit&id=<%=Encoder.forUrlAttribute(group.getId())%>">Edit</a>
    </div>
    <div class="list-group-item">
        <%=description %>
    </div>