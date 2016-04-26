<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ page import="ch.avocado.share.common.preview.IPreviewGenerator" %>
<%@ page import="ch.avocado.share.common.preview.factory.DefaultPreviewFactory" %>
<%@ page import="ch.avocado.share.model.data.File" %>
<%@ page import="ch.avocado.share.servlet.resources.base.DetailViewConfig" %>
<%@ page import="ch.avocado.share.servlet.resources.base.HtmlRenderer" %>
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
<div class="row">
    <div class="content-main">
        <div class="list-group">
            <div class="list-group-item list-group-header">
                <div class="pull-xs-right btn-group" role="group">
                    <a class="btn btn-secondary-outline" href="?action=edit&id=<%=id %>"><span
                            class="octicon octicon-pencil"></span></a>
                    <%-- TODO: fix absolute link --%>
                    <a class="btn btn-primary-outline" href="/download?download=yes&id=<%=id %>" target="_blank">Download</a>
                </div>
                <h2><%=title %>
                </h2>
            </div>
            <div class="list-group-item">
                <h3 class="list-group-item-heading">Beschreibung</h3>
                <p><%=description%>
                </p>
            </div>
            <div class="list-group-item">
                <h3 class="list-group-item-heading">Vorschau</h3>
                <div class="preview">
                    <%=previewGenerator.getPreview() %>
                </div>
            </div>
        </div>
    </div>
    <div class="content-right">
        <%@include file="../member/index.jsp" %>
        <%@include file="info.jsp"%>
    </div>
</div>
