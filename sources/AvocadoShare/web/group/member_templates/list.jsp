<%@ page import="ch.avocado.share.model.data.User" %>
<%@ page import="ch.avocado.share.model.data.Group" %>
<%@ page import="ch.avocado.share.common.Encoder" %>
<%@ page import="ch.avocado.share.model.data.AccessControlObjectBase" %><%--
  Created by IntelliJ IDEA.
  User: coffeemakr
  Date: 24.03.16
  Time: 15:44
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    User[] userMembers = (User[]) request.getAttribute("MemberUsers");
    Group[] groupMembers = (Group[]) request.getAttribute("MemberGroups");
    AccessControlObjectBase target = (AccessControlObjectBase) request.getAttribute("Target");
%>
<h1>List of Members</h1>
<h2>Users</h2>

<% for (User member : userMembers) {
    String editUrl = "?action=edit_member&id=" + Encoder.forUrlAttribute(target.getId()) + "&userId=" + Encoder.forUrlAttribute(member.getId());
%>
<%=Encoder.forHtml(member.getFullName()) %>
<a href="<%=editUrl %>">Edit</a>
<br/>
<% } %>

<h2>Groups</h2>
<% for (Group member : groupMembers) {
    String editUrl = "?action=edit_member&id=" + Encoder.forUrlAttribute(target.getId()) + "&groupId=" + Encoder.forUrlAttribute(member.getId());
%>
<%=Encoder.forHtml(member.getName()) %>
<a href="<%=editUrl %>">Edit</a>
<br/>
<% } %>


