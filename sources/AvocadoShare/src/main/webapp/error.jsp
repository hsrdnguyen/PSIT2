<%@ page import="ch.avocado.share.common.HttpStatusCode" %>
<%@ page import="ch.avocado.share.common.Encoder" %>
<%@ page contentType="text/html;charset=UTF-8"
         pageEncoding="UTF-8"
         language="java"
         isErrorPage="true"
%>
<%@include file="includes/header.jsp" %>
<%
    String message;
    if (exception != null) {
        message = exception.getMessage();
    } else {
        message = (String) request.getAttribute("javax.servlet.error.message");
        if (message == null) {
            message = "Unbekannter Fehler.";
        }
    }
    HttpStatusCode statusCode = HttpStatusCode.fromCode(response.getStatus());
    message = Encoder.forHtml(message);
    if (statusCode.getCode() == 200) {
        // If it wasn't an error we direct to the
        response.sendRedirect(baseUrl);
    }
%>
<h2>
    <a title="<%=Encoder.forHtmlAttribute(statusCode.getMessage()) %>"><%=statusCode.getCode() %>
    </a> - Oh nein!
</h2>
<div>
    <p>
        <span class="text-muted">Während dem Bearbeiten ihrer Anfrage ist ein Fehler aufgetreten:</span> <br/>
        <%=message%>
    </p>
</div>
<%@include file="includes/footer.jsp" %>