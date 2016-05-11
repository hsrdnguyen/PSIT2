<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF8" %>
<jsp:useBean id="accessBean" class="ch.avocado.share.controller.ObjectAccessBean" />
<jsp:setProperty name="accessBean" property="fileId" />
<jsp:setProperty name="accessBean" property="requesterUserId" />
<%@include file="includes/header.jsp"%>
<%
    accessBean.setObjectOwner(userSession.getUser());
    if(accessBean.grantAccess()) {
%>
<div class="alert alert-success">
    Der Benutzer wurde berechtigt.
</div>
<% } else { %>
<div class="alert alert-danger">
    Der Benutzer konnte leider nicht berechtigt werden.
</div>
<% } %>
<%@include file="includes/footer.jsp"%>