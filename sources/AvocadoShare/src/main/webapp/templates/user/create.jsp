<%@ page import="ch.avocado.share.common.form.FormBuilder" %>
<%@ page import="ch.avocado.share.controller.UserSession" %>
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
    form.setReadableFieldName("mail", "E-Mail *");
    boolean alreadyLoggedIn = new UserSession(request).isAuthenticated();
%>
<h1>Registrierung</h1>
<% if(alreadyLoggedIn) { %>
    <div class="alert alert-info">
        Sie sind bereits angemeldet.
    </div>
    <script type="application/javascript">
        var currentPaths = document.location.href.split("/");
        document.location.href = currentPaths.slice(0, currentPaths.length - 1).join("/");
    </script>
<% } else { %>
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
        <%--
        <div class="form-group" id="formular-msg">
            <label class="control-label" for="formular-field-8">
                Profilbild</label>
            <div class="form-group">
                <button class="btn btn-primary" type="submit" name="submit" id="formular-field-8">
                    Datei auswählen...
                </button>
                <input name="send" value="1" type="hidden">
                <button class="btn btn-primary" type="submit" name="submit" id="formular-field-9">
                    Hochladen
                </button>
                <input name="send" value="1" type="hidden">
            </div>
        </div>
        --%>
        <%=form.getSubmit("Registrieren") %>
        </form>
    </div>
</div>
<% } %>