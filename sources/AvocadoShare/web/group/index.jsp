<%@ page import="ch.avocado.share.controller.TemplateType" %>
<%@ page import="ch.avocado.share.model.data.Group" %>
<jsp:useBean id="groupBean" class="ch.avocado.share.controller.GroupBean" />
<jsp:useBean id="groupMemberBean" class="ch.avocado.share.controller.GroupMemberControlBean" />
<jsp:setProperty name="groupBean" property="*" />
<jsp:setProperty name="groupMemberBean" property="action" />
<jsp:setProperty name="groupMemberBean" property="groupId"/>
<jsp:setProperty name="groupMemberBean" property="userId"/>
<%
    request.getRequestDispatcher("../includes/header.jsp").include(request, response);

    groupBean.renderRequest(request, response);


    if(response.getStatus() == 200 && groupBean.getRendererTemplateType() == TemplateType.DETAIL) {
        Group group = (Group) request.getAttribute("Group");
        groupMemberBean.setTarget(group);
        groupMemberBean.setMethod("GET");
        groupMemberBean.renderRequest(request, response);
    }
    request.getRequestDispatcher("../includes/footer.jsp").include(request, response);
%>