<%@ page import="ch.avocado.share.model.data.Group" %>
<%@ page import="ch.avocado.share.common.Encoder" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<% Group[] groups = (Group[]) request.getAttribute("Groups"); %>
<div class="list-group">
    <div class="list-group-item list-group-header">
        <h2>Meine Gruppen</h2>
    </div>
<%
    if (groups.length == 0) {
%>
    <div class="list-group-item list-group-item-info">
        Wir können leider keine Gruppen für Sie finden.
    </div>
<%
} else {
%>
        <%
            for (Group group : groups) {
                String name = Encoder.forHtml(group.getName());
                String detailLink = "?id=" + Encoder.forUrlAttribute(group.getId());
                String description = Encoder.forHtml(group.getDescription());
        %>
    <a href="<%=detailLink %>" class="list-group-item">
        <h3 class="list-group-item-heading">
            <%=name %>
        </h3>
        <p>
            <%=description %>
        </p>
    </a>
<%
    } // for groups
%>
<%
    } // group.length != 0
%>
</div>
