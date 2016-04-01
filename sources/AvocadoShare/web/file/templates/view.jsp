<%@ page import="ch.avocado.share.model.data.File" %>
<%@ page import="ch.avocado.share.common.Encoder" %>
<%@ page import="ch.avocado.share.common.preview.factory.DefaultPreviewFactory" %>
<%@ page import="ch.avocado.share.common.preview.PreviewException" %>
<%@ page import="ch.avocado.share.common.preview.IPreviewGenerator" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    File file = (File) request.getAttribute("File");
    String id = Encoder.forUrlAttribute(file.getId());
    String title = Encoder.forHtml(file.getTitle());
    String description = Encoder.forHtml(file.getDescription());
    DefaultPreviewFactory defaultPreviewFactory = new DefaultPreviewFactory();
    IPreviewGenerator previewGenerator = null;
    previewGenerator = defaultPreviewFactory.getInstanceAndHandleErrors(file);
%>
<section class="document-show">
    <div class="list-group">
        <div class="list-group-item list-group-header"><h2><%=title %></h2></div>
        <div class="list-group-item">
            <h3 class="list-group-header">Beschreibung</h3>
            <p><%=description%></p>
        </div>
        <div class="list-group-item">
            <h3 class="list-group-header">Vorschau</h3>
            <div class="preview">
                <%=previewGenerator.getPreview() %>
            </div>
        </div>
    </div>
</section>
<a class="btn btn-primary" href="?action=edit&id=<%=id %>">Edit</a>
<a class="btn btn-primary" href="/download?download=yes&id=<%=id %>" target="_blank">Download</a>
