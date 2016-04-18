<%@ page import="ch.avocado.share.model.data.Group" %>
<%@ page import="ch.avocado.share.common.form.FormBuilder" %>
<%@ page import="ch.avocado.share.servlet.resources.base.DetailViewConfig" %>
<%@ page import="ch.avocado.share.servlet.resources.base.HtmlRenderer" %>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%
    DetailViewConfig viewConfig = (DetailViewConfig) request.getAttribute(HtmlRenderer.ATTRIBUTE_DETAIL_VIEW_CONFIG);
    FormBuilder formBuilder = new FormBuilder(viewConfig, Group.class);
    formBuilder.setReadableFieldName("description", "Beschreibung");
    formBuilder.setReadableFieldName("name", "Name");
%>
<h2>Gruppe bearbeiten</h2>
<%=formBuilder.getFormErrors()%>
<%=formBuilder.getFormBegin("PATCH") %>
<div class="form-group">
    <%=formBuilder.getLabelFor("name") %>
    <%=formBuilder.getInputFor("name") %>
</div>
<div class="form-group">
<%=formBuilder.getLabelFor("description") %>
<%=formBuilder.getInputFor("description", "textarea") %>
</div>
<div class="form-group">
<%=formBuilder.getSubmit("Gruppe speichern")%>
</div>
<%=formBuilder.getFormEnd() %>

<h3>Gruppe löschen</h3>
<div class="alert alert-danger">
    Eine gelöschte Gruppe kann nicht wiederhergestellt werden.<br/>
    Sowohl Sie als auch alle Mitglieder verlieren die Rechte die Sie durch diese Gruppe erworben haben.
</div>
<%=formBuilder.getFormBegin("delete")%>
<%=formBuilder.getSubmit("Gruppe löschen", "btn-danger")%>
<%=formBuilder.getFormEnd()%>
