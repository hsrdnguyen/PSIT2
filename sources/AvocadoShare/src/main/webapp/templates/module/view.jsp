<%@ page import="ch.avocado.share.model.data.Module" %>
<%@ page import="ch.avocado.share.common.Encoder" %>
<%@ page import="ch.avocado.share.servlet.resources.base.DetailViewConfig" %>
<%@ page import="ch.avocado.share.servlet.resources.base.HtmlRenderer" %>
<%@ page import="java.util.List" %>
<%@ page import="ch.avocado.share.service.exceptions.DataHandlerException" %>
<%@ page import="ch.avocado.share.model.exceptions.ServiceNotFoundException" %>
<%@ page import="java.util.ArrayList" %>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%
    DetailViewConfig viewConfig = (DetailViewConfig) request.getAttribute(HtmlRenderer.ATTRIBUTE_DETAIL_VIEW_CONFIG);
    Module module = viewConfig.getObject(Module.class);
    String id = Encoder.forUrlAttribute(module.getId());
    String title = Encoder.forHtml(module.getName());
    String description = Encoder.forHtml(module.getDescription());

    List<File> files;
    boolean fetchFilesFailed = false;
    try {
        files = module.getFiles()
    } catch (DataHandlerException | ServiceNotFoundException e) {
        e.printStackTrace();
        files = new ArrayList<>();
        fetchFilesFailed = true;
    }

    String baseUrlInModule = request.getServletContext().getContextPath();
%>

<div class="row">
    <div class="col-xl-8">
        <div class="list-group">
            <div class="list-group-item list-group-header">
                <h2><%=title %></h2>
                <a class="btn btn-primary" href="?action=edit&id=<%=id %>">Edit</a>
            </div>
            <div class="list-group-item">
                <h3 class="list-group-item-heading">Beschreibung</h3>
                <p><%=description%></p>
            </div>
        </div>
    </div>
    <%@include file="../member/index.jsp"%>
    <div class="col-xl-8">
        <div class="list-group">
            <div class="list-group-item list-group-header">
                <h3>Dateien</h3>
            </div>
<%
                if(fetchFilesFailed) {
%>
                    <div class="list-group-item list-group-item-warning">
                        Die Dateien konnten leider nicht geladen werden.
                        Bitte versuchen Sie es später noch einmal oder kontaktieren Sie ihren Administrator.
                    </div>
<%
                } else {
                    for(File file: files) {
                        String moduleFileName = Encoder.forHtml(file.getTitle())
                        String moduleFileLink = baseUrlInModule + "/file?id=" + Encoder.forHtmlAttribute(file.getId());
                        String moduleFileDescription = Encoder.forHtml(file.getDescription());
%>
                    <a class="list-group-item" href="<%=moduleFileLink %>">
                        <div class="list-group-item-heading">
                            <%=moduleFileName%>
                        </div>
                        <%=moduleFileDescription %>
                    </a>
<%
                    }
                }
%>
        </div>
    </div>
</div>
