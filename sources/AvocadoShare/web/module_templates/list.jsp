<%@ page import="ch.avocado.share.model.data.Module" %>
<%@ page import="java.util.List" %>
<%@ page import="ch.avocado.share.common.Encoder" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    List<Module> modules = (List<Module>) request.getAttribute("Modules");
    boolean hasModules = modules != null && !modules.isEmpty();
%>
<section class="document-show">
    <div class="list-group">
        <div class="list-group-item list-group-header"><h2>Module</h2></div>
            <% if(!hasModules) { %>
        <div class="list-group-item list-group-item-info">
            Wir konnten leider keine Module für Sie finden.
        </div>
<%
            } else {
                for(Module module: modules) {
                    String detailLink = "?id=" + Encoder.forHtmlAttribute(module.getId());
                    String title = module.getName();
                    String description = module.getDescription();
            %>
        <a href="<%=detailLink %>" class="list-group-item">
            <h4 class="list-group-item-heading"><%=title%>
            </h4>
            <p class="list-group-item-text"><%=description %>
            </p>
        </a>
<%
        }
    }
%>