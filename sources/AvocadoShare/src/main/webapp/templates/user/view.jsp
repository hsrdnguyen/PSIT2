<%@ page import="ch.avocado.share.common.Encoder" %>
<%@ page import="ch.avocado.share.controller.UserSession" %>
<%@ page import="ch.avocado.share.model.data.User" %>
<%@ page import="ch.avocado.share.servlet.resources.base.DetailViewConfig" %>
<%@ page import="ch.avocado.share.servlet.resources.base.HtmlRenderer" %>
<%@ page import="ch.avocado.share.common.UrlHelper" %>
<%@ page import="ch.avocado.share.servlet.resources.base.ResourceServlet" %>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%
    DetailViewConfig viewConfig = (DetailViewConfig) request.getAttribute(HtmlRenderer.ATTRIBUTE_DETAIL_VIEW_CONFIG);
    User user = viewConfig.getObject(User.class);
    boolean isOwner = false;
    UserSession viewSession = new UserSession(request);
    if (viewSession.getUser() != null && viewSession.getUser().getId().equals(user.getId())) {
        isOwner = true;
    }
    String userName = Encoder.forHtml(user.getFullName());
    String description = Encoder.forHtml(user.getDescription());
    UrlHelper urlHelper = new UrlHelper(request);
    String avatarUrl = urlHelper.getAvatarUrl(user.getAvatar());
    String editUrl = urlHelper.getBase() + "/user" + "?action=" + ResourceServlet.ACTION_EDIT + "&id=" + user.getId();
    editUrl = Encoder.forHtmlAttribute(editUrl);
%>
<div class="content-main">
    <div class="list-group">
        <div class="list-group-item list-group-header">
            <% if (isOwner) { %>
            <a class="btn btn-primary-outline pull-xs-right" href="<%=editUrl %>">Bearbeiten</a>
            <% } %>
            <h2>Profil</h2>
        </div>
        <div class="list-group-item">
            <img src="<%=Encoder.forHtmlAttribute(avatarUrl)%> "/>
            <span><%=userName%></span>
        </div>
        <% if (!description.isEmpty()) { %>
        <div class="list-group-item">
            <h3>Über mich</h3>
            <p><%=description %>
            </p>
        </div>
        <% } %>
    </div>
</div>