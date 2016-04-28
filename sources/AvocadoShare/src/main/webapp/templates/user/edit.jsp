<%@ page import="ch.avocado.share.common.form.FormBuilder" %>
<%@ page import="ch.avocado.share.model.data.User" %>
<%@ page import="ch.avocado.share.servlet.resources.base.DetailViewConfig" %>
<%@ page import="ch.avocado.share.servlet.resources.base.HtmlRenderer" %>
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
<div class="list-group">
    <div class="list-group-item list-group-header">
        <h1>Benutzer bearbeiten</h1>
    </div>
    <div class="list-group-item">
        <% if (!form.getFormErrors().isEmpty()) { %>

        <div class="alert alert-danger">
            <%=form.getFormErrors()%>
        </div>
        <% } %>
        <h2>Informationen</h2>

        <%=form.getFormBegin("patch") %>
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

        <h3>Passwort</h3>
        <%=form.getFormBegin("patch") %>
        <div class="form-group">
            <%=form.getLabelFor("password")%>
            <%=form.getInputFor("password")%>
        </div>
        <div class="form-group">
            <%=form.getLabelFor("passwordConfirmation") %>
            <%=form.getInputFor("passwordConfirmation", "password", "") %>
        </div>
        <%=form.getSubmit("Ändern")%>
        <%=form.getFormEnd() %>

        <h3>Profilbild</h3>
        <% form.setEncodingType("multipart/form-data"); %>
        <%=form.getFormBegin("patch") %>
        <div class="form-group">
            <input type="file" name="avatar"/>
        </div>
        <%=form.getSubmit("Hochladen") %>
        <%=form.getFormEnd() %>

        <h3>Anderes</h3>
        <h4>Benutzer löschen</h4>
        <p>
            Sie können Ihrer Benutzer mit der folgenden Schaltfläche löschen.
        </p>
        <div class="alert alert-danger">
            Wenn Sie Ihren Benutzer löschen werden alle Gruppen, Module und Dateien, die Sie erstellt haben
            unwiderruflich
            gelöscht!
        </div>
    </div>
</div>
<%=form.getFormBegin("delete") %>
<%=form.getSubmit("Benutzer löschen")%>
<%=form.getFormEnd()%>
<%@include file="../../includes/footer.jsp" %>
