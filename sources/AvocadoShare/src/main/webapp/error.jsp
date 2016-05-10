<%@ page import="ch.avocado.share.common.HttpStatusCode" %>
<%@ page import="ch.avocado.share.common.ResponseHelper" %>
<%@ page import="java.util.Enumeration" %>
<%@ page contentType="text/html;charset=UTF-8"
         pageEncoding="UTF-8"
         language="java"
         isErrorPage="true"
%>
<%@include file="includes/header.jsp" %>
<%

    Enumeration<String> names = request.getAttributeNames();
    while (names.hasMoreElements()) {
        System.out.println(names.nextElement());
    }
    System.out.println("Exception: " + exception + " - " + (exception == null));
    String message;
    if (exception != null) {
        message = exception.getMessage();
    } else {
        message = (String) request.getAttribute("javax.servlet.error.message");
        if (message == null || message.isEmpty()) {
            message = "Unbekannter Fehler.";
        }
    }
    HttpStatusCode statusCode = HttpStatusCode.fromCode(response.getStatus());
    message = Encoder.forHtml(message);
    if (statusCode.getCode() == 200) {
        // If it wasn't an error we direct to the
        response.sendRedirect(baseUrl);
    }
    Throwable avocadoExcpetion = (Throwable) request.getAttribute(ResponseHelper.EXCEPTION_ATTRIBUTE);
    if(avocadoExcpetion != null) {
        exception = avocadoExcpetion;
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
    <% if (exception != null) { %>
    <pre>
        <%
            exception.printStackTrace(new java.io.PrintWriter(out));
        %>
    </pre>
    <% } %>
</div>
<%@include file="includes/footer.jsp" %>