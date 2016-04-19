<%@ page import="ch.avocado.share.model.data.File"%>
<%@ page import="java.util.List" %>
<jsp:useBean id="searchBean" class="ch.avocado.share.controller.SearchBean"/>
<jsp:setProperty name="searchBean" property="searchString"/>
<%@include file="includes/header.jsp"%>
  <h1>Dokumentenüberblick</h1>
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

        <%
          List<File> results = searchBean.search();

          for (File file : results) {

        %>
            <a href="<%out.write("/file?id="+file.getId());%>" class="list-group-item">
            <h4 class="list-group-item-heading"><%out.write(file.getTitle());%></h4>
            <p class="list-group-item-text"><%out.write(file.getDescription());%></p>
            </a>
        <%
          }
        %>
      </div>
    </section>
  </div>
<%@include file="includes/footer.jsp"%>
</body>
</html>
