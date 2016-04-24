<%@ page import="ch.avocado.share.service.exceptions.DataHandlerException" %>
<%@ page import="ch.avocado.share.servlet.resources.base.DetailViewConfig" %>
<%@ page import="ch.avocado.share.servlet.resources.base.HtmlRenderer" %>
<%@ page import="ch.avocado.share.servlet.resources.base.ResourceServlet" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>
<%@ page import="ch.avocado.share.model.data.*" %>
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
        files = module.getFiles();
    } catch (DataHandlerException e) {
        e.printStackTrace();
        files = new ArrayList<File>();
        fetchFilesFailed = true;
    }

    String baseUrlInModule = request.getServletContext().getContextPath();
%>

<div class="row">
    <div class="col-xl-8">
        <div class="list-group">
            <div class="list-group-item list-group-header">
                <% if(viewConfig.getAccess().containsLevel(AccessLevelEnum.MANAGE)) { %>
                    <a title="Module bearbeiten" class="btn btn-secondary-outline pull-xs-right" href="?action=edit&id=<%=id %>">
                        <span class="octicon octicon-pencil"></span>
                    </a>
                <% } %>
                <h2><%=title %></h2>
            </div>
            <div class="list-group-item">
                <h3 class="list-group-item-heading">Beschreibung</h3>
                <p><%=description%></p>
            </div>
        </div>

        <div class="list-group">
            <div class="list-group-item list-group-header">
                <% if(viewConfig.getAccess().containsLevel(AccessLevelEnum.WRITE)) { %>
                <a href="<%=baseUrlInModule %>/file?action=<%=ResourceServlet.ACTION_CREATE%>&m=<%=Encoder.forHtmlAttribute(module.getId())%>"
                   class="btn btn-primary-outline pull-xs-right" title="Neue Datei hochladen">
                    <span class="octicon octicon-cloud-upload"></span>
                </a>
                <% } %>
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
            } else if(files.isEmpty()) { %>
            <div class="list-group-item">
                Es existieren noch keine Dateien in diesem Modul.
            </div>
            <%
            } else {
                for(File file: files) {
                    String moduleFileName = Encoder.forHtml(file.getTitle());
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
    <div class="col-xl-4">
        <%@include file="../member/index.jsp"%>
    </div>
</div>
