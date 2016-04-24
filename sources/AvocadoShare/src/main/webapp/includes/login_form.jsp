<%@include file="header.jsp" %>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%
    String loginError = (String) request.getAttribute(LoginServlet.LOGIN_ERROR);
    String email = (String) request.getAttribute(LoginServlet.ATTRIBUTE_EMAIL);
    if(email == null) {
        email = "";
    } else {
        email = Encoder.forHtmlAttribute(email);
    }
    if (loginError != null) {
%>
        <div class="alert alert-danger">
            <%=loginError %>
        </div>
<% } %>
<script type="application/javascript">
    function resetPassword(link) {
        var emailInput = document.getElementById("login-form-email");
        if(emailInput.value.length == 0) {
            return true;
        } else {
            window.location.href = link.href + "?email=" + encodeURIComponent(emailInput.value);
            return false;
        }
    }
</script>
<div class="row">
    <div class="col-xl-offset-3 col-xl-6 col-lg-offset-2 col-lg-8 col-md-offset-1 col-md-10">
        <h2>Anmelden</h2>
        <form name="login_form" action="login" method="POST">
            <div class="form-group row">
                <label for="login-form-email" class="col-sm-2 form-control-label">E-Mail</label>
                <div class="col-sm-10">
                    <input required name="<%=LoginServlet.FIELD_EMAIL %>" type="email" class="form-control"
                           id="login-form-email" placeholder="E-Mail-Adresse" value="<%=email %>">
                </div>
            </div>
            <div class="form-group row">
                <label for="login-form-password" class="col-sm-2 form-control-label">Passwort</label>
                <div class="col-sm-10">
                    <input required name="<%=LoginServlet.FIELD_PASSWORD %>" type="password" class="form-control"
                           id="login-form-password" placeholder="Passwort" pattern=".{9,}"
                           title="Das Passwort muss mindestens 9 Zeichen lang sein.">
                    <a class="small" id="password-reset-link" onclick="return resetPassword(this);" href="<%=baseUrl%>/forgot.jsp"
                       title="Hier können Sie ihr Passwort zurücksetzen">Passwort vergessen?</a>
                </div>
            </div>
            <%--
            <div class="form-group row">
                <div class="col-sm-push-2 col-sm-10">
                    <div class="checkbox">
                        <label>
                            <input name="remember" type="checkbox" checked="checked"> angemeldet bleiben
                        </label>
                    </div>
                </div>
            </div>
            --%>
            <div class="form-group row">
                <div class="col-sm-offset-2 col-sm-10">
                    <button type="submit" class="btn btn-secondary">Anmelden</button>
                </div>
            </div>
        </form>
    </div>
</div>
<%@include file="footer.jsp" %>