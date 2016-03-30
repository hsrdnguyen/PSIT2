<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="ch.avocado.share.service.Mock.SecurityHandlerMock" %>
<%@ page import="ch.avocado.share.controller.UserSession" %>
<%@ page import="ch.avocado.share.common.ServiceLocator" %>
<%@ page import="ch.avocado.share.service.ISecurityHandler" %>
<%@ page import="ch.avocado.share.model.data.AccessLevelEnum" %>
<jsp:useBean id="fileBean" class="ch.avocado.share.controller.FileUploadBean"/>
<jsp:setProperty name="fileBean" property="id"/>
<jsp:setProperty name="fileBean" property="moduleId"/>
<jsp:setProperty name="fileBean" property="description" />
<jsp:setProperty name="fileBean" property="title" />
<jsp:setProperty name="fileBean" property="method" />
<jsp:setProperty name="fileBean" property="action" />
<%
    request.getRequestDispatcher("../includes/header.jsp").include(request, response);
    SecurityHandlerMock.use();
    SecurityHandlerMock securityHandlerMock = (SecurityHandlerMock) ServiceLocator.getService(ISecurityHandler.class);
    new UserSession(request).authenticate(securityHandlerMock.getUserWithAccess(AccessLevelEnum.READ));
    fileBean.renderRequest(request, response);
    request.getRequestDispatcher("../includes/footer.jsp").include(request, response);
%>
