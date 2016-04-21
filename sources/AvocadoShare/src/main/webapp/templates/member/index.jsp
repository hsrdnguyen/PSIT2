<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%
    final String owner_parameter = "o";
    final String action_edit = "edit_members";
    final String action_create = "create_member";
    DetailViewConfig viewConfigInMembers = (DetailViewConfig) request.getAttribute(HtmlRenderer.ATTRIBUTE_DETAIL_VIEW_CONFIG);
    Members members = viewConfigInMembers.getMembers();
    boolean userCanEdit = viewConfigInMembers.getAccess().containsLevel(AccessLevelEnum.MANAGE);
%>
<div class="col-xl-4">
    <div class="list-group">
    <%
        String action = request.getParameter("action");
        if (userCanEdit && action_edit.equalsIgnoreCase(action)) {
    %>
    <%@include file="edit.jsp"%>
    <%
        } else if (userCanEdit && action_create.equalsIgnoreCase(action)) {
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