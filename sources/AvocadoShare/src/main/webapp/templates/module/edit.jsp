<%@ page import="ch.avocado.share.common.form.FormBuilder" %>
<%@ page import="ch.avocado.share.model.data.Module" %>
<%@ page import="ch.avocado.share.servlet.resources.base.DetailViewConfig" %>
<%@ page import="ch.avocado.share.servlet.resources.base.HtmlRenderer" %>
<%@ page import="ch.avocado.share.common.form.InputType" %>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%
    DetailViewConfig viewConfig = (DetailViewConfig) request.getAttribute(HtmlRenderer.ATTRIBUTE_DETAIL_VIEW_CONFIG);
    FormBuilder form = new FormBuilder(viewConfig, Module.class);
    form.setReadableFieldName("name", "Name");
    form.setReadableFieldName("description", "Beschreibung");
%>
<div class="row">
    <div class="content-main">
        <h2>Modul ändern</h2>
        <%=form.getFormErrors() %>
        <%=form.getFormBegin("PATCH")%>
        <div class="form-group">
            <%=form.getLabelFor("name") %>
            <%=form.getInputFor("name") %>
        </div>
        <div class="form-group">
            <%=form.getLabelFor("description") %>
            <%=form.getInputFor("description", InputType.TEXTAREA) %>
        </div>
        <%=form.getSubmit("Modul speichern")%>
        <%=form.getFormEnd()%>

        <h3>Modul löschen</h3>
        <div class="alert alert-danger">
            Wenn Sie diese Modul löschen werden auch alle Dateien, die in dieses Modul hochgeladen wurden gelöscht.<br/>
            Weder Sie noch die Mitglieder dieses Modules können danach noch auf diese Dateien zugreifen.
        </div>
        <%=form.getFormBegin("delete")%>
        <%=form.getSubmit("Modul löschen", "btn-danger")%>
        <%=form.getFormEnd() %>
    </div>
    <div class="content-right">
        <%@include file="../member/index.jsp" %>
    </div>
</div>