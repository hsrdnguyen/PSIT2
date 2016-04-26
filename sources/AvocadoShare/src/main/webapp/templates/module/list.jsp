<%@ page import="ch.avocado.share.common.Encoder" %>
<%@ page import="ch.avocado.share.model.data.Module" %>
<%@ page import="ch.avocado.share.servlet.resources.base.HtmlRenderer" %>
<%@ page import="ch.avocado.share.servlet.resources.base.ListViewConfig" %>
<%@ page import="java.util.Collection" %>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%
    ListViewConfig viewConfig = (ListViewConfig) request.getAttribute(HtmlRenderer.ATTRIBUTE_LIST_VIEW_CONFIG);
    Collection<Module> modules = viewConfig.getObjects(Module.class);
    boolean hasModules = modules != null && !modules.isEmpty();
%>
<div class="row">
    <div class="content-main">
        <div class="list-group">
            <div class="list-group-item list-group-header"><h2>Module</h2></div>
            <% if (!hasModules) { %>
            <div class="list-group-item list-group-item-info">
                Wir konnten leider keine Module für Sie finden.
            </div>
            <%
            } else {
                for (Module module : modules) {
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
        </div>
    </div>
</div>