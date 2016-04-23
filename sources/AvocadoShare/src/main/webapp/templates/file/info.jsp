<%@ page import="ch.avocado.share.common.Encoder" %>
<%@ page import="ch.avocado.share.servlet.resources.base.ViewConfig" %>
<%
    File fileForInfos = viewConfig.getObject(File.class);
%>
<div class="list-group">
    <div class="list-group-item list-group-header">
        <h4>Informationen</h4>
    </div>
    <div class="list-group-item">
        <div class="text-muted">Content Type</div>
        <span><%=Encoder.forHtml(fileForInfos.getMimeType() %></span>
    </div>
</div>