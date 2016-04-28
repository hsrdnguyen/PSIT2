<%@ page import="ch.avocado.share.common.Encoder" %>
<%@ page import="ch.avocado.share.controller.UserSession" %>
<%@ page import="ch.avocado.share.model.data.User" %>
<%@ page import="ch.avocado.share.servlet.resources.base.DetailViewConfig" %>
<%@ page import="ch.avocado.share.servlet.resources.base.HtmlRenderer" %>
<%@ page import="ch.avocado.share.common.UrlHelper" %>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%
    DetailViewConfig viewConfig = (DetailViewConfig) request.getAttribute(HtmlRenderer.ATTRIBUTE_DETAIL_VIEW_CONFIG);
    User user = viewConfig.getObject(User.class);
    boolean isOwner = false;
    String urlId = Encoder.forUrlAttribute(user.getId());
    UserSession viewSession = new UserSession(request);
    if(viewSession.getUser() != null && viewSession.getUser().getId().equals(user.getId())) {
        isOwner = true;
    }
    String userName = Encoder.forHtml(user.getFullName());
    String description = Encoder.forHtml(user.getDescription());
    UrlHelper urlHelper = new UrlHelper(request);
    String avatarUrl = urlHelper.getAvatarUrl(user.getAvatar());
%>
<img src="<%=Encoder.forHtmlAttribute(avatarUrl)%>" />
<h2><%=userName%></h2>
<% if(isOwner) { %>
<a class="btn btn-primary" href="?action=edit&id=<%=urlId %>">Bearbeiten</a>
<% } %>
<p>
    <%=description %>
</p>