<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:useBean id="members" class="ch.avocado.share.controller.GroupMemberControlBean" />
<jsp:setProperty name="members" property="level" />
<jsp:setProperty name="members" property="ownerId" />
<jsp:setProperty name="members" property="targetId" />
<%
    members.executeRequest(request, response);
%>
