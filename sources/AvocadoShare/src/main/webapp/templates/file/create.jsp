<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="ch.avocado.share.common.form.FormBuilder" %>
<%@ page import="ch.avocado.share.controller.FileBean" %>
<%@ page import="ch.avocado.share.controller.UserSession" %>
<%@ page import="ch.avocado.share.model.data.File" %>
<%@ page import="ch.avocado.share.model.data.Module" %>
<%@ page import="ch.avocado.share.model.exceptions.HttpBeanException" %>
<%@ page import="ch.avocado.share.servlet.resources.base.DetailViewConfig" %>
<%@ page import="ch.avocado.share.servlet.resources.base.HtmlRenderer" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%
    String parameterModuleId = request.getParameter("m");

    DetailViewConfig viewConfig = (DetailViewConfig) request.getAttribute(HtmlRenderer.ATTRIBUTE_DETAIL_VIEW_CONFIG);
    FormBuilder formBuilder = new FormBuilder(viewConfig, File.class);
    formBuilder.setEncodingType("multipart/form-data");
    formBuilder.setReadableFieldName("title", "Titel");
    formBuilder.setReadableFieldName("description", "Beschreibung");
    formBuilder.setReadableFieldName("moduleId", "Modul auswählen");
    formBuilder.setReadableFieldName("fileItem", "Datei auswählen");

    UserSession userSession = new UserSession(request);
    List<Module> userModules = new ArrayList<Module>();
    try {
        userModules = FileBean.getModulesToUpload(userSession.getUser());
    } catch (HttpBeanException e) {
        response.sendError(e.getStatusCode(), e.getDescription());
    }

%>
<div class="row">
    <div class="content-main">
        <div class="list-group">
            <div class="list-group-item list-group-header">
                <h2>Dateien erstellen</h2>
            </div>
            <div class="list-group-item">
                <%
                    if (userModules.isEmpty()) { %>
                <div class="alert alert-info">
                    <strong>Sie sind in keinem Modul.</strong>
                    Bevor Sie eine Datei hochladen können, müssen Sie Mitglied eines Modules sein.
                </div>

                <%
                } else {
                %>
                <%=formBuilder.getFormErrors() %>
                <div>
                    <p class="text-block">Es müssen alle Felder ausfüllt werden.</p>
                    <div>
                        <%= formBuilder.getFormBegin("post") %>
                        <div class="form-group">
                            <%=formBuilder.getLabelFor("moduleId") %>
                            <%=formBuilder.getSelectFor("moduleId", userModules, parameterModuleId) %>
                        </div>
                        <div class="form-group">
                            <%=formBuilder.getLabelFor("fileItem")%>
                            <input class="form-control" type="file" name="fileItem" size="50"/>
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
            </div>
        </div>
    </div>
    <div class="content-right">
        <div class="list-group-item list-group-header">
            <h3><span class="mega-octicon octicon-info"></span> Informationen</h3>
        </div>
        <div class="list-group-item">
            <p>Die Datei ist für alle Mitglieder im Modul ersichtlich.</p>
            <p>Alle Mitglieder, die Schreibrecht am Modul besitzen können die Datei bearbeiten.</p>
        </div>
    </div>
</div>
<%
    }
%>