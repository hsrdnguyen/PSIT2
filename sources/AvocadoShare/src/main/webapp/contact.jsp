<%@ page import="ch.avocado.share.model.data.User" %>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@include file="includes/header.jsp" %>
<%
    User user = userSession.getUser();
    String prename = "", surname = "", email = "";
    if (user != null) {
        prename = Encoder.forHtmlAttribute(user.getPrename());
        surname = Encoder.forHtmlAttribute(user.getSurname());
        email = Encoder.forHtmlAttribute(user.getMail().getAddress());
    }
%>
<h1>Kontakt</h1>
<div class="text-contact">
    <p>Bei Fragen kontaktieren Sie uns</p>
    <h2>Senden Sie uns eine Nachricht</h2>
    <div id="message-form">
        <p class="text-block">Die Felder mit * müssen ausgefüllt werden.</p>
        <div id="xform" class="xform">
            <form action="" method="post" id="form_formular" class="contact_action">

                <div class="form-group" id="formular-anrede">
                    <label class="control-label" for="contact-anrede">Anrede</label>
                    <select class="form-control" id="contact-anrede" name="anrede">
                        <option value="Frau">Frau</option>
                        <option value="Herr">Herr</option>
                    </select>
                </div>
                <div class="form-group" id="formular-vorname">
                    <label class="control-label" for="contact-vorname">Vorname *</label>
                    <input required class="form-control" name="vorname" id="contact-vorname" value="<%=prename %>" type="text">
                </div>
                <div class="form-group" id="formular-nachname">
                    <label class="control-label" for="contact-nachname">Nachname *</label>
                    <input required class="form-control" name="nachname" id="contact-nachname" value="<%=surname %>" type="text">
                </div>
                <div class="form-group form-email" id="formular-email">
                    <label class="control-label" for="formular-field-5">E-mail *</label>
                    <input required class="form-control" name="email" id="formular-field-5" value="<%=email %>" type="email">
                </div>
                <div class="form-group" id="formular-betreff">
                    <label class="control-label" for="formular-field-6">Betreff *</label>
                    <input required class="form-control" name="betreff" id="formular-field-6" type="text">
                </div>
                <div class="form-group" id="formular-msg">
                    <label class="control-label" for="formular-field-7">Nachricht *</label>
                    <textarea required class="form-control" name="msg" id="formular-field-7" rows="10"></textarea>
                </div>
                <button class="btn btn-primary" type="submit" name="submit" id="formular-field-8">Senden</button>
            </form>
        </div>
    </div>
</div>
<%@include file="includes/footer.jsp" %>