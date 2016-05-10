<%@ page import="ch.avocado.share.common.form.FormBuilder" %>
<%@ page import="ch.avocado.share.model.data.Group" %>
<%@ page import="ch.avocado.share.servlet.resources.base.DetailViewConfig" %>
<%@ page import="ch.avocado.share.servlet.resources.base.HtmlRenderer" %>
<%@ page import="ch.avocado.share.common.form.InputType" %>
<%@ page import="ch.avocado.share.common.HttpMethod" %>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%
    DetailViewConfig viewConfig = (DetailViewConfig) request.getAttribute(HtmlRenderer.ATTRIBUTE_DETAIL_VIEW_CONFIG);
    FormBuilder formBuilder = new FormBuilder(viewConfig, Group.class);
    formBuilder.setReadableFieldName("description", "Beschreibung");
    formBuilder.setReadableFieldName("name", "Name");
%>
<%=formBuilder.getFormErrors()%>
<%=formBuilder.getFormBegin(HttpMethod.POST) %>
<div class="row">
    <div class="content-main">
        <div class="list-group-item list-group-header">
            <h2>Neue Gruppe erstellen</h2>
        </div>
        <div class="list-group-item">
            <div class="form-group">
                <%=formBuilder.getLabelFor("name") %>
                <%=formBuilder.getInputFor("name") %>
            </div>
            <div class="form-group">
                <%=formBuilder.getLabelFor("description") %>
                <%=formBuilder.getInputFor("description", InputType.TEXTAREA) %>
            </div>
            <div class="form-group">
                <%=formBuilder.getSubmit("Gruppe erstellen")%>
                <%=formBuilder.getFormEnd() %>
            </div>
        </div>
    </div>
    <div class="content-right">
        <div class="list-group-item list-group-header">
            <h3><span class="mega-octicon octicon-info"></span> Informationen</h3>
        </div>
        <div class="list-group-item">
            Erstellen Sie eine Gruppe, um mit KollegenInnen sich eifacher
            zu organisieren.<br/>
            Sie können Sich als Gruppe in ein Modul einschreiben und alle
            MitgliederInnen erhalten sofort dasselbe Zugriffsrecht.
        </div>
    </div>
</div>