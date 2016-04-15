<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="ch.avocado.share.model.data.File" %>
<%@ page import="ch.avocado.share.common.Encoder" %>
<%@ page import="ch.avocado.share.common.preview.factory.DefaultPreviewFactory" %>
<%@ page import="ch.avocado.share.common.preview.IPreviewGenerator" %>
<%@ page import="ch.avocado.share.servlet.resources.base.DetailViewConfig" %>
<%@ page import="ch.avocado.share.servlet.resources.base.HtmlRenderer" %>
<%@ page import="ch.avocado.share.common.constants.SQLQueryConstants" %>
<%
    DetailViewConfig viewConfig = (DetailViewConfig) request.getAttribute(HtmlRenderer.ATTRIBUTE_DETAIL_VIEW_CONFIG);
    File file = viewConfig.getObject(File.class);
    String id = Encoder.forUrlAttribute(file.getId());
    String title = Encoder.forHtml(file.getTitle());
    String description = Encoder.forHtml(file.getDescription());
    DefaultPreviewFactory defaultPreviewFactory = new DefaultPreviewFactory();
    IPreviewGenerator previewGenerator = null;
    previewGenerator = defaultPreviewFactory.getInstanceAndHandleErrors(file);
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
        <div class="list-group-item">
            <h3 class="list-group-item-heading">Vorschau</h3>
            <div class="preview">
                <%=previewGenerator.getPreview() %>
            </div>
        </div>
    </div>
</section>
