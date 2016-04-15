<%@ page import="ch.avocado.share.model.data.User" %>
<%@ page import="ch.avocado.share.model.data.Group" %>
<%@ page import="ch.avocado.share.common.Encoder" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div class="list-group-item list-group-header">
    <h3>Mitglieder</h3>
</div>
<div class="list-group-item">
    <div class="list-group-item-heading">
        <h4>Benutzer</h4>
    </div>
</div>
<% for (User member : members.getUsers()) {
    String memberEditUrl = "?action=" + action_edit + "&id=" + Encoder.forUrlAttribute(members.getTarget().getId()) + "&" + owner_parameter + "=" + Encoder.forUrlAttribute(member.getId());
    String memberName = Encoder.forHtml(member.getFullName());
%>
<div class="list-group-item">
    <button class="btn btn-link pull-xs-right" onclick="window.location.href='<%=memberEditUrl%>'; return false; ">
        Rechte bearbeiten
    </button>
    <span>
        <%=memberName %>
    </span>
</div>
<% } %>

<div class="list-group-item">
    <div class="list-group-item-heading">
        <h4>Gruppen</h4>
    </div>
</div>
<% for (Group member : members.getGroups()) {
    String memberEditUrl = "?action=edit_member&id=" + Encoder.forUrlAttribute(members.getTarget().getId()) + "&groupId=" + Encoder.forUrlAttribute(member.getId());
    String memberDetailUrl = "?id=" + Encoder.forUrlAttribute(members.getTarget().getId());
    String memberName = Encoder.forHtml(member.getReadableName());
%>
<a href="<%=memberDetailUrl %>" class="list-group-item">
    <%-- hackyish way to allow nested links --%>
    <button class="btn btn-link pull-xs-right" onclick="window.location.href='<%=memberEditUrl%>'; return false; ">
        Rechte bearbeiten
    </button>
    <span>
    <%=memberName %>
    </span>
</a>
<% } %>


