<%@ page import="ch.avocado.share.controller.UserSession" %>
<%@ page import="ch.avocado.share.service.Mock.UserDataHandlerMock" %>
<%@ page import="ch.avocado.share.service.Mock.SecurityHandlerMock" %>
<%@ page import="ch.avocado.share.service.Mock.GroupDataHandlerMock" %>
<%@ page import="ch.avocado.share.service.ISecurityHandler" %>
<%@ page import="ch.avocado.share.model.data.AccessLevelEnum" %>
<%@ page import="ch.avocado.share.controller.TemplateType" %>
<%@ page import="ch.avocado.share.model.data.Group" %>
<%@ page import="ch.avocado.share.common.ServiceLocator" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:useBean id="groupBean" class="ch.avocado.share.controller.GroupBean" />
<jsp:useBean id="groupMemberBean" class="ch.avocado.share.controller.GroupMemberControlBean" />
<jsp:setProperty name="groupBean" property="*" />
<jsp:setProperty name="groupMemberBean" property="action" />
<jsp:setProperty name="groupMemberBean" property="groupId"/>
<jsp:setProperty name="groupMemberBean" property="userId"/>
<%
    GroupDataHandlerMock.use();
    UserDataHandlerMock.use();
    SecurityHandlerMock.use();
    SecurityHandlerMock securityHandlerMock = (SecurityHandlerMock ) ServiceLocator.getService(ISecurityHandler.class);
    UserSession userSession = new UserSession(request);
    userSession.authenticate(securityHandlerMock.getUserWithAccess(AccessLevelEnum.OWNER));


    groupBean.renderRequest(request, response);


    if(response.getStatus() == 200 && groupBean.getRendererTemplateType() == TemplateType.DETAIL) {
        Group group = (Group) request.getAttribute("Group");
        groupMemberBean.setTarget(group);
        groupMemberBean.setMethod("GET");
        groupMemberBean.renderRequest(request, response);
    }
%>
