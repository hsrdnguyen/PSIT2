<%@ page import="ch.avocado.share.common.Encoder" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%
    File fileForInfos = viewConfig.getObject(File.class);
    SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy 'um' HH:mm:ss");
    String creationDate = Encoder.forHtml(format.format(fileForInfos.getCreationDate()));
    String updateDate = Encoder.forHtml(format.format(fileForInfos.getLastChanged()));
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
        <div class="text-muted">Erstelldatum</div>
        <span><%=creationDate %></span>
    </div>

    <div class="list-group-item">
        <span><%=updateDate %></span>
    </div>
</div>