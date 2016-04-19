<%@ page import="ch.avocado.share.common.Encoder" %>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<jsp:useBean id="forgottenPassword" class="ch.avocado.share.controller.ForgottenPasswordBean"/>
<jsp:setProperty name="forgottenPassword" property="email"/>
<jsp:include page="includes/header.jsp" />
<%
    boolean showFormular = true;
    if (request.getMethod() == "POST") {
        if (forgottenPassword.requestNewPassword()) {
            showFormular = false;
%>
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
    }
    if(showFormular) {
%>
    <form method="POST">
        <div class="input-group">
            <input class="form-control" name="email" value="<%=Encoder.forHtmlAttribute(forgottenPassword.getEmail())%>"/>
        </div>
        <div class="input-group">
            <input class="btn btn-primary" type="submit" value="Zurücksetzten"/>
        </div>

    </form>
<% } %>
<jsp:include page="includes/footer.jsp" />
