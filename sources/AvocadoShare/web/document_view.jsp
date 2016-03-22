<!DOCTYPE html>
<html lang="de">
<head>
  <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Avocado Share</title>
  <link rel="stylesheet" href="css/bootstrap.min.css">
  <link rel="stylesheet" href="css/octicons.css">
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
      <a href="index.html" class="navbar-brand" href="#">Avocado Share</a>
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

            <div id="navbar-login" class="dropdown-menu" aria-labelledby="navbar-login-toggle-button">
              <form method="POST" action="document_viewlog.html">
                <fieldset>
                  <div class="input-group dropdown-item">
                    <label for="navbar-login-username" class="input-group-addon">
                      <span class="octicon octicon-person"></span>
                      <!-- Display label without css and placeholder -->
                      <span class="sr-only">Benutzername</span>
                    </label>
                    <input id="navbar-login-username" type="text" class="form-control" placeholder="Benutzername">
                  </div>
                  <div class="input-group dropdown-item">
                    <label for="navbar-login-password" class="input-group-addon">
                      <span class="octicon octicon-lock"></span>
                      <!-- Display label without css and placeholder -->
                      <span class="sr-only">Passwort</span>
                    </label>
                    <input id="navbar-login-password" type="password" class="form-control" placeholder="Passwort">
                  </div>
                  <input type="submit" value="Anmelden" class="dropdown-item"/>
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
  <h1>Dokumenten�berblick</h1>
   <form class="form-inline">
            <div class="input-group">
              <input type="text" class="form-control" placeholder="Suchwort">
              <span class="input-group-btn">
                <button class="btn btn-secondary" type="submit">Suchen</button>
              </span>

            </div>
   </form><br>
  <div class="row">
    <!-- 3 columns on extra large screen,
         2 on large and 1 on smaller screens -->
    <section class="filter">
      <div class="list-group">
        <div class="list-group-item list-group-header"><h2>Filter</h2></div>
        <!-- First Module -->
        <div class="tags">
        <label>
            <input type="checkbox" rel="dab" />
            Datenbank
        </label><br>
        <label>
            <input type="checkbox" rel="web" />
            Web
        </label><br>
        <label>
            <input type="checkbox" rel="prog" />
            Programmieren
        </label><br>
        <label>
            <input type="checkbox" rel="thin" />
            Theoretische Informatik
        </label><br>
        <label>
            <input type="checkbox" rel="manit" />
            Mathematik Analysis
        </label><br>
        <label>
            <input type="checkbox" rel="mgmit" />
           Diskrete Mathematik
        </label><br>
        <label>
            <input type="checkbox" rel="etc" />
            ...
        </label><br>
    </div>
      </div>
    </section>
    <section class="document-show">
      <div class="list-group">
        <div class="list-group-item list-group-header"><h2>Dokumente</h2></div>
        <a href="document_view0.html" class="list-group-item">
          <h4 class="list-group-item-heading">Analysis f�r Dummies</h4>
          <p class="list-group-item-text">Kurzeinstieg in Anlysis.</p>
        </a>
        <a href="#" class="list-group-item">
          <h4 class="list-group-item-heading">Webdesign f�r Nerds</h4>
          <p class="list-group-item-text">HTML &amp; Unicode im Web.</p>
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
      <li><a href="#about">Über uns</a></li>
      <li><a href="contact.html" href="#contact">Kontakt</a></li>
      <li><a href="#something">Fehler melden</a></li>
    </ul>
    <p>Sorgfälltig mit Liebe erstellt von S. Bergman, L. Kunz, D. T. Nguyen und <span title="De Besti">Cyril M�ller</span>.</p>
    <p>&copy; 2015, All rights reserved.</p>
  </div>

</footer>
<script type="application/javascript" src="js/jquery.min.js"></script>
<script type="application/javascript" src="js/bootstrap.min.js"></script>
</body>
</html>
