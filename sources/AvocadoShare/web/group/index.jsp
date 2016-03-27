<%@ page import="ch.avocado.share.controller.UserSession" %>
<%@ page import="ch.avocado.share.service.Mock.ServiceLocatorModifier" %>
<%@ page import="ch.avocado.share.service.IUserDataHandler" %>
<%@ page import="ch.avocado.share.service.Mock.UserDataHandlerMock" %>
<%@ page import="ch.avocado.share.service.Mock.SecurityHandlerMock" %>
<%@ page import="ch.avocado.share.service.IGroupDataHandler" %>
<%@ page import="ch.avocado.share.service.Mock.GroupDataHandlerMock" %>
<%@ page import="ch.avocado.share.service.ISecurityHandler" %>
<%@ page import="ch.avocado.share.model.data.AccessLevelEnum" %>
<%@ page import="ch.avocado.share.controller.TemplateType" %>
<%@ page import="ch.avocado.share.model.data.Group" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:useBean id="groupBean" class="ch.avocado.share.controller.GroupBean" />
<jsp:useBean id="groupMemberBean" class="ch.avocado.share.controller.GroupMemberControlBean" />
<jsp:setProperty name="groupBean" property="*" />
<jsp:setProperty name="groupMemberBean" property="action" />
<jsp:setProperty name="groupMemberBean" property="groupId"/>
<jsp:setProperty name="groupMemberBean" property="userId"/>
<%
    UserSession userSession = new UserSession(request);
    SecurityHandlerMock securityHandler = new SecurityHandlerMock();
    ServiceLocatorModifier.setService(IUserDataHandler.class, new UserDataHandlerMock());
    ServiceLocatorModifier.setService(IGroupDataHandler.class, new GroupDataHandlerMock());
    ServiceLocatorModifier.setService(ISecurityHandler.class, securityHandler);
    userSession.authenticate(securityHandler.getUserWithAccess(AccessLevelEnum.OWNER));
    groupBean.renderRequest(request, response);
    if(response.getStatus() == 200 && groupBean.getRendererTemplateType() == TemplateType.DETAIL) {
        Group group = (Group) request.getAttribute("Group");
        groupMemberBean.setTarget(group);
        groupMemberBean.renderRequest(request, response);
    }
%>
