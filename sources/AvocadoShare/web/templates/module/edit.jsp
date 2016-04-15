<%@ page import="ch.avocado.share.model.data.Module" %>
<%@ page import="ch.avocado.share.common.form.FormBuilder" %>
<%@ page import="ch.avocado.share.servlet.resources.base.DetailViewConfig" %>
<%@ page import="ch.avocado.share.servlet.resources.base.HtmlRenderer" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    DetailViewConfig viewConfig = (DetailViewConfig) request.getAttribute(HtmlRenderer.ATTRIBUTE_DETAIL_VIEW_CONFIG);
    FormBuilder form = new FormBuilder(viewConfig, Module.class);
    form.setReadableFieldName("name", "Name");
    form.setReadableFieldName("description", "Beschreibung");
%>
<h2>Modul ändern</h2>
<%=form.getFormErrors() %>
<%=form.getFormBegin("PATCH")%>
<div class="form-group">
    <%=form.getLabelFor("name") %>
    <%=form.getInputFor("name") %>
</div>
<div class="form-group">
    <%=form.getLabelFor("description") %>
    <%=form.getInputFor("description") %>
</div>
<%=form.getSubmit("Modul speichern")%>
<%=form.getFormEnd()%>
