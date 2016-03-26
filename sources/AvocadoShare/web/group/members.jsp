<%@ page import="ch.avocado.share.controller.UserSession" %>
<%@ page import="ch.avocado.share.service.Mock.SecurityHandlerMock" %>
<%@ page import="ch.avocado.share.service.Mock.ServiceLocatorModifier" %>
<%@ page import="ch.avocado.share.service.IUserDataHandler" %>
<%@ page import="ch.avocado.share.service.IGroupDataHandler" %>
<%@ page import="ch.avocado.share.service.ISecurityHandler" %>
<%@ page import="ch.avocado.share.model.data.AccessLevelEnum" %>
<%@ page import="ch.avocado.share.service.Mock.UserDataHandlerMock" %>
<%@ page import="ch.avocado.share.service.Mock.GroupDataHandlerMock" %>
<%@ page import="ch.avocado.share.common.Encoder" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:useBean id="members" class="ch.avocado.share.controller.GroupMemberControlBean" />
<jsp:setProperty name="members" property="*" />
<% // Display, change members %>
<%
    UserSession userSession = new UserSession(request);
    SecurityHandlerMock securityHandler = new SecurityHandlerMock();
    ServiceLocatorModifier.setService(IUserDataHandler.class, new UserDataHandlerMock());
    ServiceLocatorModifier.setService(IGroupDataHandler.class, new GroupDataHandlerMock());
    ServiceLocatorModifier.setService(ISecurityHandler.class, securityHandler);
    userSession.authenticate(securityHandler.getUserWithAccess(AccessLevelEnum.OWNER));
    members.executeRequest(request, response);
    if(response.getStatus() == HttpServletResponse.SC_OK) {
        response.sendRedirect("?id=" + Encoder.forUrl(members.getTargetId()));
    }
%>