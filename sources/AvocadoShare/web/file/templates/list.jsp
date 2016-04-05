<%@ page import="ch.avocado.share.model.data.File" %>
<%@ page import="ch.avocado.share.common.Encoder" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<% List<File> files = (List<File>) request.getAttribute("Files"); %>
<section class="document-show">
    <div class="list-group">
        <div class="list-group-item list-group-header"><h2>Dokumente</h2></div>
        <%
            if (files.size() == 0) {
        %>
            <div class="list-group-item list-group-item-info">
                Wir konnten leider keine Dokumente für Sie finden.
            </div>
        <%
            }
            for (File file : files) {
                String title = Encoder.forHtml(file.getTitle());
                String detailLink = "?id=" + Encoder.forUrlAttribute(file.getId());
                String description = Encoder.forHtml(file.getDescription());
        %>

        <a href="<%=detailLink %>" class="list-group-item">
            <h4 class="list-group-item-heading"><%=title%>
            </h4>
            <p class="list-group-item-text"><%=description %>
            </p>
        </a>
        <% } %>
    </div>
</section>