<%@ page import="ch.avocado.share.model.data.File" %>
<%@ page import="ch.avocado.share.common.Encoder" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<% File[] files = (File[]) request.getAttribute("Files"); %>
<section class="document-show">
    <div class="list-group">
        <div class="list-group-item list-group-header"><h2>Dokumente</h2></div>
<%
    for(File file: files) {
        String title = Encoder.forHtml(file.getTitle());
        String detailLink = "?id=" + Encoder.forUrlAttribute(file.getId());
        String description = Encoder.forHtml(file.getDescription());
%>

        <a href="<%=detailLink %>" class="list-group-item">
            <h4 class="list-group-item-heading"><%=title%></h4>
            <p class="list-group-item-text"><%=description %></p>
        </a>
<% } %>
    </div>
</section>