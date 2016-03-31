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
    DefaultPreviewFactory defaultPreviewFactory = new DefaultPreviewFactory();
    IPreviewGenerator previewGenerator = null;
    previewGenerator = defaultPreviewFactory.getInstanceAndHandleErrors(file);
%>

<h2><%=title %></h2>

<h3>Vorschau</h3>
<div class="preview">
<%=previewGenerator.getPreview() %>
</div>
<a class="btn btn-primary" href="?action=edit&id=<%=id %>">Edit</a>
<a class="btn btn-primary" href="/download?id=<%=id %>" target="_blank">Download</a>
