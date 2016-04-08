<%@ page import="ch.avocado.share.common.form.FormBuilder" %>
<%@ page import="ch.avocado.share.model.data.User" %>
<%@ page import="ch.avocado.share.controller.UserBean" %>
<%@ page import="java.util.Map" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF8" %>
<%@include file="../../includes/header.jsp" %>
<%
    FormBuilder form = new FormBuilder((User) request.getAttribute("User"), User.class, (Map<String, String>) request.getAttribute(UserBean.ATTRIBUTE_FORM_ERRORS));
    form.setReadableFieldName("prename", "Vorname");
    form.setReadableFieldName("surname", "Nachname");
    form.setReadableFieldName("password", "Passwort");
    form.setReadableFieldName("passwordConfirmation", "Passwort wiederholen");
    form.setReadableFieldName("mail", "E-Mail *");
%>
<h1>Registrierung</h1>
<% if(!form.getFormErrors().isEmpty()) { %>
    <div class="alert alert-danger">
        <%=form.getFormErrors()%>
    </div>
<% } %>
<div class="text-contact">
    <p class="text-block">Die Felder mit * müssen ausgefüllt werden.</p>
    <div id="xform" class="xform">
        <%=form.getFormBegin("post") %>
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
        <div class="form-group" id="formular-betreff">
            <%=form.getLabelFor("password")%>
            <%=form.getInputFor("password")%>
        </div>
        <div class="form-group" id="formular-betreff">
            <%=form.getLabelFor("passwordConfirmation") %>
            <%=form.getInputFor("passwordConfirmation", "password", "") %>
        </div>
        <div class="form-group" id="formular-msg">
            <label class="control-label" for="formular-field-8">
                Profilbild</label>
            <div class="form-group">
                <button class="btn btn-primary" type="submit" name="submit" id="formular-field-8">
                    Datei auswählen...
                </button>
                <input name="send" value="1" type="hidden">
                <button class="btn btn-primary" type="submit" name="submit" id="formular-field-8">
                    Hochladen
                </button>
                <input name="send" value="1" type="hidden">
            </div>
        </div>
        <%=form.getSubmit("Registrieren") %>
        </form>
    </div>
</div>
<%@include file="../../includes/footer.jsp" %>
