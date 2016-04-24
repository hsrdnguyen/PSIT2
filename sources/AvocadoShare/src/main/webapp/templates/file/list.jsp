<%@ page import="ch.avocado.share.model.data.File" %>
<%@ page import="ch.avocado.share.common.Encoder" %>
<%@ page import="ch.avocado.share.servlet.resources.base.ListViewConfig" %>
<%@ page import="ch.avocado.share.servlet.resources.base.HtmlRenderer" %>
<%@ page import="java.util.Collection" %>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%
    ListViewConfig viewConfig = (ListViewConfig) request.getAttribute(HtmlRenderer.ATTRIBUTE_LIST_VIEW_CONFIG);
    Collection<File> files = viewConfig.getObjects(File.class);
%>
<div class="row">
    <div class="col-lg-8">
        <div class="list-group">
            <div class="list-group-item list-group-header"><h2>Dokumente</h2></div>
            <%
                if (files.size() == 0) {
            %>
            <div class="list-group-item list-group-item-info">
                Wir konnten leider keine Dokumente für Sie finden.
            </div>
            <%
                }
                for (File file : files) {
                    String title = Encoder.forHtml(file.getTitle());
                    String detailLink = "?id=" + Encoder.forUrlAttribute(file.getId());
                    String description = Encoder.forHtml(file.getDescription());
            %>

            <a href="<%=detailLink %>" class="list-group-item">
                <h4 class="list-group-item-heading"><%=title%>
                </h4>
                <p class="list-group-item-text"><%=description %>
                </p>
            </a>
            <% } %>
        </div>
    </div>
</div>