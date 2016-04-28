<%@ page import="ch.avocado.share.common.form.FormBuilder" %>
<%@ page import="ch.avocado.share.controller.UserSession" %>
<%@ page import="ch.avocado.share.model.data.User" %>
<%@ page import="ch.avocado.share.servlet.resources.base.DetailViewConfig" %>
<%@ page import="ch.avocado.share.servlet.resources.base.HtmlRenderer" %>
<%@ page import="ch.avocado.share.common.form.InputType" %>
<%@ page import="ch.avocado.share.common.form.FormEncoding" %>
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
    form.setReadableFieldName("avatar", "Profilbild");
    form.setEncodingType(FormEncoding.MULTIPART);
    boolean alreadyLoggedIn = new UserSession(request).isAuthenticated();
%>
<div class="content-main">
    <div class="list-group-item">
        <h1>Registrierung</h1>
    </div>

    <% if (alreadyLoggedIn) { %>
    <div class="alert alert-info">
        Sie sind bereits angemeldet.
    </div>
    <script type="application/javascript">
        var currentPaths = document.location.href.split("/");
        document.location.href = currentPaths.slice(0, currentPaths.length - 1).join("/");
    </script>
    <% } else { %>
    <% if (!form.getFormErrors().isEmpty()) { %>
    <div class="list-group-item list-group-item-danger">
        <%=form.getFormErrors()%>
    </div>
    <% } %>
    <div class="list-group-item">
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
            <div class="form-group">
                <%=form.getLabelFor("password")%>
                <%=form.getInputFor("password")%>
            </div>
            <div class="form-group">
                <%=form.getLabelFor("passwordConfirmation") %>
                <%=form.getInputFor("passwordConfirmation", InputType.PASSWORD) %>
            </div>
            <div class="form-group">
                <%=form.getLabelFor("avatar")%>
                <%=form.getInputFor("avatar", InputType.FILE)%>
            </div>
            <%=form.getSubmit("Registrieren") %>
            </form>
        </div>
    </div>
</div>
<% } %>