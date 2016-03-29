<%@ page import="ch.avocado.share.common.form.FormBuilder" %>
<%@ page import="ch.avocado.share.model.data.File" %>
<%@ page import="ch.avocado.share.controller.FileUploadBean" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="ch.avocado.share.model.data.Module" %>
<%@ page import="ch.avocado.share.service.Mock.SecurityHandlerMock" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    SecurityHandlerMock.use();

    FormBuilder formBuilder = new FormBuilder("", (File) request.getAttribute("File"), File.class, (HashMap<String, String>) request.getAttribute(FileUploadBean.ATTRIBUTE_FORM_ERRORS));
    formBuilder.setEncodingType("multipart/form-data");
    formBuilder.setReadableFieldName("title", "Titel *");
    formBuilder.setReadableFieldName("description", "Beschreibung *");
    formBuilder.setReadableFieldName("moduleId", "Modul auswählen");

    Module[] userModules = FileUploadBean.getModuleForAccessingUser(request);
%>
<h2>Dateien Hochladen</h2>
<%=formBuilder.getFormErrors() %>
<div style="collapsed-contact" aria-expanded="true" class="collapse in" id="message-form">
    <p class="text-block">Die Felder mit * müssen ausgefüllt werden.</p>
    <div id="xform" class="xform">
        <%= formBuilder.getFormBegin("post") %>
        <div class="form-group" id="formular-anrede">
            <%=formBuilder.getLabelFor("moduleId") %>
            <%=formBuilder.getSelectForModules("moduleId", userModules) %>
        </div>
        <input type="file" name="file" size="50"/>
        <div class="form-group" id="formular-title">
            <%=formBuilder.getLabelFor("title") %>
            <%=formBuilder.getInputFor("title") %>
        </div>

        <div class="form-group" id="formular-description">
            <%=formBuilder.getLabelFor("description") %>
            <%=formBuilder.getInputFor("description", "textarea") %>
        </div>
        <input class="btn-primary" type="submit" value="Hochladen"/>

        <%=formBuilder.getFormEnd() %>
    </div>
</div>