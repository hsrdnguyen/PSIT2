<%@ page import="ch.avocado.share.common.Encoder" %>
<%@ page import="ch.avocado.share.controller.UserSession" %>
<%@ page import="ch.avocado.share.servlet.LoginServlet" %>
<%@ page import="ch.avocado.share.servlet.resources.base.ResourceServlet" %>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%
    String[] scripts = (String[]) request.getAttribute("ch.avocado.share.includes.header.scripts");
    String title = (String) request.getAttribute("ch.avocado.share.includes.header.title");
    if(title == null) {
        title = "Avocado Share";
    } else {
        title = "Avocado Share - " + Encoder.forHtml(title);
    }
    if(scripts == null) {
        scripts = new String[0];
    }
    String baseUrl = request.getServletContext().getContextPath();
    String currentUrl = (String) request.getAttribute("javax.servlet.error.request_uri");
    if(currentUrl == null) {
        currentUrl = (String) request.getAttribute("javax.servlet.forward.request_uri");
    }
    if(currentUrl == null) {
        currentUrl = request.getRequestURI();
    }
    UserSession userSession = new UserSession(request);
    String name;
    if (userSession.isAuthenticated()) {
        name = Encoder.forHtml(userSession.getUser().getFullName());
    }
%>
<!DOCTYPE html>
<html lang="de">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><%=title %></title>
    <link rel="stylesheet" href="<%=baseUrl%>/static/components/bootstrap/dist/css/bootstrap.css">
    <link rel="stylesheet" href="<%=baseUrl%>/static/css/octicons.css">
    <link rel="stylesheet" href="<%=baseUrl%>/static/css/app.css">
    <script type="application/javascript" src="<%=baseUrl%>/static/js/app.js"></script>
    <% for(String script: scripts) { %>
    <script type="application/javascript" src="<%=Encoder.forHtmlAttribute(script)%>"></script>
    <% } %>
</head>
<body>
<div id="footerpusher">
    <header>
        <nav class="navbar navbar-dark bg-inverse">
            <div class="container">
                <button class="navbar-toggler hidden-md-up" type="button" data-toggle="collapse"
                        data-target="#exCollapsingNavbar2">
                    &#9776;
                </button>
                <div class="collapse navbar-toggleable-sm" id="exCollapsingNavbar2">
                    <a class="navbar-brand" href="<%=baseUrl%>/index.jsp">
                        Avocado Share
                    </a>
                    <ul class="nav navbar-nav">
                        <% if (userSession.isAuthenticated()) { %>
                        <li class="nav-item">
                            <a class="nav-link" href="<%=baseUrl%>/group">Gruppen</a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" href="<%=baseUrl%>/module">Module</a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" href="<%=baseUrl%>/file">Dokumente</a>
                        </li>
                        <% } else { %>
                        <%-- Links for mobile users --%>
                        <li class="nav-item hidden-md-up">
                            <a class="nav-link" href="<%=baseUrl%>/login">Login</a>
                        </li>
                        <li class="nav-item hidden-md-up">
                            <a class="nav-link" href="<%=baseUrl%>/user?action=create">Register</a>
                        </li>
                        <% } %>
                        <!-- Navbar Search -->
                        <li class="nav-item pull-md-right">
                            <form method="get" action="document_view.jsp" class="form-inline">
                                <div class="input-group">
                                    <input type="text" class="form-control" id="searchString" name="searchString" placeholder="Suchen nach..."/>
                                    <span class="input-group-btn">
                                        <button class="btn btn-primary" type="submit">Los!</button>
                                    </span>
                                </div>
                            </form>
                        </li>

                        <% if (userSession.isAuthenticated()) { %>
                        <li class="nav-item pull-md-right">
                            <div class="dropdown">
                                <button class="btn btn-primary-outline dropdown-toggle"
                                        type="button" id="create-new-toggle-button"
                                        data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                                    <span class="octicon octicon-plus"></span>
                                </button>
                                <div class="dropdown-menu" id="create-new-dropdown" aria-labelledby="create-new-toggle-button">
                                    <a class="dropdown-item" href="<%=baseUrl%>/file?<%=ResourceServlet.PARAMETER_ACTION%>=<%=ResourceServlet.ACTION_CREATE%>">
                                        Neue Datei erstellen
                                    </a>
                                    <a class="dropdown-item" href="<%=baseUrl%>/module?<%=ResourceServlet.PARAMETER_ACTION%>=<%=ResourceServlet.ACTION_CREATE%>">
                                        Neues Modul erstellen
                                    </a>
                                    <a class="dropdown-item" href="<%=baseUrl%>/group?<%=ResourceServlet.PARAMETER_ACTION%>=<%=ResourceServlet.ACTION_CREATE%>">
                                        Neue Gruppe erstellen
                                    </a>
                                </div>
                            </div>
                        </li>
                        <% } %>
                        <!-- Navbar Login formular -->
                        <li class="nav-item hidden-sm-down pull-md-right">
                            <div class="dropdown">
                                <!-- User button -->
                                <button class="btn btn-primary-outline dropdown-toggle"
                                        type="button" id="navbar-login-toggle-button"
                                        data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                                    <span class="octicon octicon-person"></span>
                                </button>
                                <div id="navbar-login" class="dropdown-menu"
                                     aria-labelledby="navbar-login-toggle-button">
                                    <% if (userSession.isAuthenticated()) { %>
                                    <a class="dropdown-item"
                                       href="<%=baseUrl%>/user?id=<%=Encoder.forUrlAttribute(userSession.getUserId())%>">Profil</a>
                                    <div class="dropdown-divider"></div>
                                    <a class="dropdown-item" href="<%=baseUrl%>/logout">Abmelden</a>
                                    <% } else { %>
                                    <!-- login formular -->
                                    <form action="<%=baseUrl%>/login" method="POST">
                                        <div class="input-group dropdown-item" style="margin-bottom: 0">
                                            <label for="navbar-login-username" class="input-group-addon">
                                                <span class="octicon octicon-mail"></span>
                                                <span class="sr-only">E-Mail-Adresse</span>
                                            </label>
                                            <input id="navbar-login-username" type="text" class="form-control"
                                            <%-- type="email" --%>
                                                   name="<%=LoginServlet.FIELD_EMAIL %>" placeholder="E-Mail"/>
                                        </div>
                                        <div class="input-group dropdown-item" style="margin-bottom: 0">
                                            <label for="navbar-login-password" class="input-group-addon">
                                                <span class="octicon octicon-key"></span>
                                                <span class="sr-only">Passwort</span>
                                            </label>
                                            <input id="navbar-login-password" type="password" class="form-control"
                                            <%-- required pattern=".{9,}" --%>
                                                   title="Ihr Passwort ist mindestens 9 Zeichen lang."
                                                   name="<%=LoginServlet.FIELD_PASSWORD%>" placeholder="Passwort"/>
                                        </div>
                                        <input type="hidden" value="<%=Encoder.forHtmlAttribute(currentUrl) %>"
                                               name="<%=LoginServlet.FIELD_REDIRECT_TO%>"/>
                                        <div class="dropdown-item">
                                            <input type="submit" class="btn btn-secondary" value="Anmelden"/>
                                        </div>
                                    </form>
                                    <div class="dropdown-divider"></div>
                                    <!--
                                        <a href="<%=baseUrl%>/indexlog.jsp">
                                            <span class="dropdown-item">Anmelden</span>
                                        </a>
                                        -->
                                    <a href="<%=baseUrl%>/user?action=create">
                                        <span class="dropdown-item">Registrieren</span>
                                    </a>
                                    <% } %>
                                </div>
                            </div>
                        </li>
                    </ul>
                </div>
            </div>
        </nav>
    </header>