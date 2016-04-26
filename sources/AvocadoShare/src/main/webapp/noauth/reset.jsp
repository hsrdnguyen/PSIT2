<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<jsp:useBean id="forgottenPassword" class="ch.avocado.share.controller.ForgottenPasswordBean"/>
<jsp:setProperty name="forgottenPassword" property="email"/>
<jsp:setProperty name="forgottenPassword" property="code"/>
<jsp:setProperty name="forgottenPassword" property="password"/>
<jsp:setProperty name="forgottenPassword" property="passwordConfirmation"/>
<%@include file="../includes/header.jsp" %>
<h2>Passwort zurücksetzen</h2>
<%
    boolean showForm = true;
    if (request.getMethod().equals("POST")) {
        if (forgottenPassword.resetPassword()) {
            showForm = false;

%>
<div class="alert alert-success">
    Password erfolgreich zurückgesetzt.
</div>
<% } else { %>
<div class="alert alert-danger">
    <strong>Passwort nicht zurückgesetzt.</strong> <%=forgottenPassword.getErrorMessage() %>
</div>
<% }
} %>

<% if (showForm) {
    if (forgottenPassword.getCode() != null && forgottenPassword.getEmail() != null) { %>
<form method="POST">
    <div class="form-group">
        <label for="reset-password" class="form-control-label">Neues Passwort</label>
        <input id="reset-password" class="form-control" name="password" type="password" placeholder="Passwort"/>
    </div>
    <div class="form-group">
        <label for="reset-password-confirmation" class="form-control-label">Neues Passwort wiederholen</label>
        <input id="reset-password-confirmation" class="form-control" name="passwordConfirmation" type="password" placeholder="Passwort wiederholen"/>
    </div>
    <input type="hidden" name="code" value="<%=Encoder.forHtmlAttribute(forgottenPassword.getCode()) %>">
    <input type="hidden" name="email" value="<%=Encoder.forHtmlAttribute(forgottenPassword.getEmail())%>">
    <input class="btn btn-primary" type="submit" value="Zurücksetzten"/>
</form>
<% } else { %>
<div class="alert alert-danger">
    <strong>Defekter Link!</strong> Bitte stellen Sie sicher, dass sie den kompletten Link kopiert haben oder
    versuchen Sie es erneut.
</div>
<% }
}
%>
<%@include file="../includes/footer.jsp" %>