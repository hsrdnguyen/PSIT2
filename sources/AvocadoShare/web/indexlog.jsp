<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF8" %>
<!DOCTYPE html>
<html lang="de">
<head>
  <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Avocado Share</title>
  <link rel="stylesheet" href="components/bootstrap/dist/css/bootstrap.min.css">
  <link rel="stylesheet" href="components/octicons/octicons/octicons.css">
  <link rel="stylesheet" href="css/app.css">
</head>
<body>
<div id="footerpusher">
<header>
    <nav class="navbar navbar-light bg-faded">
      <div class="container">
      <button class="navbar-toggler hidden-sm-up" type="button" data-toggle="collapse" data-target="#exCollapsingNavbar2">
        &#9776;
      </button>
      <div class="collapse navbar-toggleable-xs" id="exCollapsingNavbar2">
      <a href="indexlog.html" class="navbar-brand" href="#">Avocado Share</a>
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
          <form action="document_view.html" class="form-inline">
            <div class="input-group">
              <input type="text" class="form-control" placeholder="Search for...">
              <span class="input-group-btn">
                <button class="btn btn-secondary" type="submit">Los!</button>
              </span>
            </div>
          </form>
        </li>
        <!-- Navbar Login formular -->
        <li class="nav-item hidden-xs-down pull-md-right">
          <div class="dropdown">
            <button class="btn btn-secondary dropdown-toggle"
                    type="button" id="navbar-login-toggle-button"
                    data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
              <span class="octicon octicon-person"></span>
            </button>



            <a href="upload.html">



              <button id="navbar-login-toggle-button" class="btn btn-secondary "
              type="submit">Upload</button></a>


            <div id="navbar-login" class="dropdown-menu" aria-labelledby="navbar-login-toggle-button">
              <form action="index.html" method="POST" action="#login">
                <fieldset>
                  <div class="input-group dropdown-item">
                    <label for="navbar-login-username" class="input-group-addon">
                      <span class="octicon octicon-person"></span>

                  <input type="submit" value="Logout" class="dropdown-item"/>
                </fieldset>
              </form>
            </div>
          </div>
        </li>
      </ul>
      </div>
      </div>
    </nav>
</header>
<main class="container">
  <h1>Dein Portfolio</h1>
  <div class="row">
    <!-- 3 columns on extra large screen,
         2 on large and 1 on smaller screens -->
    <section class="col-xl-4 col-lg-6">
      <div class="list-group">
        <div class="list-group-item list-group-header"><h2>Module</h2></div>
        <!-- First Module -->
        <a href="document_viewlog.html" class="list-group-item">
          <h4 class="list-group-item-heading">Analysis für Dummies</h4>
          <p class="list-group-item-text">Kurzeinstieg in Anlysis.</p>
        </a>
        <!-- Second Module -->
        <a href="document_view.html" class="list-group-item">
          <h4 class="list-group-item-heading">Webdesign für Nerds</h4>
          <p class="list-group-item-text">HTML &amp; Unicode á·m Web.</p>
        </a>
        <!-- Third Module -->
        <a href="document_view.html" class="list-group-item">
          <h4 class="list-group-item-heading">MANIT</h4>
          <p class="list-group-item-text">Donec id elit non mi porta gravida at eget metus. Maecenas sed diam eget risus varius blandit.</p>
        </a>
      </div>
    </section>
    <section class="col-xl-4 col-lg-6">
      <div class="list-group">
        <div class="list-group-item list-group-header"><h2>Kürzlich hinzugefügte Dateien</h2></div>
        <a href="#" class="list-group-item">
          <h4 class="list-group-item-heading">Analysis für Dummies</h4>
          <p class="list-group-item-text">Kurzeinstieg in Anlysis.</p>
        </a>
        <a href="#" class="list-group-item">
          <h4 class="list-group-item-heading">Webdesign für Nerds</h4>
          <p class="list-group-item-text">HTML &amp; Unicode á·m Web.</p>
        </a>
        <a href="#" class="list-group-item">
          <h4 class="list-group-item-heading">MANIT</h4>
          <p class="list-group-item-text">Donec id elit non mi porta gravida at eget metus. Maecenas sed diam eget risus varius blandit.</p>
        </a>
      </div>
    </section>
    <section class="col-xl-4 col-lg-6">
      <div class="list-group">
        <div class="list-group-item list-group-header"><h2>Zuletzt besucht</h2></div>
        <a href="#" class="list-group-item">
          <h4 class="list-group-item-heading">Analysis für Dummies</h4>
          <p class="list-group-item-text">Kurzeinstieg in Anlysis.</p>
        </a>
        <a href="#" class="list-group-item">
          <h4 class="list-group-item-heading">Webdesign für Nerds</h4>
          <p class="list-group-item-text">HTML &amp; Unicode á·m Web.</p>
        </a>
        <a href="#" class="list-group-item">
          <h4 class="list-group-item-heading">MANIT</h4>
          <p class="list-group-item-text">Donec id elit non mi porta gravida at eget metus. Maecenas sed diam eget risus varius blandit.</p>
        </a>
      </div>
    </section>
  </div>
</main>
</div> <!-- /#footerpusher -->
<footer class="text-muted">
  <div class="container">
    <ul>
      <li><a href="#">Ãber uns</a></li>
      <li><a href="contact.html">Kontakt</a></li>
      <li><a href="#">Fehler melden</a></li>
      <li><a href="#">Profil Bearbeiten</a></li>
    </ul>
    <p>SorgfÃ¤lltig mit Liebe erstellt von S. Bergman, L. Kunz, D. T. Nguyen und <span title="De Besti">Cyril Müller</span>.</p>
    <p>&copy; 2015, All rights reserved.</p>
  </div>

</footer>
<script type="application/javascript" src="components/jquery/dist/jquery.min.js"></script>
<script type="application/javascript" src="components/bootstrap/dist/js/bootstrap.min.js"></script>
</body>
</html>
