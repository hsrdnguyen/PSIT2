<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ page import="ch.avocado.share.common.Encoder" %>
<%@ page import="ch.avocado.share.common.ServiceLocator" %>
<%@ page import="ch.avocado.share.model.exceptions.ServiceNotFoundException" %>
<%@ page import="ch.avocado.share.service.IFileStorageHandler" %>
<%@ page import="ch.avocado.share.service.exceptions.FileStorageException" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%
    File fileForInfos = viewConfig.getObject(File.class);
    SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy 'um' HH:mm:ss");
    String creationDate = Encoder.forHtml(format.format(fileForInfos.getCreationDate()));
    String updateDate = Encoder.forHtml(format.format(fileForInfos.getLastChanged()));
    long size = 0;
    try {
        IFileStorageHandler fileStorageHandler = ServiceLocator.getService(IFileStorageHandler.class);
        size = fileStorageHandler.getFileSize(fileForInfos.getPath());
    } catch (ServiceNotFoundException e) {
        e.printStackTrace();
    } catch (FileStorageException e) {
        e.printStackTrace();
    }

%>
<div class="list-group">
    <div class="list-group-item list-group-header">
        <h4>Informationen</h4>
    </div>
    <div class="list-group-item">
        <div class="text-muted">Content Type</div>
        <span><%=Encoder.forHtml(fileForInfos.getMimeType()) %></span>
    </div>
    <div class="list-group-item">
        <div class="text-muted">Erstellt</div>
        <span><%=creationDate %></span>
    </div>

    <div class="list-group-item">
        <div class="text-muted">Geändert</div>
        <span><%=updateDate %></span>
    </div>
    <% if(size != 0) { %>
    <div class="list-group-item">
        <div class="text-muted">Grösse</div>
        <span><%=size %></span>
    </div>
    <% } %>

</div>