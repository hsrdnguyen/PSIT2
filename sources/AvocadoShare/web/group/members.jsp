<%@ page import="ch.avocado.share.controller.UserSession" %>
<%@ page import="ch.avocado.share.service.Mock.SecurityHandlerMock" %>
<%@ page import="ch.avocado.share.service.ISecurityHandler" %>
<%@ page import="ch.avocado.share.model.data.AccessLevelEnum" %>
<%@ page import="ch.avocado.share.service.Mock.UserDataHandlerMock" %>
<%@ page import="ch.avocado.share.service.Mock.GroupDataHandlerMock" %>
<%@ page import="ch.avocado.share.common.Encoder" %>
<%@ page import="ch.avocado.share.common.ServiceLocator" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:useBean id="members" class="ch.avocado.share.controller.GroupMemberControlBean" />
<jsp:setProperty name="members" property="*" />
<%
    GroupDataHandlerMock.use();
    UserDataHandlerMock.use();
    SecurityHandlerMock.use();
    SecurityHandlerMock securityHandlerMock = (SecurityHandlerMock ) ServiceLocator.getService(ISecurityHandler.class);
    UserSession userSession = new UserSession(request);
    userSession.authenticate(securityHandlerMock.getUserWithAccess(AccessLevelEnum.OWNER));

    members.executeRequest(request, response);
    if(response.getStatus() == HttpServletResponse.SC_OK) {
        // redirect to group view
        response.sendRedirect("?id=" + Encoder.forUrl(members.getTargetId()));
    }
%>