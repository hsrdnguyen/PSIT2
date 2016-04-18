<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ page import="ch.avocado.share.common.Encoder" %>
<jsp:useBean id="forgottenPassword" class="ch.avocado.share.controller.ForgottenPasswordBean"/>
<jsp:setProperty name="forgottenPassword" property="email"/>
<jsp:setProperty name="forgottenPassword" property="code"/>
<jsp:setProperty name="forgottenPassword" property="password"/>
<jsp:setProperty name="forgottenPassword" property="passwordConfirmation"/>
<%@include file="includes/header.jsp"%>
<% if(request.getMethod().equals("POST")) {
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
        <input type="hidden" name="code" value="<%=Encoder.forHtmlAttribute(forgottenPassword.getCode()) %>">
        <input type="hidden" name="email" value="<%=Encoder.forHtmlAttribute(forgottenPassword.getEmail())%>">
        <input type="submit" value="Zurücksetzten" />
    </form>
<% } else { %>
    <div class="alert alert-danger">
        Ungültige Anfrage. Bitte stellen Sie sicher, dass sie den kompletten Link kopiert haben oder
        versuchen Sie es erneut.
    </div>
<% }%>
<%@include file="includes/footer.jsp"%>