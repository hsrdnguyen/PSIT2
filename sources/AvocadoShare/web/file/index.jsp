<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:useBean id="fileBean" class="ch.avocado.share.controller.FileUploadBean"/>
<jsp:setProperty name="fileBean" property="id"/>
<jsp:setProperty name="fileBean" property="moduleId"/>
<jsp:setProperty name="fileBean" property="description" />
<jsp:setProperty name="fileBean" property="title" />
<jsp:setProperty name="fileBean" property="method" />
<jsp:setProperty name="fileBean" property="action" />
<%
    request.getRequestDispatcher("../includes/header.jsp").include(request, response);
    fileBean.renderRequest(request, response);
    request.getRequestDispatcher("../includes/footer.jsp").include(request, response);
%>
