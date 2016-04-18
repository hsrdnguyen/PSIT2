<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF8" %>
<jsp:useBean id="userBean" class="ch.avocado.share.controller.UserBean"
/><jsp:setProperty name="userBean" property="*"
/><%
    userBean.renderRequest(request, response);
%>