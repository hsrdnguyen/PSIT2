<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF8" %>
<jsp:useBean id="accessBean" class="ch.avocado.share.controller.FileAccessBean" />
<jsp:setProperty name="accessBean" property="fileId" />
<jsp:setProperty name="accessBean" property="ruserId" />
<jsp:setProperty name="accessBean" property="ouserId" />
<jsp:include page="includes/header.jsp" />
<% if(accessBean.grantAccess()) { %>
<div class="alert alert-success">
    Der User wurde berechtigt.
</div>
<% } else { %>
<div class="alert alert-danger">
    Der User konnte leider nicht berechtigt werden.
</div>
<% } %>
<jsp:include page="includes/footer.jsp" />