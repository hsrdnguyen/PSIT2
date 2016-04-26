<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="ch.avocado.share.common.form.FormBuilder" %>
<%@ page import="ch.avocado.share.controller.FileBean" %>
<%@ page import="ch.avocado.share.controller.UserSession" %>
<%@ page import="ch.avocado.share.model.data.File" %>
<%@ page import="ch.avocado.share.model.data.Module" %>
<%@ page import="ch.avocado.share.servlet.resources.base.DetailViewConfig" %>
<%@ page import="ch.avocado.share.servlet.resources.base.HtmlRenderer" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%
    DetailViewConfig viewConfig = (DetailViewConfig) request.getAttribute(HtmlRenderer.ATTRIBUTE_DETAIL_VIEW_CONFIG);
    FormBuilder form = new FormBuilder(viewConfig, File.class);
    form.setEncodingType("multipart/form-data");
    form.setReadableFieldName("title", "Titel");
    form.setReadableFieldName("description", "Beschreibung");
    form.setReadableFieldName("moduleId", "Modul auswählen");
    form.setReadableFieldName("fileItem", "Datei auswählen");

    UserSession userSession = new UserSession(request);
    List<Module> userModules = FileBean.getModulesToUpload(userSession.getUser());
%>
<div class="row">
    <div class="content-main">
        <div class="list-group">
            <div class="list-group-item list-group-header">
                <h2>Dateien bearbeiten</h2>
            </div>
            <div class="list-group-item">
                <%=form.getFormErrors() %>
                <div class="list-group-item-heading">
                    <h3>Neue Datei hochladen</h3>
                </div>
                <%= form.getFormBegin("patch") %>
                <div class="form-group row">
                    <div class="col-sm-2">
                        <%=form.getLabelFor("fileItem")%>
                    </div>
                    <div class="col-sm-10">
                        <input class="form-control" type="file" name="fileItem" size="50"/>
                    </div>
                </div>
                <div class="row">
                    <div class="col-sm-push-2 col-sm-10">
                        <p>
                            Bitte beachten Sie, dass Sie die vorhandene Datei unwiderruflich gelöscht wird.
                        </p>
                        <%=form.getSubmit("Hochladen", "btn-danger")%>
                    </div>
                </div>
                <%=form.getFormEnd() %>
            </div>
            <div class="list-group-item">
                <h3>Eigenschaften bearbeiten</h3>
                <div>
                    <%= form.getFormBegin("patch") %>
                    <div class="form-group row">
                        <div class="col-sm-2">
                            <%=form.getLabelFor("moduleId") %>
                        </div>
                        <div class="col-sm-10">
                            <%=form.getSelectFor("moduleId", userModules) %>
                        </div>
                    </div>
                    <div class="form-group row">
                        <div class="col-sm-2">
                            <%=form.getLabelFor("title") %>
                        </div>
                        <div class="col-sm-10">
                            <%=form.getInputFor("title") %>
                        </div>
                    </div>

                    <div class="form-group row">
                        <div class="col-sm-2">
                            <%=form.getLabelFor("description") %>
                        </div>
                        <div class="col-sm-10">
                            <%=form.getInputFor("description", "textarea") %>
                        </div>
                    </div>

                    <div class="form-group row">
                        <div class="col-sm-offset-2 col-sm-10">
                            <%=form.getSubmit("Speichern")%>
                        </div>
                    </div>
                    <%=form.getFormEnd() %>
                </div>
            </div>
            <div class="list-group-item">
                <h4>Datei löschen</h4>
                <div class="alert alert-danger">
                    Wenn Sie die Datei löschen, können weder Sie noch andere Benutzer die Datei
                    anzeigen, herunterladen oder widerherstellen.
                </div>
                <%=form.getFormBegin("delete") %>
                <%=form.getSubmit("Datei löschen", "btn-danger")%>
                <%=form.getFormEnd()%>
            </div>
        </div>
    </div>
    <div class="content-right">
        <%@include file="../member/index.jsp"%>
        <%@include file="info.jsp"%>
    </div>
</div>