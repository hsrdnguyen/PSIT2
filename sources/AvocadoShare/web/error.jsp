<%@ page import="ch.avocado.share.common.HttpStatusCode" %>
<%@ page import="ch.avocado.share.common.Encoder" %>
<%@ page contentType="text/html;charset=UTF-8"
         pageEncoding="UTF-8"
         language="java"
         isErrorPage="true"
%>
<!DOCTYPE html>
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
    String baseUrl = request.getServletContext().getContextPath();
    // TODO: try to import header and footer
%>
<html>
<head>
    <title>Error <%=statusCode.getCode()%></title>
    <meta charset="UTF-8">
    <link rel="stylesheet" href="<%=baseUrl%>/components/bootstrap/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="<%=baseUrl%>/css/app.css">
</head>
<main>
    <div class="container">
    <h2>
        <a title="<%=Encoder.forHtmlAttribute(statusCode.getMessage()) %>"><%=statusCode.getCode() %></a> - Oh nein!
    </h2>
    <div>
        <p>Ihre Anfrage konnte leider nicht bearbeitet werden.</p>
        <p><%=message%>
        </p>
    </div>
    </div>
</main>
</html>