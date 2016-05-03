<%@include file="includes/header.jsp"%>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%
    String message = (String) request.getAttribute("ch.avocado.share.message.success");
%>
<div class="content-main">
    <h2>Erfolg!</h2>
    <% if(message != null) { %>
    <div class="alert alert-success">
        <strong>Die Aktion war efolgreich!</strong> Wir konnten Sie aber leider nicht zur gewünschten Seite weiterleiten.
    </div>
    <% } %>
</div>
<%@include file="includes/footer.jsp"%>