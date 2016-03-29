
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
  <h1>Dokument bearbeiten</h1>
  <div class="text-contact">

	<div style="collapsed-contact" aria-expanded="true" class="collapse in" id="message-form">
	<p class="text-block">Die Felder mit * müssen ausgefüllt werden.</p><div id="xform" class="xform">

    
      <div class="form-group" id="formular-anrede">
          <label class="control-label" for="formular-field-1">Fach auswählen</label>
              <select class="form-control" id="formular-field-1" name="anrede">
                          <option value="Frau">Kurzeinstieg in Analysis</option>
                          <option value="Herr">Webdesign</option>
                          <option value="Frau">Manit</option>
                          <option value="Herr">PSIT</option>
                  </select>
              </div>
              <div class="form-group" id="formular-nachname">
                  <label class="control-label" for="formular-field-3">Author *</label>
                  <input class="form-control" name="nachname" id="formular-field-3" value="Max Müsli" type="text">
                  </div>


              <div class="form-group" id="formular-vorname">
                  <label class="control-label" for="formular-field-2">Titel *</label>
                  <input class="form-control" name="vorname" id="formular-field-2" value="Analysis für Dummies" type="text">
                  </div>
                  <div class="form-group" id="formular-msg">
                  <label class="control-label" for="formular-field-7">Untertitel</label>
                  <input class="form-control" name="Passwort" id="formular-field-6" value="Lorem ipsum dolor sit amet" type="text">
                  </div>
                    <label class="control-label" for="formular-field-7">Inhalt </label>
                    <div class="form-group" id="formular-msg">

                  <div class="form-group" id="formular-msg">


                          <ul class="nav navbar-nav">



                            <li class="nav-item hidden-xs-down">
                              <div class="dropdown">
                                <button
                                        type="button" id="navbar-login-toggle-button"
                                        data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"
                                        class="btn-primary" type="submit" name="submit" id="formular-field-8">
                                Pt
                                </button>

                                <div id="text-groesse" class="dropdown-menu dropdown-menu22" aria-labelledby="navbar-login-toggle-button">

                                      <a href=""><input  value="10" class="dropdown-item"/>
                                      </a>
                                      <a href=""><input  class="dropdown-item"  value="11">
                                      </a>
                                      <a href=""><input  class="dropdown-item"  value="12">
                                      </a>
                                      <a href=""><input  class="dropdown-item"  value="16">
                                      </a>
                                      <a href=""><input  class="dropdown-item"  value="24">
                                      </a>
                                </div>
                              </div>
                            </li>
                          </ul>

                          <button class="btn-primary" type="" name="submit" id="formular-field-8"><i>  It</i></button>
                          <input name="send" value="1" type="hidden">
                          <button class="btn-primary" type="" name="submit" id="formular-field-8"><b>B</b></button>
                          <input name="send" value="1" type="hidden">
                          <button class="btn-primary" type="" name="submit" id="formular-field-8">Img</button>
                          <input name="send" value="1" type="hidden">

</div>
</div>
                      <textarea class="form-control" name="msg" id="formular-field-7" rows="10">

 Lorem ipsum dolor sit amet, consectetur adipiscing elit. Suspendisse mattis,
 nulla id pretium malesuada, dui est laoreet risus, ac rhoncus eros diam id odio.
 Duis elementum ligula eu ipsum condimentum accumsan.

 Vivamus euismod elit ac libero facilisis tristique. Duis mollis non ligula vel tincidunt. Nulla consectetur
 libero id nunc ornare, vel vulputate tellus mollis. Sed quis nulla magna. Integer rhoncus sem quis ultrices
 lobortis. Maecenas tempus nulla quis dolor vulputate egestas. Phasellus cursus tortor quis massa faucibus fermentum
 vel sit amet tortor. Phasellus vehicula lorem et tortor luctus, a dignissim lacus tempor. Aliquam volutpat
 molestie metus sit amet aliquam. Duis vestibulum quam tortor, sed ultrices orci sagittis nec.
 Sed sit amet metus sit
 Post Title

 Proin fermentum, purus id eleifend molestie, nisl arcu rutrum tellus, a luctus erat tortor ut augue. Vivamus
 feugiat nisi sit amet dignissim fermentum. Duis elementum mattis lacinia. Sed sit amet metus sit amet leo semper
 feugiat. Nulla vel orci nec neque interdum facilisis egestas vitae lorem. Phasellus elit ante, posuere at augue
 quis, porta vestibulum magna. Nullam non mattis odio. Donec eget velit leo. Nunc et diam volutpat tellus ultrices
 fringilla eu a neque. Integer lectus nunc, vestibulum a turpis vitae, malesuada lacinia nibh. In sit amet leo ut

  convallis bibendum. Nullam nec purus sapien. Quisque sollicitudin cursus felis sit amet aliquam. at augue quis,
  porta vestibulum magna. Nullam non mattis odio. Donec eget velit leo. Nunc et diam volutpat tellus ultrices
  fringilla eu a neque. Integer lectus nunc, vestibulum a turpis vitae, malesuada lacinia nibh. In sit amet leo ut
  turpis convallis bibendum. Nullam nec purus sapien. Quisque sollicitudin cursus felis sit amet aliquam. </textarea>
                      </div>




                      <form id="edit-doc"action="document_view2.html" method="POST" action="#edit">






<button class="btn-primary" type="submit" name="submit" id="formular-field-8">Aktualisieren</button>
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
      <li><a href="contact.html">Kontakt</a></li>
      <li><a href="#">Fehler melden</a></li>
      <li><a href="profil.html">Profil Bearbeiten</a></li>
    </ul>
    <p>Sorgfälltig mit Liebe erstellt von S. Bergman, L. Kunz, D. T. Nguyen und <span title="De Besti">Cyril Müller</span>.</p>
    <p>&copy; 2015, All rights reserved.</p>
  </div>

</footer>
<script type="application/javascript" src="components/jquery/dist/jquery.min.js"></script>
<script type="application/javascript" src="components/bootstrap/dist/js/bootstrap.min.js"></script>
</body>
</html>
