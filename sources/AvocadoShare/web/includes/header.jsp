<%@ page import="ch.avocado.share.servlet.LoginServlet" %>
<%@ page import="ch.avocado.share.controller.UserSession" %>
<%@ page import="ch.avocado.share.common.Encoder" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF8" %>
<%
    String baseUrl = request.getServletContext().getContextPath();
    String currentUrl = request.getRequestURI();
    UserSession userSession = new UserSession(request);
%>
<!DOCTYPE html>
<html lang="de">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Avocado Share</title>
    <link rel="stylesheet" href="<%=baseUrl%>/components/bootstrap/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="<%=baseUrl%>/components/octicons/octicons/octicons.css">
    <link rel="stylesheet" href="<%=baseUrl%>/css/app.css">
</head>
<body>
<div id="footerpusher">
    <header>
        <nav class="navbar navbar-light bg-faded">
            <div class="container">
                <button class="navbar-toggler hidden-sm-up" type="button" data-toggle="collapse"
                    data-target="#exCollapsingNavbar2">
                    &#9776;
                </button>
                <div class="collapse navbar-toggleable-xs" id="exCollapsingNavbar2">
                    <a href="index.jsp" class="navbar-brand" href="#">Avocado Share</a>
                    <ul class="nav navbar-nav">
                        <!-- Links for mobile users -->
                        <li class="nav-item hidden-sm-up">
                            <a class="nav-link" href="#login">Login</a>
                        </li>
                        <li class="nav-item hidden-sm-up">
                            <a class="nav-link" href="#register">Register</a>
                        </li>
                        <!-- Navbar Search -->
                        <li class="nav-item pull-md-right">
                            <form action="document_view.jsp" class="form-inline">
                                <div class="input-group">
                                    <input type="text" class="form-control" placeholder="Search for...">
              <span class="input-group-btn">
                <button class="btn btn-secondary" type="submit">Los!</button>
              </span>
                                </div>
                            </form>
                        </li>

                        <% if(userSession.isAuthenticated()) { %>
                        <li class="nav-item pull-md-right">
                            <a href="<%=baseUrl%>/file/?action=create" class="btn btn-secondary">Upload</a>
                        </li>
                        <% } %>
                        <!-- Navbar Login formular -->
                        <li class="nav-item hidden-xs-down pull-md-right">
                            <div class="dropdown">
                                <!-- User button -->
                                <button class="btn btn-secondary dropdown-toggle"
                                        type="button" id="navbar-login-toggle-button"
                                        data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                                    <span class="octicon octicon-person"></span>
                                </button>
                                <div id="navbar-login" class="dropdown-menu"
                                     aria-labelledby="navbar-login-toggle-button">
                                    <% if(userSession.isAuthenticated()) { %>
                                    <a class="dropdown-item" href="#">Profile</a>
                                    <a class="dropdown-item" href="<%=baseUrl%>/logout">Abmelden</a>
                                    <% } else { %>
                                    <!-- login formular -->
                                    <form action="<%=baseUrl%>/login" method="POST" action="#login">
                                        <fieldset>
                                            <div class="input-group dropdown-item" style="margin-bottom: 0">
                                                <label for="navbar-login-username" class="input-group-addon">
                                                    <span class="octicon octicon-person"></span><span class="sr-only">Benutzername</span>
                                                </label>
                                                <input id="navbar-login-username" type="text" class="form-control"
                                                       name="<%=LoginServlet.FIELD_EMAIL %>" placeholder="E-Mail"/>
                                            </div>
                                            <div class="input-group dropdown-item" style="margin-bottom: 0">
                                                <label for="navbar-login-password" class="input-group-addon">
                                                    <span class="octicon octicon-lock"></span><span class="sr-only">Passwort</span>
                                                </label>
                                                <input id="navbar-login-password" type="password" class="form-control"
                                                       name="<%=LoginServlet.FIELD_PASSWORD%>" placeholder="Passwort"/>
                                            </div>
                                        </fieldset>
                                        <input type="hidden" value="<%=Encoder.forHtmlAttribute(currentUrl) %>" name="<%=LoginServlet.FIELD_REDIRECT_TO%>" />
                                        <div class="dropdown-item">
                                            <input type="submit" class="btn btn-secondary" value="Anmelden" />
                                        </div>
                                    </form>
                                    <div class="dropdown-divider"></div>
                                    <!--
                                        <a href="<%=baseUrl%>/indexlog.jsp">
                                            <span class="dropdown-item">Anmelden</span>
                                        </a>
                                        -->
                                    <a href="<%=baseUrl%>/register.jsp">
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
    <main class="container">