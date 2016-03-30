<%@ page import="ch.avocado.share.model.data.File" %>
<%@ page import="ch.avocado.share.common.Encoder" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    File file = (File) request.getAttribute("File");
    String id = Encoder.forUrlAttribute(file.getId());
%>
<h2><%=Encoder.forHtml(file.getTitle())%></h2>

<a class="btn btn-primary" href="?action=edit&id=<%=id %>">Edit</a>
<a class="btn btn-primary" href="/download?id=<%=id %>" target="_blank">Download</a>
