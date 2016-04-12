<%@ page import="ch.avocado.share.model.data.Module" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    List<Module> modules = (List<Module>) request.getAttribute("Modules");
    boolean hasModules = modules != null && !modules.isEmpty();

    if(hasModules) {
%>