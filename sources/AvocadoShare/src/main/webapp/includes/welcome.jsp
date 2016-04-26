<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@include file="header_no_container.jsp" %>
<div class="jumbotron bg-inverse">
    <div class="container">
        <div class="row">
            <div class="col-sm-6">
                <h1 class="display-3">Avocado Share</h1>
                <p class="lead">
                    Eine Platform für dein Studium!
                </p>
                <hr>
                <button class="btn btn-primary">Registrieren</button>
            </div>
            <div class="col-sm-push-2 col-sm-4">
                <div class="list-group" style="color:inherit">
                    <div class="list-group-item list-group-header">
                        <h3>Anmelden</h3>
                    </div>
                    <div class="list-group-item">
                        <form action="<%=baseUrl%>/login" method="POST">
                            <div class="form-group">
                                <div class="input-group">
                                    <label for="welcome-login-username" class="input-group-addon">
                                        <span class="octicon octicon-mail"></span>
                                        <span class="sr-only">E-Mail-Adresse</span>
                                    </label>
                                    <input id="welcome-login-username" type="text" class="form-control"
                                    <%-- type="email" --%>
                                           name="<%=LoginServlet.FIELD_EMAIL %>" placeholder="E-Mail"/>
                                </div>
                            </div>
                            <div class="form-group">
                                <div class="input-group">
                                    <label for="welcome-login-password" class="input-group-addon">
                                        <span class="octicon octicon-key"></span>
                                        <span class="sr-only">Passwort</span>
                                    </label>
                                    <input id="welcome-login-password" type="password" class="form-control"
                                    <%-- required pattern=".{9,}" --%>
                                           title="Ihr Passwort ist mindestens 9 Zeichen lang."
                                           name="<%=LoginServlet.FIELD_PASSWORD%>" placeholder="Passwort"/>
                                </div>
                            </div>
                            <input type="hidden" value="<%=Encoder.forHtmlAttribute(currentUrl) %>"
                                   name="<%=LoginServlet.FIELD_REDIRECT_TO%>"/>
                            <div class="form-group">
                                <input type="submit" class="btn btn-secondary" value="Anmelden"/>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<div class="container">
    <h2 style="color: #ffffff;">Features</h2>
    <div class="row">
        <div class="col-sm-4">
            <div class="card card-block">
                <h4 class="card-title">Kollaborativ</h4>
                <p class="card-text">dsadsa</p>
            </div>
        </div>
        <div class="col-sm-4">
            <div class="card card-block">
                <h4 class="card-title">Kollaborativ</h4>
                <p class="card-text">dsadsa</p>
            </div>
        </div>
        <div class="col-sm-4">
            <div class="card card-block">
                <h4 class="card-title">Kollaborativ</h4>
                <p class="card-text">dsadsa</p>
            </div>
        </div>
    </div>
</div>
<%@include file="footer_no_container.jsp" %>