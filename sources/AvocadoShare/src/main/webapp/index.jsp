<%@ page import="ch.avocado.share.controller.UserSession" %>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%
  UserSession userSession = new UserSession(request);
  if(userSession.isAuthenticated()) {
%>
<jsp:include page="includes/portfolio.jsp" />
<%
  } else {
%>
<jsp:include page="includes/welcome.jsp" />
<%
  }
%>