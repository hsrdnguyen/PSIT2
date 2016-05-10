<%@ page import="ch.avocado.share.common.form.FormBuilder" %>
<%@ page import="ch.avocado.share.model.data.User" %>
<%@ page import="ch.avocado.share.servlet.resources.base.DetailViewConfig" %>
<%@ page import="ch.avocado.share.servlet.resources.base.HtmlRenderer" %>
<%@ page import="ch.avocado.share.common.form.InputType" %>
<%@ page import="ch.avocado.share.common.form.FormEncoding" %>
<%@ page import="ch.avocado.share.common.HttpMethod" %>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%
    DetailViewConfig viewConfig = (DetailViewConfig) request.getAttribute(HtmlRenderer.ATTRIBUTE_DETAIL_VIEW_CONFIG);
    User user = viewConfig.getObject(User.class);
    FormBuilder form = new FormBuilder(viewConfig, User.class);
    form.setReadableFieldName("prename", "Vorname");
    form.setReadableFieldName("surname", "Nachname");
    form.setReadableFieldName("password", "Passwort");
    form.setReadableFieldName("passwordConfirmation", "Passwort wiederholen");
    form.setReadableFieldName("mail", "E-Mail-Adresse");
%>
<div class="content-main">
    <div class="list-group">
        <div class="list-group-item list-group-header">
            <h2>Benutzer bearbeiten</h2>
        </div>
        <% if (!form.getFormErrors().isEmpty()) { %>
        <div class="list-group-item list-group-item-danger">
            <%=form.getFormErrors()%>
        </div>
        <% } %>

        <div class="list-group-item">
            <div class="list-group-item-heading">
                <h3>Informationen</h3>
            </div>
            <%=form.getFormBegin(HttpMethod.PATCH) %>
            <div class="form-group">
                <%=form.getLabelFor("prename")%>
                <%=form.getInputFor("prename")%>
            </div>
            <div class="form-group">
                <%=form.getLabelFor("surname")%>
                <%=form.getInputFor("surname")%>
            </div>
            <div class="form-group">
                <%=form.getLabelFor("mail")%>
                <%=form.getInputFor("mail")%>
            </div>
            <%=form.getSubmit("Speichern") %>
            <%=form.getFormEnd()%>
        </div>
        <div class="list-group-item">
            <div class="list-group-item-heading">
                <h3>Passwort</h3>
            </div>
            <%=form.getFormBegin(HttpMethod.PATCH) %>
            <div class="form-group">
                <%=form.getLabelFor("password")%>
                <%=form.getInputFor("password")%>
            </div>
            <div class="form-group">
                <%=form.getLabelFor("passwordConfirmation") %>
                <%=form.getInputFor("passwordConfirmation", InputType.PASSWORD) %>
            </div>
            <%=form.getSubmit("Ändern")%>
            <%=form.getFormEnd() %>
        </div>
        <div class="list-group-item">
            <div class="list-group-item-heading">
                <h3>Profilbild</h3>
            </div>
            <% form.setEncodingType(FormEncoding.MULTIPART); %>
            <%=form.getFormBegin(HttpMethod.PATCH) %>
            <div class="form-group">
                <input type="file" name="avatar"/>
            </div>
            <%=form.getSubmit("Hochladen") %>
            <%=form.getFormEnd() %>
        </div>
    </div>
    <div class="list-group">
        <div class="list-group-item list-group-header">
            <h4>Benutzer löschen</h4>
        </div>
        <div class="list-group-item">
            Sie können Ihrer Benutzer mit der folgenden Schaltfläche löschen.
        </div>
        <div class="list-group-item list-group-item-danger">
            Wenn Sie Ihren Benutzer löschen werden alle Gruppen, Module und Dateien, die Sie erstellt haben
            unwiderruflich
            gelöscht!
        </div>
        <div class="list-group-item">
            <% form.setEncodingType(FormEncoding.URLENCODED); %>
            <%=form.getFormBegin(HttpMethod.PATCH) %>
            <%=form.getSubmit("Benutzer löschen", "btn-danger")%>
            <%=form.getFormEnd()%>
        </div>
    </div>
</div>
<%@include file="../../includes/footer.jsp" %>
