<%@ page import="ch.avocado.share.controller.TemplateType" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:useBean id="groupBean" class="ch.avocado.share.controller.GroupBean" />
<%
groupBean.handleRequest(request, response);
%>