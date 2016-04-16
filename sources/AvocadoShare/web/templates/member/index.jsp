<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%
    final String owner_parameter = "o";
    final String action_edit = "edit_members";
    final String action_create = "create_member";
    DetailViewConfig viewConfigInMembers = (DetailViewConfig) request.getAttribute(HtmlRenderer.ATTRIBUTE_DETAIL_VIEW_CONFIG);
    Members members = viewConfigInMembers.getMembers();
%>
<div class="col-xl-4">
    <div class="list-group">
    <%
        String edit = request.getParameter("action");
        if (edit != null) edit = edit.toLowerCase();
        if (action_edit.equals(edit)) {
    %>
    <%@include file="edit.jsp"%>
    <%
        } else if (action_create.equals(edit)) {
    %>
    <%@include file="create.jsp"%>
    <%
        } else {
    %>
    <%@include file="list.jsp"%>
    <%
        }
    %>
    </div>
</div>