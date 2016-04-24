<%@ page import="ch.avocado.share.common.form.FormBuilder" %>
<%@ page import="ch.avocado.share.model.data.Group" %>
<%@ page import="ch.avocado.share.servlet.resources.base.DetailViewConfig" %>
<%@ page import="ch.avocado.share.servlet.resources.base.HtmlRenderer" %>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%
    DetailViewConfig viewConfig = (DetailViewConfig) request.getAttribute(HtmlRenderer.ATTRIBUTE_DETAIL_VIEW_CONFIG);
    FormBuilder formBuilder = new FormBuilder(viewConfig, Group.class);
    formBuilder.setReadableFieldName("description", "Beschreibung");
    formBuilder.setReadableFieldName("name", "Name");
%>
<h1>Neue Gruppe erstellen</h1>
<%=formBuilder.getFormErrors()%>
<%=formBuilder.getFormBegin("POST") %>
<div class="row">
    <div class="col-md-6">
        <div class="form-group">
            <%=formBuilder.getLabelFor("name") %>
            <%=formBuilder.getInputFor("name") %>
        </div>
        <div class="form-group">
            <%=formBuilder.getLabelFor("description") %>
            <%=formBuilder.getInputFor("description", "textarea") %>
        </div>
        <div class="form-group">
            <%=formBuilder.getSubmit("Gruppe erstellen")%>
            <%=formBuilder.getFormEnd() %>
        </div>
    </div>
</div>