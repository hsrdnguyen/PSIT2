<%@ page import="ch.avocado.share.controller.UserSession" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:useBean id="group" class="ch.avocado.share.controller.GroupBean"/>
<jsp:setProperty name="group" property="name"/>
<jsp:setProperty name="group" property="description"/>
<html>
<head>
    <title>Create Group</title>
</head>
<body>
<%
    UserSession userSession = new UserSession(request);
    if (!userSession.isAuthenticated()) {
        response.sendError(response.SC_FORBIDDEN);
    } else {
        group.setOwner(userSession.getUser());
        if (request.getMethod().equals("POST")) {
            if (group.create()) {%>
                <div class="alert alert-success">Gruppe erstellt.</div><%
            } else {
                %><div class="alert alert-danger"><strong>Gruppe konnte nicht erstellt werden</strong><%=group.getErrorMessage()%></div><%
            }
        }
    }
%>
<h1>Gruppe erstellen</h1>
<form method="post">
    <div class="form-group">
        <label for="groupName">Name</label>
        <input id="groupName" class="input-group" type="text" name="name"/>
    </div>
    <div class="form-group">
        <label for="groupDescription">Beschreibung</label>
        <textarea id="groupDescription" class="input-group" name="description"></textarea>
    </div>
    <div class="form-control">
        <input type="submit" value="Gruppe erstellen" />
    </div>
</form>
</body>
</html>
