<%@ page import="ch.avocado.share.model.data.User" %>
<%@ page import="ch.avocado.share.model.data.Group" %>
<%@ page import="ch.avocado.share.common.Encoder" %>
<%@ page import="ch.avocado.share.model.data.AccessControlObjectBase" %>
<%@ page import="ch.avocado.share.model.data.Members" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    Members members = (Members) request.getAttribute("Members");
%>
<div class="list-group-item">
    <div class="list-group-item-heading">
        <h3>Mitglieder</h3>
    </div>
</div>
<div class="list-group-item">
    <div class="list-group-item-heading">
        <h4>Benutzer</h4>
    </div>
</div>
<% for (User member : members.getUsers()) {
    String editUrl = "?action=edit_member&id=" + Encoder.forUrlAttribute(members.getTarget().getId()) + "&userId=" + Encoder.forUrlAttribute(member.getId());
    String name = Encoder.forHtml(member.getFullName());
%>
<div class="list-group-item">
    <button class="btn btn-link pull-xs-right" onclick="window.location.href='<%=editUrl%>'; return false; ">
        Rechte bearbeiten
    </button>
    <span>
        <%=name %>
    </span>
</div>
<% } %>

<div class="list-group-item">
    <div class="list-group-item-heading">
        <h4>Gruppen</h4>
    </div>
</div>
<% for (Group member : members.getGroups()) {
    String editUrl = "?action=edit_member&id=" + Encoder.forUrlAttribute(members.getTarget().getId()) + "&groupId=" + Encoder.forUrlAttribute(member.getId());
    String detailUrl = "?id=" + Encoder.forUrlAttribute(members.getTarget().getId());
    String name = Encoder.forHtml(member.getReadableName());
%>
<a href="<%=detailUrl %>" class="list-group-item">
    <%-- hackyish way to allow nested links --%>
    <button class="btn btn-link pull-xs-right" onclick="window.location.href='<%=editUrl%>'; return false; ">
        Rechte bearbeiten
    </button>
    <span>
    <%=name %>
    </span>
</a>
<% } %>


