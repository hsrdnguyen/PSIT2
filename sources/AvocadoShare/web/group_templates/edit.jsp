<%@ page import="ch.avocado.share.model.data.Group" %>
<%@ page import="ch.avocado.share.common.Encoder" %>
<%@ page import="ch.avocado.share.common.form.FormBuilder" %>
<%@ page import="java.util.Map" %>
<%@ page import="ch.avocado.share.controller.GroupBean" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    FormBuilder formBuilder = new FormBuilder((Group) request.getAttribute("Group"), Group.class, (Map<String, String>) request.getAttribute(GroupBean.ATTRIBUTE_FORM_ERRORS));
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
