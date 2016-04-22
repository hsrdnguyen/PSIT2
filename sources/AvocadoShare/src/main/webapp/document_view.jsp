<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ page import="ch.avocado.share.model.data.File"%>
<%@ page import="java.util.List" %>
<jsp:useBean id="searchBean" class="ch.avocado.share.controller.SearchBean"/>
<jsp:setProperty name="searchBean" property="searchString"/>
<%@include file="includes/header.jsp"%>

  <h1>Dokumentenüberblick</h1>

<br>
  <div class="row">
    <!-- 3 columns on extra large screen,
         2 on large and 1 on smaller screens -->
    <section class="filter">
      <div class="list-group">
        <div class="list-group-item list-group-header"><h2>Filter</h2></div>
        <!-- First Module -->
        <div class="list-group-item">
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
              String fileUrl = baseUrl + "/file?id=" + Encoder.forHtmlAttribute(file.getId());
              String fileTitle = Encoder.forHtml(file.getTitle());
              String fileDescription = Encoder.forHtml(file.getDescription());
        %>

            <a href="<%=fileUrl %>" class="list-group-item">
                <div class="pull-xs-right">
                    <form id="edit-doc" method="POST" action="<%=baseUrl%>/requestAccess.jsp">
                        <input type="submit" id="requestRights" class="btn btn-secondary btn-secondary" type="submit" value="Rechte anfordern" />
                        <input id="fileId" name="fileId" type="hidden" value="<%=file.getId() %>" />
                    </form>
                </div>
                <h4 class="list-group-item-heading"><%=fileTitle %></h4>
                <p class="list-group-item-text"><%=fileDescription %></p>
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
