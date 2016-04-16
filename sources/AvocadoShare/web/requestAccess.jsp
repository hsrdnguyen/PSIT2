<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<jsp:useBean id="accessBean" class="ch.avocado.share.controller.FileAccessBean" />
<jsp:setProperty name="accessBean" property="fileId" />
<%@include file="includes/header.jsp"%>
<%
    accessBean.setRequestingUserMail("sascha.berg.sb@gmail.com");
    if(accessBean.requestAccess()) {
%>
<div class="alert alert-success">
    Der Besitzer der Datei wurde benachrichtigt.
</div>
<% } else { %>
<div class="alert alert-danger">
    Das Mail konnte leider nicht versendet werden.
</div>
<% } %>
<%@include file="includes/footer.jsp"%>
