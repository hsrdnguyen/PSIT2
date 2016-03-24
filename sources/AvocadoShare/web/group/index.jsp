<%@ page import="ch.avocado.share.controller.TemplateType" %>
<%@ page import="ch.avocado.share.controller.UserSession" %>
<%@ page import="ch.avocado.share.service.Mock.ServiceLocatorModifier" %>
<%@ page import="ch.avocado.share.service.IUserDataHandler" %>
<%@ page import="ch.avocado.share.service.Mock.UserDataHandlerMock" %>
<%@ page import="ch.avocado.share.common.ServiceLocator" %>
<%@ page import="ch.avocado.share.service.Mock.SecurityHandlerMock" %>
<%@ page import="ch.avocado.share.service.IGroupDataHandler" %>
<%@ page import="ch.avocado.share.service.Mock.GroupDataHandlerMock" %>
<%@ page import="ch.avocado.share.service.ISecurityHandler" %>
<%@ page import="ch.avocado.share.model.data.AccessLevelEnum" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:useBean id="groupBean" class="ch.avocado.share.controller.GroupBean" />
<jsp:setProperty name="groupBean" property="*" />
<%
    UserSession userSession = new UserSession(request);
    SecurityHandlerMock securityHandler = new SecurityHandlerMock();
    ServiceLocatorModifier.setService(IUserDataHandler.class, new UserDataHandlerMock());
    ServiceLocatorModifier.setService(IGroupDataHandler.class, new GroupDataHandlerMock());
    ServiceLocatorModifier.setService(ISecurityHandler.class, securityHandler);
    userSession.authenticate(securityHandler.getUserWithAccess(AccessLevelEnum.OWNER));
    groupBean.renderRequest(request, response);
%>
