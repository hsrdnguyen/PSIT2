<%@ page import="ch.avocado.share.common.form.FormBuilder" %>
<%@ page import="ch.avocado.share.model.data.Module" %>
<%@ page import="ch.avocado.share.servlet.resources.base.DetailViewConfig" %>
<%@ page import="ch.avocado.share.servlet.resources.base.HtmlRenderer" %>
<%@ page import="ch.avocado.share.common.form.InputType" %>
<%@ page import="ch.avocado.share.common.HttpMethod" %>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%
    DetailViewConfig viewConfig = (DetailViewConfig) request.getAttribute(HtmlRenderer.ATTRIBUTE_DETAIL_VIEW_CONFIG);
    FormBuilder form = new FormBuilder(viewConfig, Module.class);
    form.setReadableFieldName("name", "Name");
    form.setReadableFieldName("description", "Beschreibung");
%>
<div class="row">
    <div class="content-main">
        <div class="list-group-item list-group-header">
            <h2>Neues Modul erstellen</h2>
        </div>
        <div class="list-group-item">
            <%=form.getFormErrors() %>
            <%=form.getFormBegin(HttpMethod.POST)%>
            <div class="form-group">
                <%=form.getLabelFor("name") %>
                <%=form.getInputFor("name") %>
            </div>
            <div class="form-group">
                <%=form.getLabelFor("description") %>
                <%=form.getInputFor("description", InputType.TEXTAREA) %>
            </div>
            <%=form.getSubmit("Modul erstellen")%>
            <%=form.getFormEnd()%>
        </div>
    </div>
    <div class="content-right">
        <div class="list-group-item list-group-header">
            <h3><span class="mega-octicon octicon-info"></span> Informationen</h3>
        </div>
        <div class="list-group-item">
            !TODO!
        </div>
    </div>
</div>