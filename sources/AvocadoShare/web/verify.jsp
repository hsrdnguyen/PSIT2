<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF8" %>
<jsp:useBean id="verification" class="ch.avocado.share.controller.VerificationBean" />
<jsp:setProperty name="verification" property="code" />
<jsp:setProperty name="verification" property="email" />
<%@include file="includes/header.jsp"%>
<% if(verification.verifyEmailCode()) { %>
    <div class="alert alert-success">
        Benutzer erfolgreich verifiziert. Sie können sich jetzt anmelden.
    </div>
<% } else { %>
    <div class="alert alert-danger">
        Der Verifizierungslink ist leider ungültig.
    </div>
<% } %>
<%@include file="includes/footer.jsp"%>
