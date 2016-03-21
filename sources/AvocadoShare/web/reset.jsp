<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="ch.avocado.share.common.Encoder" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF8" %>
<jsp:useBean id="forgottenPassword" class="ch.avocado.share.controller.ForgottenPasswordBean"/>
<jsp:setProperty name="forgottenPassword" property="email"/>
<jsp:setProperty name="forgottenPassword" property="code"/>
<jsp:setProperty name="forgottenPassword" property="password"/>
<jsp:setProperty name="forgottenPassword" property="passwordConfirmation"/>

<% // TODO: include header and footer %>
<html>
<head>
    <title>Title</title>
</head>
<body>
<% if(request.getMethod() == "POST") {
    if(forgottenPassword.resetPassword()) { %>
        <div class="alert alert-success">
            Password erfolgreich zurückgesetzt.
        </div>
<%  } else {  %>
        <div class="alert alert-danger">
            <strong>Passwort nicht zurückgesetzt.</strong> <%=forgottenPassword.getErrorMessage() %>
        </div>
<%  }
} %>

<% if(forgottenPassword.getCode() != null && forgottenPassword.getEmail() != null) { %>
    <form method="POST">
        <input name="password" type="password" />
        <input name="passwordConfirmation" type="password" />
        <input type="hidden" name="code" value="<%=Encoder.encodeForAttribute(forgottenPassword.getCode()) %>">
        <input type="hidden" name="email" value="<%=Encoder.encodeForAttribute(forgottenPassword.getEmail())%>">
        <input type="submit" value="Zurücksetzten" />
    </form>
<% } else { %>
    <div class="alert alert-danger">
        Ungültige Anfrage. Bitte stellen Sie sicher, dass sie den kompletten Link kopiert haben oder
        versuchen Sie es erneut.
    </div>
<% }%>
</body>
</html>
