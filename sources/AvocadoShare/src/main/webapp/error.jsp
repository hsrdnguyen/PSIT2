<%@ page import="ch.avocado.share.common.HttpStatusCode" %>
<%@ page contentType="text/html;charset=UTF-8"
         pageEncoding="UTF-8"
         language="java"
         isErrorPage="true"
%>
<jsp:useBean id="errorBean" class="ch.avocado.share.controller.ErrorDisplayBean" />
<%@include file="includes/header.jsp" %>
<%
    errorBean.setRequest(request);
    errorBean.setResponse(response);
    if(exception != null) {
        errorBean.setException(exception);
    }
    String message = errorBean.getDescription();
    HttpStatusCode statusCode = errorBean.getStatusCode();
    exception = errorBean.getException();
%>
<div class="content-main">
    <div class="list-group">
        <div class="list-group-item list-group-header">
            <h2>
                <a title="<%=Encoder.forHtmlAttribute(statusCode.getMessage()) %>"><%=statusCode.getCode() %>
                </a> - Oh nein!
            </h2>
        </div>
        <div class="list-group-item">
            <p>
                <span class="text-muted">Während dem Bearbeiten ihrer Anfrage ist ein Fehler aufgetreten:</span> <br/>
                <%=Encoder.forHtml(message)%>
            </p>
        </div>
        <% if (exception != null) { %>
        <div class="list-group-item">
            <pre class="stacktrace">
                <%=Encoder.getStackTraceForHtml(exception) %>
            </pre>
        </div>
        <% } %>
    </div>
</div>
<%@include file="includes/footer.jsp" %>