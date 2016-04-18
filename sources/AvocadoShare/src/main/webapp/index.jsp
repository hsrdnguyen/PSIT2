<%@include file="includes/header.jsp"%>
  <style type="text/css">
    text {
      position: relative;
      -webkit-animation: mymove 3s;  /* Chrome, Safari, Opera */
      -webkit-animation-iteration-count: 1;  /* Chrome, Safari, Opera */
      -webkit-animation-fill-mode: forwards;  /* Chrome, Safari, Opera */
      -webkit-animation-timing-function: ease;  /* Chrome, Safari, Opera */
      animation: mymove 3s;
      animation-iteration-count: 1;
      animation-fill-mode: forwards;
    }

    /* Chrome, Safari, Opera */
    @-webkit-keyframes mymove {
      from {left:-130px;}
      to {left: 0px;}
    }

    @keyframes mymove {
      from {left: -130px;}
      to {left: 0px;}
    }

  </style>

  <h1>Dein Portfolio</h1>
  <div class="row">
    <!-- 3 columns on extra large screen,
         2 on large and 1 on smaller screens -->
    <section class="col-xl-4 col-lg-6">
      <div class="list-group">
        <div class="list-group-item list-group-header"><h2>Module</h2></div>
        <!-- First Module -->
        <a href="document_view.jsp" class="list-group-item">
          <h4 class="list-group-item-heading">Analysis für Dummies</h4>
          <p class="list-group-item-text">Kurzeinstieg in Anlysis.</p>
        </a>
        <!-- Second Module -->
        <a href="#" class="list-group-item">
          <h4 class="list-group-item-heading">Webdesign für Nerds</h4>
          <p class="list-group-item-text">HTML &amp; Unicode ጷm Web.</p>
        </a>
        <!-- Third Module -->
        <a href="#" class="list-group-item">
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
          <p class="list-group-item-text">HTML &amp; Unicode ጷm Web.</p>
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
          <p class="list-group-item-text">HTML &amp; Unicode ጷm Web.</p>
        </a>
        <a href="#" class="list-group-item">
          <h4 class="list-group-item-heading">MANIT</h4>
          <p class="list-group-item-text">Donec id elit non mi porta gravida at eget metus. Maecenas sed diam eget risus varius blandit.</p>
        </a>
      </div>
    </section>
  </div>
<%@include file="includes/footer.jsp"%>