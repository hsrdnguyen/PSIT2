<%@ page import="ch.avocado.share.model.data.File" %>
<%@ page import="ch.avocado.share.common.Encoder" %><%--
  Created by IntelliJ IDEA.
  User: coffeemakr
  Date: 29.03.16
  Time: 09:24
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<% File[] files = (File[]) request.getAttribute("Files"); %>
<h2>Meine Dateien</h2>
<%
    for(File file: files) {
        String title = Encoder.forHtml(file.getTitle());
%>
    <h3><%=title %></h3>
<% } %>