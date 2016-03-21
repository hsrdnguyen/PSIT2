<%@ page import="ch.avocado.share.common.Encoder" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF8" %>
<jsp:useBean id="forgottenPassword" class="ch.avocado.share.controller.ForgottenPasswordBean"/>
<jsp:setProperty name="forgottenPassword" property="email"/>
<html>
<head>
    <title></title>
</head>
<body>
<% // TODO: include header %>
<%
    if (request.getMethod() == "POST") {
        if (forgottenPassword.sendEmail()) {%>
<div class="alert alert-success">
    Falls ihre E-Mail-Adresse stimmt, erhalten sie in Kürze ein Link, um ihr Password zurückzusetzten.
</div>
<%
        } else {
%>
<div class="alert alert-danger">
    <strong>Fehlgeschlagen:</strong> <%=forgottenPassword.getErrorMessage() %>
</div>
<%
        }
    } else {
%>
    <form method="POST">
        <div class="input-group">
            <input class="form-control" name="email" value="<%=Encoder.encodeForAttribute(forgottenPassword.getEmail()) %>"/>
        </div>
        <div class="input-group">
            <input class="btn btn-primary" type="submit" value="Zurücksetzten"/>
        </div>

    </form>
<% } %>
<% // TODO: include footer %>
</body>
</html>
