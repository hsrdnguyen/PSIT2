<%@ page import="ch.avocado.share.model.data.User" %>
<%@ page import="ch.avocado.share.common.form.FormBuilder" %>
<%@ page import="java.util.HashMap" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF8" %>
<jsp:useBean id="registrationBean" class="ch.avocado.share.controller.UserHandlerBean"/>
<%
    if (request.getParameter("prename") != null &&
            request.getParameter("surname") != null &&
            request.getParameter("id") != null &&
            request.getParameter("mail") != null) {
        registrationBean.setName(request.getParameter("prename"));
        registrationBean.setPrename(request.getParameter("surname"));
        registrationBean.setEmailAddress(request.getParameter("mail"));
        registrationBean.setId(request.getParameter("id"));

        registrationBean.updateUser();

        String redirectURL = request.getContextPath() + "/index.jsp";
        response.sendRedirect(redirectURL);
    } else {
%>
<%@include file="includes/header.jsp" %>
<%
    User user = userSession.getUser();
    if(user == null) {
        response.sendRedirect(request.getContextPath() + "/index.jsp");
    }
    FormBuilder form = new FormBuilder(user, User.class, new HashMap<String, String>());
    form.setReadableFieldName("prename", "Vorname *");
    form.setReadableFieldName("surname", "Nachname *");
    form.setReadableFieldName("password", "Passwort *");
    form.setReadableFieldName("mail", "E-Mail *");

%>

<h1>Benutzerdaten aktualisieren</h1>
<div class="text-contact">

    <div aria-expanded="true" class="collapse in" id="message-form">
        <p class="text-block">Die Felder mit * müssen ausgefüllt werden.</p>
        <div id="xform" class="xform">
            <%=form.getFormBegin("post") %>
                <div class="form-group" id="formular-vorname">
                    <%=form.getLabelFor("prename")%>
                    <%=form.getInputFor("prename")%>
                </div>
                <div class="form-group" id="formular-nachname">
                    <%=form.getLabelFor("surname")%>
                    <%=form.getInputFor("surname")%>
                </div>
                <div class="form-group form-email" id="formular-email">
                    <%=form.getLabelFor("mail")%>
                    <%=form.getInputFor("mail")%>
                </div>
                <%=form.getSubmit("Speichern") %>
            <%=form.getFormEnd()%>
        </div>
    </div>
</div>
<%@include file="includes/footer.jsp" %>
<%}%>
