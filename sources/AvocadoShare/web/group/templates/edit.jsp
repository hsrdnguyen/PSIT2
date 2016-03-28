<%@ page import="ch.avocado.share.model.data.Group" %>
<%@ page import="ch.avocado.share.common.Encoder" %>
<%@ page import="ch.avocado.share.common.FormBuilder" %>
<%@ page import="java.util.Map" %>
<%@ page import="ch.avocado.share.controller.GroupBean" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    FormBuilder formBuilder = new FormBuilder("", (Group) request.getAttribute("Group"), Group.class, (Map<String, String>) request.getAttribute(GroupBean.ATTRIBUTE_FORM_ERRORS));
    formBuilder.setReadableFieldName("description", "Beschreibung");
    formBuilder.setReadableFieldName("name", "Name");
%>
<h1>Create new Group</h1>
<%=formBuilder.getFormErrors()%>
<%=formBuilder.getFormBegin("PATCH") %>
<%=formBuilder.getLabelFor("name") %>
<%=formBuilder.getInputFor("name") %>
<%=formBuilder.getLabelFor("description") %>
<%=formBuilder.getInputFor("description") %>
<input type="submit" value="Gruppe erstellen" />
<%=formBuilder.getFormEnd() %>
