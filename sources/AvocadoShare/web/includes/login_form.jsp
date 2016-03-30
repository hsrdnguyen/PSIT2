<%@page import="ch.avocado.share.servlet.LoginServlet"%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="de">
<head>
  <meta charset="UTF-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Avocado Share</title>
  <link rel="stylesheet" href="../css/bootstrap.min.css">
  <link rel="stylesheet" href="../css/octicons.css">
  <link rel="stylesheet" href="../css/app.css">
</head>
<body>
<section class="fullpage" id="login">
  <div class="vertical-center container">
    <div class="row">
    <% String loginError = (String) request.getAttribute(LoginServlet.LOGIN_ERROR); 
    if(loginError != null) {
    %>
    <div class="error">
    	<%=loginError %>
    </div>
    <% } %>
      <div id="loginform" class="col-xl-offset-3 col-xl-6 col-lg-offset-2 col-lg-8 col-md-offset-1 col-md-10">
        <h2>Anmelden</h2>
          <form action="login" method="POST">
            <div class="form-group row">
              <label for="startpage-login-email" class="col-sm-2 form-control-label">E-Mail</label>
              <div class="col-sm-10">
                <input name="<%=LoginServlet.FIELD_EMAIL %>" type="email" class="form-control" id="startpage-login-email" placeholder="E-Mail-Adresse">
              </div>
            </div>
            <div class="form-group row">
              <label for="startpage-login-password" class="col-sm-2 form-control-label">Passwort</label>
              <div class="col-sm-10">
                <input name="<%=LoginServlet.FIELD_PASSWORD %>" type="password" class="form-control" id="startpage-login-password" placeholder="Passwort">
              </div>
            </div>
            <div class="form-group row">
              <label class="col-sm-2"></label><!-- if no label is required replace with col-push -->
              <div class="col-sm-10">
                <div class="checkbox">
                  <label>
                    <input name="remember" type="checkbox" checked="checked"> angemeldet bleiben
                  </label>
                </div>
              </div>
            </div>
            <div class="form-group row">
              <div class="col-sm-offset-2 col-sm-10">
                <button type="submit" class="btn btn-secondary">Anmelden</button>
              </div>
            </div>
          </form>
      </div>
    </div>
  </div>
</section>
<section class="fullpage" id="about">
  <div class="container">
    <h2>About us</h2>
  </div>
</section>

<footer class="text-muted">
  <div class="container">
    <ul>
      <li><a href="#about">Über uns</a></li>
      <li><a href="#contact">Kontakt</a></li>
      <li><a href="#something">Fehler melden</a></li>
    </ul>
    <p>Sorgfälltig und mit Liebe erstellt von S. Bergman, L. Kunz, D. T. Nguyen und <span title="De Besti">Cyril Müller</span>.</p>
    <p>&copy; 2015, All rights reserved.</p>
  </div>

</footer>
<script type="application/javascript" src="../js/jquery.min.js"></script>
<script type="application/javascript" src="../js/bootstrap.min.js"></script>
</body>
</html>