<%@ page import="ch.avocado.share.common.form.FormBuilder" %>
<%@ page import="ch.avocado.share.model.data.User" %>
<%@ page import="java.util.HashMap" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF8" %>
<jsp:useBean id="registrationBean" class="ch.avocado.share.controller.UserHandlerBean"/>
<%@include file="includes/header.jsp" %>
<%
    if(userSession.isAuthenticated()) {
        response.sendRedirect(baseUrl);
    }else if (request.getParameter("prename") != null &&
            request.getParameter("surname") != null &&
            request.getParameter("email") != null &&
            request.getParameter("password") != null) {
        registrationBean.setName(request.getParameter("surname"));
        registrationBean.setPrename(request.getParameter("prename"));
        registrationBean.setEmailAddress(request.getParameter("email"));
        registrationBean.setPassword(request.getParameter("password"));

        registrationBean.addUser();
        response.sendRedirect(baseUrl);
    } else {
        FormBuilder form = new FormBuilder(User.class, new HashMap<String, String>());
        form.setReadableFieldName("prename", "Vorname *");
        form.setReadableFieldName("surname", "Nachname *");
        form.setReadableFieldName("password", "Passwort *");
        form.setReadableFieldName("password_confirmation", "Passwort wiederholen *");
        form.setReadableFieldName("mail", "E-Mail *");
%>
<h1>Registrierung</h1>
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
            <%=form.getLabelFor("password_confirmation") %>
            <%=form.getInputFor("password_confirmation", "password", "") %>
        </div>
        <div class="form-group" id="formular-msg">
            <label class="control-label" for="formular-field-8">
                Profilbild</label>
            <div class="form-group" id="formular-msg">
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
<%@include file="includes/footer.jsp" %>
<%}%>