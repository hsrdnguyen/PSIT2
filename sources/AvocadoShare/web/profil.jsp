
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
      <a href="indexlog.jsp" class="navbar-brand" href="#">Avocado Share</a>
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
        <!-- Navbar Login formular -->
        <li class="nav-item hidden-xs-down pull-md-right">
          <div class="dropdown">
            <button class="btn btn-secondary dropdown-toggle"
                    type="button" id="navbar-login-toggle-button"
                    data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
              <span class="octicon octicon-person"></span>
            </button>



            <a href="upload.jsp">



              <button id="navbar-login-toggle-button" class="btn btn-secondary "
              type="submit">Upload</button></a>


            <div id="navbar-login" class="dropdown-menu" aria-labelledby="navbar-login-toggle-button">
              <form action="index.jsp" method="POST" action="#login">
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
<main class="container container1">
  <h1>Benutzerdaten aktualisieren</h1>
  <div class="text-contact">

	<div style="collapsed-contact" aria-expanded="true" class="collapse in" id="message-form">
	<p class="text-block">Die Felder mit * müssen ausgefüllt werden.</p><div id="xform" class="xform">

    <form action="" method="post" id="form_formular" class="contact_action">

<div class="form-group" id="formular-anrede">
    <label class="control-label" for="formular-field-1">Anrede</label>
        <select class="form-control" id="formular-field-1" name="anrede">
                    <option value="Frau">Frau</option>
                    <option value="Herr">Herr</option>
            </select>
        </div>
<div class="form-group" id="formular-vorname">
    <label class="control-label" for="formular-field-2">Vorname *</label>
    <input class="form-control" name="vorname" id="formular-field-2" value="Studentname" type="text">
    </div>
<div class="form-group" id="formular-nachname">
    <label class="control-label" for="formular-field-3">Nachname *</label>
    <input class="form-control" name="nachname" id="formular-field-3" value="Studentnachname" type="text">
    </div>
<div class="form-group" id="formular-class">
    <label class="control-label" for="formular-field-4">Klasse</label>
    <input class="form-control" name="class" id="formular-field-4" value="TB15 Win" type="text">
    </div>
<div class="form-group form-email" id="formular-email">
    <label class="control-label" for="formular-field-5">E-mail *</label>
    <input class="form-control" name="email" id="formular-field-5" value="student@zhaw.ch" type="email">
    </div>
<div class="form-group" id="formular-betreff">
    <label class="control-label" for="formular-field-6">Passwort *</label>
    <input class="form-control" name="Passwort" id="formular-field-6" value="world123" type="text">
    </div>
    <div class="form-group" id="formular-betreff">
        <label class="control-label" for="formular-field-6">Passwort bestätigen *</label>
        <input class="form-control" name="Passwort" id="formular-field-6" value="world123" type="text">
        </div>
<div class="form-group" id="formular-msg">
    <label class="control-label" for="formular-field-7">Berechtigung</label>
    <input class="form-control" name="Passwort" id="formular-field-6" value="Student" type="text">
    </div>



    <div class="form-group" id="formular-msg">
        <label class="control-label" for="formular-field-7">
          Profilbild</label>
  <div class="form-group" id="formular-msg">
          <button class="btn-primary" type="submit" name="submit" id="formular-field-8">Datei auswählen...</button>
          <input name="send" value="1" type="hidden">
        <button class="btn-primary" type="submit" name="submit" id="formular-field-8">Hochladen</button>
        <input name="send" value="1" type="hidden">

        </div></div>



<button class="btn-primary" type="submit" name="submit" id="formular-field-8">Senden</button>
<input name="send" value="1" type="hidden">
</form>
</div>
</div>
</div>
</main>
</div> <!-- /#footerpusher -->
<footer class="text-muted">
  <div class="container">
    <ul>
      <li><a href="#">Über uns</a></li>
      <li><a href="contact.jsp">Kontakt</a></li>
      <li><a href="#">Fehler melden</a></li>
      <li><a href="profil.jsp">Profil Bearbeiten</a></li>
    </ul>
    <p>Sorgfälltig mit Liebe erstellt von S. Bergman, L. Kunz, D. T. Nguyen und <span title="De Besti">Cyril Müller</span>.</p>
    <p>&copy; 2015, All rights reserved.</p>
  </div>

</footer>
<script type="application/javascript" src="components/jquery/dist/jquery.min.js"></script>
<script type="application/javascript" src="components/bootstrap/dist/js/bootstrap.min.js"></script>
</body>
</html>
