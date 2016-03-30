<%@ page import="ch.avocado.share.model.data.Group" %>
<%@ page import="ch.avocado.share.common.Encoder" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<% Group[] groups = (Group[]) request.getAttribute("Groups"); %>
<h1>Meine Gruppen</h1>
<%
    if (groups.length == 0) {
%>
<div class="alert alert-info">
    Wir können leider keine Gruppen für Sie finden.
</div>
<%
} else {
%>
<div class="row">
    <%
        for (Group group : groups) {
            String name = Encoder.forHtml(group.getName());
            String detailLink = "?id=" + Encoder.forUrlAttribute(group.getId());
            String description = Encoder.forHtml(group.getDescription());
    %>
    <div class="col-xs-12">
        <div class="thumbnail">
            <div class="caption">
                <h3>
                    <%=name %>
                </h3>
                <p>
                    <%=description %>
                </p>
                <a class="btn btn-primary" href="<%=detailLink %>">Details</a>
            </div>
        </div>
    </div>
    <%
        } // for groups
    %>
</div>
<%
    } // group.length != 0
%>
