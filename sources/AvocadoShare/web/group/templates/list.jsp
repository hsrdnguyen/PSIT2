<%@ page import="ch.avocado.share.model.data.Group" %>
<%@ page import="ch.avocado.share.common.Encoder" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<% Group[] groups = (Group[]) request.getAttribute("Groups"); %>
<h1>List of groups</h1>
<% for(Group group: groups) { %>
    <div>
        Gruppe <%=Encoder.forHtml(group.getName()) %>
    </div>
<% } // for groups %>
