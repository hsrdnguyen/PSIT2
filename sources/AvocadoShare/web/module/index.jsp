<%@ page import="ch.avocado.share.controller.TemplateType" %>
<%@ page import="ch.avocado.share.model.data.Module" %>
<jsp:useBean id="moduleBean" class="ch.avocado.share.controller.ModuleBean" />
<jsp:useBean id="memberBean" class="ch.avocado.share.controller.ModuleMemberControlBean" />
<jsp:setProperty name="moduleBean" property="*" />
<%
    moduleBean.renderRequest(request, response);
    if(response.getStatus() == 200 && moduleBean.getRendererTemplateType() == TemplateType.DETAIL) {
        Module module = (Module) request.getAttribute("Group");
        memberBean.setTarget(module);
        memberBean.setMethod("GET");
        memberBean.renderRequest(request, response);
%>