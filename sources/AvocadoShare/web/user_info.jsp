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
              <form method="POST" action="#login">
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
<h1>Benutzerinformationen</h1>
<div class="text-personal">
    <a class="collapsed-personal" data-toggle="collapse" href="#personal" aria-expanded="false" aria-controls="personal">
 	<h2>Personalien</h2>
	</a>
	<div style="collapsed-personal" aria-expanded="true" class="collapse in" id="personal">

    <form action="" method="post" id="form_personal" class="contact_action">

	<div class="form-user" id="formular-username">
    <label class="control-label" for="formular-field-1">Benutzername *</label>
    <input class="form-control" name="benutzername" id="formular-field-1" value="Benutzername" type="text">
    </div>
    <div class="form-prename" id="formular-prename">
    <label class="control-label" for="formular-field-2">Vorname *</label>
    <input class="form-control" name="vorname" id="formular-field-2" value="Vorname" type="text">
    </div>
    <div class="form-surename" id="formular-surename">
    <label class="control-label" for="formular-field-3">Nachname *</label>
    <input class="form-control" name="nachname" id="formular-field-3" value="Nachname" type="text">
    </div>
    <div class="form-email" id="formular-email">
    <label class="control-label" for="formular-field-5">E-mail *</label>
    <input class="form-control" name="email" id="formular-field-4" value="E-Mail" type="email">
    </div>
    </form>
    </div>
</div><br>
<div class="text-security">
    <a class="collapsed-security" data-toggle="collapse" href="#security" aria-expanded="false" aria-controls="security">
 	<h2>Sicherheit</h2>
	</a>
	<div style="collapsed-security" aria-expanded="true" class="collapse in" id="security">

    <form action="" method="post" id="form_security" class="contact_action">

	<div class="form-password" id="formular-password">
    <label class="control-label" for="formular-field-1">Passwort *</label>
    <input class="form-control" name="password_old" id="formular-field-5" value="Current Password" type="text"><br>
    <input class="form-control" name="password_new" id="formular-field-6" value="New Password" type="text"><br>
    <input class="form-control" name="password_confirm" id="formular-field-7" value="Confirm" type="text">
    </div>
    </form>
    </div>
</div><br>
<div class="text_modul">
	<a class="collapsed-modul" data-toggle="collapse" href="#modul" aria-expanded="false" aria-controls="modul">
    <h2>Module</h2>
    </a>
    <div style="collapsed-modul" aria-expanded="true" class="collapse in" id="modul">
    <form action="" method="post" id="form_modul" class="contact_action">
    <!-- Hier kommen Module rein -->
    <p>Hier kommen Module rein</p>
    </form>
    </div>
    </div>
</main>
</div>
<footer class="text-muted">
  <div class="container">
    <ul>
      <li><a href="#about">Über uns</a></li>
      <li><a href="contact.html" href="#contact">Kontakt</a></li>
      <li><a href="#something">Fehler melden</a></li>
    </ul>
    <p>Sorgfälltig mit Liebe erstellt von S. Bergman, L. Kunz, D. T. Nguyen und <span title="De Besti">Cyril Müller</span>.</p>
    <p>&copy; 2015, All rights reserved.</p>
  </div>
  </footer>
 <script type="application/javascript" src="components/jquery/dist/jquery.min.js"></script>
 <script type="application/javascript" src="js/bootstrap.min.js"></script>
 </body>
 </html>
