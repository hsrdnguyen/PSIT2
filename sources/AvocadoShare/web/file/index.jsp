<%@ page import="ch.avocado.share.service.Mock.SecurityHandlerMock" %>
<%@ page import="ch.avocado.share.controller.UserSession" %>
<%@ page import="ch.avocado.share.common.ServiceLocator" %>
<%@ page import="ch.avocado.share.service.ISecurityHandler" %>
<%@ page import="ch.avocado.share.model.data.AccessLevelEnum" %><%--
  Created by IntelliJ IDEA.
  User: coffeemakr
  Date: 29.03.16
  Time: 08:05
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:useBean id="fileBean" class="ch.avocado.share.controller.FileUploadBean"/>
<jsp:setProperty name="fileBean" property="id"/>
<jsp:setProperty name="fileBean" property="moduleId"/>
<jsp:setProperty name="fileBean" property="description" />
<jsp:setProperty name="fileBean" property="title" />
<jsp:setProperty name="fileBean" property="method" />
<jsp:setProperty name="fileBean" property="action" />
<html>
<head>
    <title>File</title>
</head>
<body>
<%
    SecurityHandlerMock.use();
    SecurityHandlerMock securityHandlerMock = (SecurityHandlerMock) ServiceLocator.getService(ISecurityHandler.class);
    new UserSession(request).authenticate(securityHandlerMock.getUserWithAccess(AccessLevelEnum.READ));
    fileBean.renderRequest(request, response);
%>
</body>
</html>
