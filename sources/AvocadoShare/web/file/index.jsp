<%--
  Created by IntelliJ IDEA.
  User: coffeemakr
  Date: 29.03.16
  Time: 08:05
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:useBean id="fileBean" class="ch.avocado.share.controller.FileUploadBean"/>
<jsp:setProperty name="fileBean" property="id"/>
<jsp:setProperty name="fileBean" property="moduleId"/>
<jsp:setProperty name="fileBean" property="description" />
<jsp:setProperty name="fileBean" property="title" />

<html>
<head>
    <title>File</title>
</head>
<body>
<%
    fileBean.renderRequest(request, response);
%>
</body>
</html>
