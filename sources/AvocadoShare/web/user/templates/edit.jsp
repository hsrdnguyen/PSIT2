<%@ page import="ch.avocado.share.common.form.FormBuilder" %>
<%@ page import="ch.avocado.share.model.data.User" %>
<%@ page import="ch.avocado.share.controller.UserBean" %>
<%@ page import="java.util.Map" %>
<%@include file="../../includes/header.jsp" %>
<%
    FormBuilder form = new FormBuilder((User) request.getAttribute("User"), User.class, (Map<String, String>) request.getAttribute(UserBean.ATTRIBUTE_FORM_ERRORS));
    form.setReadableFieldName("prename", "Vorname");
    form.setReadableFieldName("surname", "Nachname");
    form.setReadableFieldName("password", "Passwort");
    form.setReadableFieldName("passwordConfirmation", "Passwort wiederholen");
    form.setReadableFieldName("mail", "E-Mail-Adresse");
%>
<h1>Benutzer bearbeiten</h1>
<% if(!form.getFormErrors().isEmpty()) { %>
<div class="alert alert-danger">
    <%=form.getFormErrors()%>
</div>
<% } %>
<h2>Informationen</h2>

    <%=form.getFormBegin("patch") %>
    <div class="form-group" id="formular-prename">
        <%=form.getLabelFor("prename")%>
        <%=form.getInputFor("prename")%>
    </div>
    <div class="form-group" id="formular-surname">
        <%=form.getLabelFor("surname")%>
        <%=form.getInputFor("surname")%>
    </div>
    <div class="form-group" id="formular-email">
        <%=form.getLabelFor("mail")%>
        <%=form.getInputFor("mail")%>
    </div>
    <%=form.getSubmit("Speichern") %>
    <%=form.getFormEnd()%>

<h3>Passwort</h3>
    <%=form.getFormBegin("patch") %>
    <div class="form-group" id="formular-betreff">
        <%=form.getLabelFor("password")%>
        <%=form.getInputFor("password")%>
    </div>
    <div class="form-group" id="formular-betreff">
        <%=form.getLabelFor("passwordConfirmation") %>
        <%=form.getInputFor("passwordConfirmation", "password", "") %>
    </div>
    <%=form.getSubmit("Ändern")%>
    <%=form.getFormEnd() %>

<%@include file="../../includes/footer.jsp" %>
