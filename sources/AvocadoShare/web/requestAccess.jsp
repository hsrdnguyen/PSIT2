<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF8" %>
<jsp:useBean id="accessBean" class="ch.avocado.share.controller.FileAccessBean" />
<jsp:setProperty name="accessBean" property="fileId" />
<jsp:setProperty name="accessBean" property="requestingUserMail" />
<html>
<head>
    <title></title>
</head>
<body>
<% // TODO: include header %>
<% if(accessBean.requestAccess()) { %>
<div class="alert alert-success">
    Der Besitzer des Files wurde benachrichtigt.
</div>
<% } else { %>
<div class="alert alert-danger">
    Das Mail konnte leider nicht versendet werden.
</div>
<% } %>
<% // TODO: include footer %>
</body>
</html>
