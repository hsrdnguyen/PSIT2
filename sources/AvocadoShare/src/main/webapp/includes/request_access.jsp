<%@ page import="ch.avocado.share.model.data.AccessControlObjectBase" %>
<%@ page import="ch.avocado.share.model.data.User" %>
<%@include file="header.jsp"%>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%
    AccessControlObjectBase object = (AccessControlObjectBase) request.getAttribute("object");
    User owner = (User) request.getAttribute("owner");
    String type = (String) request.getAttribute("type");
    boolean success = request.getAttribute("success") != null;
%>
<div class="content-main">
    <div class="list-group">
        <div class="list-group-item list-group-header">
            <h2>Zugriff beantragen</h2>
        </div>
        <% if(success) { %>
        <div class="list-group-item list-group-item-success">
            <strong>Erfolg!</strong> Der Besitzer wurde benachrichtig.
        </div>
        <% } else { %>
        <div class="list-group-item">
            Mit der foldenden Schaltfläche können Sie den Besitzer (<%=Encoder.forHtml(owner.getFullName())%>)
            um Leserecht für "<%=Encoder.forHtml(object.getReadableName())%>" bitten.

            <form method="post">
                <input type="hidden" name="id" value="<%=Encoder.forHtmlAttribute(object.getId())%>"/>
                <input type="hidden" name="type" value="<%=Encoder.forHtmlAttribute(type)%>"/>
                <input type="submit" class="btn btn-primary" value="Beantragen" />
            </form>
        </div>
        <% } %>
    </div>
</div>
<div class="content-right">
    <div class="list-group">
        <div class="list-group-item list-group-header">
            <h3><span class="mega-octicon octicon-info"></span> Hinweis</h3>
        </div>
        <div class="list-group-item">
            Der Besitzer wird in einem E-Mail darüber benachrichtig, dass Sie den Zugriff angefragt haben und
            kann diese Anfrage dann bestätigen.
        </div>
    </div>
</div>
<%@include file="footer.jsp"%>