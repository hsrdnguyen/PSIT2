<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="ch.avocado.share.common.form.FormBuilder" %>
<%@ page import="ch.avocado.share.model.data.Module" %>
<%@ page import="ch.avocado.share.controller.FileBean" %>
<%@ page import="ch.avocado.share.model.data.File" %>
<%@ page import="java.util.Map" %>
<%@ page import="ch.avocado.share.controller.UserSession" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    FormBuilder formBuilder = new FormBuilder((File) request.getAttribute("File"), File.class, (Map<String, String>) request.getAttribute(FileBean.ATTRIBUTE_FORM_ERRORS));
    formBuilder.setEncodingType("multipart/form-data");
    formBuilder.setReadableFieldName("title", "Titel");
    formBuilder.setReadableFieldName("description", "Beschreibung");
    formBuilder.setReadableFieldName("moduleId", "Modul auswählen");
    formBuilder.setReadableFieldName("file", "Datei auswählen");

    UserSession userSession = new UserSession(request);
    List<Module> userModules = FileBean.getModulesToUpload(userSession.getUser());
%>
<h2>Dateien bearbeiten</h2>
<%=formBuilder.getFormErrors() %>
<h3>Neue Datei hochladen</h3>
<div>
    <%= formBuilder.getFormBegin("patch") %>
        <div class="form-group row">
            <div class="col-sm-2">
                <%=formBuilder.getLabelFor("file")%>
            </div>
            <div class="col-sm-10">
                <input class="form-control" type="file" name="file" size="50"/>
            </div>
        </div>
    <p>
        Bitte beachten Sie, dass Sie die vorhandene Datei unwiderruflich gelöscht wird.
    </p>
    <%=formBuilder.getSubmit("Hochladen", "btn-danger")%>
    <%=formBuilder.getFormEnd() %>
</div>
<h3>Eigenschaften bearbeiten</h3>
<div>
    <%= formBuilder.getFormBegin("patch") %>
    <div class="form-group row">
        <div class="col-sm-2">
            <%=formBuilder.getLabelFor("moduleId") %>
        </div>
        <div class="col-sm-10">
        <%=formBuilder.getSelectFor("moduleId", userModules) %>
        </div>
    </div>
    <div class="form-group row">
        <div class="col-sm-2">
        <%=formBuilder.getLabelFor("title") %>
        </div>
        <div class="col-sm-10">
        <%=formBuilder.getInputFor("title") %>
        </div>
    </div>

    <div class="form-group row">
        <div class="col-sm-2">
        <%=formBuilder.getLabelFor("description") %>
        </div>
        <div class="col-sm-10">
        <%=formBuilder.getInputFor("description", "textarea") %>
        </div>
    </div>

    <div class="form-group row">
        <div class="col-sm-offset-2 col-sm-10">
            <%=formBuilder.getSubmit("Speichern")%>
        </div>
    </div>
    <%=formBuilder.getFormEnd() %>
</div>