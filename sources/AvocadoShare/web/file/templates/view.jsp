<%@ page import="ch.avocado.share.model.data.File" %>
<%@ page import="ch.avocado.share.common.Encoder" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<% File file = (File) request.getAttribute("File"); %>
<h2><%=Encoder.forHtml(file.getTitle())%></h2>
<a href="/download?id=<%=Encoder.forUrlAttribute(file.getId())%>" target="_blank">Download</a>
