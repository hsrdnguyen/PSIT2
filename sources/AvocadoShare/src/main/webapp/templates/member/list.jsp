<%@ page import="ch.avocado.share.common.Encoder" %>
<%@ page import="ch.avocado.share.model.data.Group" %>
<%@ page import="ch.avocado.share.model.data.User" %>
<%@ page import="ch.avocado.share.controller.EditMemberBean" %>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<div class="list-group-item list-group-header">
    <% if (userCanEdit) { %>
    <a class="btn btn-secondary-outline pull-xs-right"
       href="?action=<%=action_create %>&id=<%=Encoder.forHtmlAttribute(members.getTarget().getId())%>">
        <span class="octicon octicon-plus"></span>
        <span class="hidden-xs-up">Hinzufügen</span>
    </a>
    <% } %>
    <h3>Mitglieder</h3>
</div>
<div class="list-group-item">
    <div class="list-group-item-heading">
        <h4><%--<span class="octicon octicon-person"></span>--%> Benutzer</h4>
    </div>
</div>
<% for (Map.Entry<User, AccessLevelEnum> member : members.getUsersWithAccess().entrySet()) {
    String memberEditUrl = "?action=" + action_edit + "&id=" + Encoder.forUrlAttribute(members.getTarget().getId()) + "&" + owner_parameter + "=" + Encoder.forUrlAttribute(member.getKey().getId());
    String memberName = Encoder.forHtml(member.getKey().getFullName());
    AccessLevelEnum currentLevel = member.getValue();
    String readableAccess = Encoder.forHtml(EditMemberBean.getReadableLevelName(currentLevel));
    boolean memberIsOwner = currentLevel.containsLevel(AccessLevelEnum.OWNER);
%>
<div class="list-group-item members users access-<%=currentLevel.name().toLowerCase()%>">
    <div class="pull-xs-right">
        <% if (!memberIsOwner && userCanEdit) { %>
            <button title="Rechte von <%=memberName %> bearbeiten" class="btn btn-sm btn-secondary-outline"
                    onclick="window.location.href='<%=memberEditUrl%>'; return false; ">
                <span class="octicon octicon-gear"></span>
                <span class="sr-only">Rechte von <%=memberName %> bearbeiten</span>
            </button>
        <% } else if (memberIsOwner) { %>
            <span class="text-muted">
                Besitzer
            </span>
        <% } %>
    </div>
    <span data-toggle="tooltip" data-placement="top" title="<%=memberName %> hat <%=readableAccess%>">
        <%=memberName %>
    </span>
</div>
<% } %>

<div class="list-group-item">
    <div class="list-group-item-heading">
        <h4><%--<span class="octicon octicon-organization"></span>--%> Gruppen</h4>
    </div>
</div>
<% for (Group member : members.getGroups()) {
    String memberEditUrl = "?action=edit_member&id=" + Encoder.forUrlAttribute(members.getTarget().getId()) + "&groupId=" + Encoder.forUrlAttribute(member.getId());
    String memberDetailUrl = "?id=" + Encoder.forUrlAttribute(members.getTarget().getId());
    String memberName = Encoder.forHtml(member.getReadableName());
%>
<a href="<%=memberDetailUrl %>" class="list-group-item members groups">
    <%-- hackyish way to allow nested links --%>
    <button class="btn btn-link pull-xs-right" onclick="window.location.href='<%=memberEditUrl%>'; return false; ">
        Rechte bearbeiten
    </button>
    <span>
    <%=memberName %>
    </span>
</a>
<% } %>
