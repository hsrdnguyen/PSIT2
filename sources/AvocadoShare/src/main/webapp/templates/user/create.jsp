<%@ page import="ch.avocado.share.common.form.FormBuilder" %>
<%@ page import="ch.avocado.share.controller.UserSession" %>
<%@ page import="ch.avocado.share.model.data.User" %>
<%@ page import="ch.avocado.share.servlet.resources.base.DetailViewConfig" %>
<%@ page import="ch.avocado.share.servlet.resources.base.HtmlRenderer" %>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%
    DetailViewConfig viewConfig = (DetailViewConfig) request.getAttribute(HtmlRenderer.ATTRIBUTE_DETAIL_VIEW_CONFIG);
    User user = viewConfig.getObject(User.class);
    FormBuilder form = new FormBuilder(viewConfig, User.class);
    boolean alreadyLoggedIn = new UserSession(request).isAuthenticated();
%>
<h1>Registrierung</h1>
<% if(alreadyLoggedIn) { %>
    <div class="alert alert-info">
        Sie sind bereits angemeldet.
    </div>
    <script type="application/javascript">
        var currentPaths = document.location.href.split("/");
        document.location.href = currentPaths.slice(0, currentPaths.length - 1).join("/");
    </script>
<% } else { %>
<% if(!form.getFormErrors().isEmpty()) { %>
    <div class="alert alert-danger">
        <%=form.getFormErrors()%>
    </div>
<% } %>
<div>
    <%@include file="register_form.jsp"%>
</div>
<% } %>