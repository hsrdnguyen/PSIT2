<%@ page import="ch.avocado.share.model.data.User" %>
<%@ page import="ch.avocado.share.model.data.Group" %>
<%@ page import="ch.avocado.share.common.Encoder" %>
<%@ page import="ch.avocado.share.model.data.AccessControlObjectBase" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    User[] userMembers = (User[]) request.getAttribute("MemberUsers");
    Group[] groupMembers = (Group[]) request.getAttribute("MemberGroups");
    AccessControlObjectBase target = (AccessControlObjectBase) request.getAttribute("Target");
%>
<h2>Mitglieder</h2>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3>Benutzer</h3>
    </div>
    <table class="table">
        <thead>
            <tr>
                <th>Name</th>
                <th width="20%">Aktion</th>
            </tr>
        </thead>
        <tbody>
        <% for (User member : userMembers) {
            String editUrl = "?action=edit_member&id=" + Encoder.forUrlAttribute(target.getId()) + "&userId=" + Encoder.forUrlAttribute(member.getId());
        %>
            <tr>
                <td><%=Encoder.forHtml(member.getFullName()) %></td>
                <td>
                    <a href="<%=editUrl %>">Bearbeiten</a>
                </td>
            </tr>
        <% } %>

        </tbody>
    </table>
</div>
<div class="panel panel-default">
    <div class="panel-heading">
        <h3>Gruppen</h3>
    </div>
    <table class="table">
        <thead>
        <tr>
            <th>Name</th>
            <th width="20%">Aktion</th>
        </tr>
        </thead>
        <tbody>
    <% for (Group member : groupMembers) {
        String editUrl = "?action=edit_member&id=" + Encoder.forUrlAttribute(target.getId()) + "&groupId=" + Encoder.forUrlAttribute(member.getId());
    %>
    <tr>
        <td><%=Encoder.forHtml(member.getName()) %></td>
        <td>
            <a href="<%=editUrl %>">Bearbeiten</a>
        </td>
    </tr>
    <% } %>
        </tbody>
    </table>
</div>


