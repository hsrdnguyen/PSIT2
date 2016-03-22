
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
          <form action="document_viewlog.html" class="form-inline">
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

            <a href="upload.html"> <button id="navbar-login-toggle-button" class="btn btn-secondary "
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
  <main class="container">


    <div class="text-contact">


   	<h2>Dateien Hochladen</h2>

  	<div style="collapsed-contact" aria-expanded="true" class="collapse in" id="message-form">
  	<p class="text-block">Die Felder mit * müssen ausgefüllt werden.</p><div id="xform" class="xform">

      <form action="FileUpload.jsp" method="post" id="form_formular" class="contact_action" enctype="multipart/form-data">


  <div class="form-group" id="formular-anrede">
      <label class="control-label" for="category">Kathegorie auswählen</label>
          <select class="form-control" id="category" name="category">
                      <option value="1">Analysis für Dummies</option>
                      <option value="2">Webdesign für Nerds</option>
                      <option value="3">Manit</option>
                      <option value="4">PSIT</option>
              </select>
          </div>
          <input type="file" name="file" size="50" />
  <div class="form-group" id="formular-title">
      <label class="control-label" for="title">Titel *</label>
      <input class="form-control" name="title" id="title" value="" type="text">
      </div>

      <div class="form-group" id="formular-description">
          <label class="control-label" for="description">Beschreibung *</label>
          <textarea class="form-control" name="description" id="description" rows="10"></textarea>
          </div>
  <div class="form-group" id="formular-author">
      <label class="control-label" for="author">Author *</label>
      <input class="form-control" name="author" id="author" value="" type="text">
      </div>
  <div class="form-group" id="formular-class">
      <label class="control-label" for="class">Klasse</label>
      <input class="form-control" name="class" id="class" value="" type="text">
      </div>

  <button class="btn-primary" type="submit" name="submit" id="formular-field-8">Hochladen</button>

  </form>
  </div>
  </div>
  </div>
  </main>
</main>
</div> <!-- /#footerpusher -->
<footer class="text-muted">
  <div class="container">
    <ul>
      <li><a href="#">Über uns</a></li>
      <li><a href="contact.html">Kontakt</a></li>
      <li><a href="#">Fehler melden</a></li>
      <li><a href="#">Profil Bearbeiten</a></li>
    </ul>
    <p>Sorgfälltig mit Liebe erstellt von S. Bergman, L. Kunz, D. T. Nguyen und <span title="De Besti">Cyril Müller</span>.</p>
    <p>&copy; 2015, All rights reserved.</p>
  </div>

</footer>
<script type="application/javascript" src="components/jquery/dist/jquery.min.js"></script>
<script type="application/javascript" src="components/bootstrap/dist/js/bootstrap.min.js"></script>
</body>
</html>
