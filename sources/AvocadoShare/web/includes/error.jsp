<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF8" %>
<%@ page import="ch.avocado.share.common.Encoder" %>
<%@ page import="ch.avocado.share.common.HttpStatusCode" %>
<%
    Integer status = (Integer) request.getAttribute("ErrorStatus");
    String message = (String) request.getAttribute("ErrorMessage");
    HttpStatusCode statusCode = HttpStatusCode.fromCode(status);
    if(status == null) {
        status = 0;
    }
%>
<h2><a title="<%=Encoder.forHtmlAttribute(statusCode.getMessage()) %>"><%=status %></a> - Oh nein!</h2>
<div>
    <p>Ihre Anfrage konnte leider nicht bearbeitet werden.</p>
    <p><%=Encoder.forHtml(message)%></p>
</div>