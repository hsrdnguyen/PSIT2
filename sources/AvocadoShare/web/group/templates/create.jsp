<%@ page import="ch.avocado.share.common.FormBuilder" %>
<%@ page import="ch.avocado.share.controller.GroupBean" %>
<%@ page import="java.util.Map" %>
<%@ page import="ch.avocado.share.controller.ResourceBean" %>
<%@ page import="ch.avocado.share.model.data.Group" %><%--
  Created by IntelliJ IDEA.
  User: coffeemakr
  Date: 24.03.16
  Time: 12:43
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    FormBuilder formBuilder = new FormBuilder("", Group.class, (Map<String, String>) request.getAttribute(GroupBean.ATTRIBUTE_FORM_ERRORS));
    formBuilder.setReadableFieldName("description", "Beschreibung");
    formBuilder.setReadableFieldName("name", "Name");
%>
<h1>Create new Group</h1>
<%=formBuilder.getFormErrors()%>
<%=formBuilder.getFormBegin("POST") %>
<%=formBuilder.getLabelFor("name") %>
<%=formBuilder.getInputFor("name") %>
<%=formBuilder.getLabelFor("description") %>
<%=formBuilder.getInputFor("description") %>
<input type="submit" value="Gruppe erstellen" />
<%=formBuilder.getFormEnd() %>
