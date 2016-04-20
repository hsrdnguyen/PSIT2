<%@ page import="ch.avocado.share.common.Encoder" %>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<jsp:useBean id="forgottenPassword" class="ch.avocado.share.controller.ForgottenPasswordBean"/>
<jsp:setProperty name="forgottenPassword" property="email"/>
<%
    request.setAttribute("ch.avocado.share.includes.header.scripts", new String[]{"https://www.google.com/recaptcha/api.js"});
    request.setAttribute("ch.avocado.share.includes.header.title", "Passwort vergessen");
%>
<jsp:include page="includes/header.jsp" />
<h2>Passwort vergessen</h2>
<%
    boolean showFormular = true;
    if (request.getMethod().equals("POST")) {
        if (forgottenPassword.requestNewPassword(request)) {
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
    <p>
        Sie haben Ihr Passwort vergessen? Kein Problem. <br/>
        Füllen Sie Ihre E-Mail-Adresse in das Formular-Feld und bestätigen
        Sie mittels der Schaltfläche &quot;Zurücksetzten&quot;.
        In Kürze erhalten Sie dann eine Nachricht mit dem weiteren Vorgehen.
    </p>
    <form method="POST">
        <div class="form-group row">
            <div class="col-md-3">
                <label for="forgot-email">E-Mail-Adresse</label>
            </div>
            <div class="col-md-9 col-lg-6">
                <input class="form-control" id="forgot-email" placeholder="E-Mail-Adresse" name="email" value="<%=Encoder.forHtmlAttribute(forgottenPassword.getEmail())%>"/>
            </div>
        </div>
        <div class="form-group row">
            <div class="col-md-3">
                Captcha
            </div>
            <div class="col-md-9 col-lg-6">
                <noscript>
                    <div class="alert alert-danger">
                        <strong>Javascript deaktiviert.</strong> Für diesen Schritt wird Javascript benötigt.
                    </div>
                </noscript>
                <div class="g-recaptcha" data-sitekey="6LeS4x0TAAAAANcSYZwdoyTFY-NjvbFkX3zU8CiU"></div>
            </div>
        </div>
        <div class="form-group row">
            <div class="col-md-push-3 col-md-9 col-lg-6">
            <input class="btn btn-primary" type="submit" value="Zurücksetzten" />
            </div>
        </div>
    </form>
<% } %>
<jsp:include page="includes/footer.jsp" />
