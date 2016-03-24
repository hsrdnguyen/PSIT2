<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF8" %>
<jsp:useBean id="verification" class="ch.avocado.share.controller.VerificationBean" />
<jsp:setProperty name="verification" property="code" />
<jsp:setProperty name="verification" property="email" />
<html>
<head>
    <title></title>
</head>
<body>
<% // TODO: include header %>
<% if(verification.verifyEmailCode()) { %>
    <div class="alert alert-success">
        Benutzer erfolgreich verifiziert. Sie können sich jetzt anmelden.
    </div>
<% } else { %>
    <div class="alert alert-danger">
        Der Verifizierungslink ist leider ungültig.
    </div>
<% } %>
<% // TODO: include footer %>
</body>
</html>
