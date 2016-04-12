<%@ page import="ch.avocado.share.model.data.Module" %>
<%@ page import="ch.avocado.share.common.Encoder" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    Module module = (Module) request.getAttribute("Module");
    String id = Encoder.forUrlAttribute(module.getId());
    String title = Encoder.forHtml(module.getName());
    String description = Encoder.forHtml(module.getDescription());
%>
<section class="document-show">
    <div class="list-group">
        <div class="list-group-item list-group-header">
            <h2><%=title %></h2>
            <a class="btn btn-primary" href="?action=edit&id=<%=id %>">Edit</a>
            <a class="btn btn-primary" href="/download?download=yes&id=<%=id %>" target="_blank">Download</a>
        </div>
        <div class="list-group-item">
            <h3 class="list-group-item-heading">Beschreibung</h3>
            <p><%=description%></p>
        </div>
    </div>
</section>