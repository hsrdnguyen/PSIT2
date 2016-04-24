<%@ page import="ch.avocado.share.common.Encoder" %>
<%@ page import="ch.avocado.share.model.data.Group" %>
<%@ page import="ch.avocado.share.servlet.resources.base.HtmlRenderer" %>
<%@ page import="ch.avocado.share.servlet.resources.base.ListViewConfig" %>
<%@ page import="java.util.Collection" %>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%
    ListViewConfig listViewConfig = (ListViewConfig) request.getAttribute(HtmlRenderer.ATTRIBUTE_LIST_VIEW_CONFIG);
    Collection<Group> groups = listViewConfig.getObjects(Group.class);
%>
<section class="document-show">
    <div class="list-group">
        <div class="list-group-item list-group-header">
            <h2>Meine Gruppen</h2>
        </div>
        <%
            if (groups.size() == 0) {
        %>
        <div class="list-group-item list-group-item-info">
            Wir können leider keine Gruppen für Sie finden.
        </div>
        <%
        } else {
        %>
        <%
            for (Group group : groups) {
                String name = Encoder.forHtml(group.getName());
                String detailLink = "?id=" + Encoder.forUrlAttribute(group.getId());
                String description = Encoder.forHtml(group.getDescription());
        %>
        <a href="<%=detailLink %>" class="list-group-item">
            <h3 class="list-group-item-heading">
                <%=name %>
            </h3>
            <p>
                <%=description %>
            </p>
        </a>
        <%
            } // for groups
        %>
        <%
            } // group.length != 0
        %>
    </div>
</section>
