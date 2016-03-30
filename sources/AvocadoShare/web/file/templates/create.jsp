<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="ch.avocado.share.common.form.FormBuilder" %>
<%@ page import="ch.avocado.share.model.data.Module" %>
<%@ page import="ch.avocado.share.controller.FileBean" %>
<%@ page import="ch.avocado.share.model.data.File" %>
<%@ page import="java.util.Map" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    FormBuilder formBuilder = new FormBuilder((File) request.getAttribute("File"), File.class, (Map<String, String>) request.getAttribute(FileBean.ATTRIBUTE_FORM_ERRORS));
    formBuilder.setEncodingType("multipart/form-data");
    formBuilder.setReadableFieldName("title", "Titel");
    formBuilder.setReadableFieldName("description", "Beschreibung");
    formBuilder.setReadableFieldName("moduleId", "Modul auswählen");
    formBuilder.setReadableFieldName("file", "Datei auswählen");

    Module[] userModules = FileBean.getModuleForAccessingUser(request);
%>
<h2>Dateien erstellen</h2>
<%=formBuilder.getFormErrors() %>
<div>
    <p class="text-block">Es müssen alle Felder ausfüllt werden.</p>
    <div>
        <%= formBuilder.getFormBegin("post") %>
        <div class="form-group">
            <%=formBuilder.getLabelFor("moduleId") %>
            <%=formBuilder.getSelectFor("moduleId", userModules) %>
        </div>
        <div class="form-group">
            <%=formBuilder.getLabelFor("file")%>
            <input class="form-control" type="file" name="file" size="50"/>
        </div>
        <div class="form-group">
            <%=formBuilder.getLabelFor("title") %>
            <%=formBuilder.getInputFor("title") %>
        </div>

        <div class="form-group">
            <%=formBuilder.getLabelFor("description") %>
            <%=formBuilder.getInputFor("description", "textarea") %>
        </div>
        <%=formBuilder.getSubmit("Speichern")%>
        <%=formBuilder.getFormEnd() %>
    </div>
</div>