<%@ page import="ch.avocado.share.controller.UserSession" %>
<%@ page import="ch.avocado.share.service.ISecurityHandler" %>
<%@ page import="ch.avocado.share.common.ServiceLocator" %>
<%@ page import="ch.avocado.share.model.data.AccessLevelEnum" %>
<%@ page import="ch.avocado.share.service.Mock.*" %><%--

Installs all dummy handlers
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    GroupDataHandlerMock.use();
    UserDataHandlerMock.use();
    SecurityHandlerMock.use();
    FileDataHandlerMock.use();
    FileStorageHandlerMock.use();
    SecurityHandlerMock securityHandlerMock = (SecurityHandlerMock) ServiceLocator.getService(ISecurityHandler.class);
    UserSession userSession = new UserSession(request);
    userSession.authenticate(securityHandlerMock.getUserWithAccess(AccessLevelEnum.OWNER));
%>
<div class="alert alert-success">
    Dummy handlers are ready to use!
</div>