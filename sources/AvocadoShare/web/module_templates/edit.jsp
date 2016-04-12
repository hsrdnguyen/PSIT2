<%@ page import="ch.avocado.share.model.data.Module" %>
<%@ page import="java.util.Map" %>
<%@ page import="ch.avocado.share.common.form.FormBuilder" %>
<%@ page import="ch.avocado.share.controller.ResourceBean" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    Module module = (Module) request.getAttribute("Module");
    Map<String, String> formErrors = (Map<String, String>) request.getAttribute(ResourceBean.ATTRIBUTE_FORM_ERRORS);
    FormBuilder form = new FormBuilder(module, Module.class, formErrors);
%>
<h2>Neues Modul erstellen</h2>
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
