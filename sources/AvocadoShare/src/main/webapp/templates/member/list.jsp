<%@ page import="ch.avocado.share.model.data.User" %>
<%@ page import="ch.avocado.share.model.data.Group" %>
<%@ page import="ch.avocado.share.common.Encoder" %>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<div class="list-group-item list-group-header">
    <a class="btn btn-secondary pull-xs-right" href="?action=<%=action_create %>&id=<%=Encoder.forHtmlAttribute(members.getTarget().getId())%>">
        <span class="octicon octicon-plus"></span>
        <span class="hidden-xs-up">Hinzufügen</span>
    </a>
    <h3>Mitglieder</h3>
</div>
<div class="list-group-item">
    <div class="list-group-item-heading">
        <h4>Benutzer</h4>
    </div>
</div>
<% for (Map.Entry<User, AccessLevelEnum> member : members.getUsersWithAccess()) {
    String memberEditUrl = "?action=" + action_edit + "&id=" + Encoder.forUrlAttribute(members.getTarget().getId()) + "&" + owner_parameter + "=" + Encoder.forUrlAttribute(member.getKey().getId());
    String memberName = Encoder.forHtml(member.getKey().getFullName());
    AccessLevelEnum currentLevel = member.getValue();
    boolean isOwner = currentLevel.containsLevel(AccessLevelEnum.OWNER);
%>
<div class="list-group-item">
    <% if(!isOwner && userCanEdit) { %>
    <button class="btn btn-sm pull-xs-right" onclick="window.location.href='<%=memberEditUrl%>'; return false; ">
        <span class="octicon octicon-pencil"></span>
    </button>
    <% } else if(isOwner) { %>
        <span class="text-muted pull-xs-right">
            Besitzer
        </span>
    <% } %>
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


