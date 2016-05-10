<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ page import="ch.avocado.share.model.data.AccessControlObjectBase" %>
<%@ page import="ch.avocado.share.model.data.File" %>
<%@ page import="ch.avocado.share.model.data.Group" %>
<%@ page import="ch.avocado.share.model.data.Module" %>
<%@ page import="java.util.List" %>
<jsp:useBean id="searchBean" class="ch.avocado.share.controller.SearchBean"/>
<jsp:setProperty name="searchBean" property="searchString"/>
<%@include file="includes/header.jsp" %>

<div class="content-main">
    <div class="list-group">
        <div class="list-group-item list-group-header">
            <h2>Suchergebnisse</h2>
        </div>

        <%
            List<AccessControlObjectBase> results = searchBean.search();

            for (AccessControlObjectBase obj : results) {

                String url = "";
                String objtitle = "";
                String description = Encoder.forHtml(obj.getDescription());
                String cssClass = "";

                if (obj.getClass() == File.class) {
                    url = baseUrl + "/file?id=" + Encoder.forHtmlAttribute(obj.getId());
                    objtitle = Encoder.forHtml(((File) obj).getTitle());
                    cssClass = "FileResult";
                } else if (obj.getClass() == Group.class) {
                    url = baseUrl + "/group?id=" + Encoder.forHtmlAttribute(obj.getId());
                    objtitle = Encoder.forHtml(((Group) obj).getName());
                    cssClass = "GroupResult";
                } else if (obj.getClass() == Module.class) {
                    url = baseUrl + "/module?id=" + Encoder.forHtmlAttribute(obj.getId());
                    objtitle = Encoder.forHtml(((Module) obj).getName());
                    cssClass = "ModuleResult";
                } else {
                    break;
                }
        %>

        <a href="<%=url %>" class="list-group-item <%=cssClass %>">
            <div class="pull-xs-right">
                <form id="edit-doc" method="POST" action="<%=baseUrl%>/requestAccess.jsp">
                    <input type="submit" id="requestRights" class="btn btn-secondary btn-secondary"
                           type="submit" value="Rechte anfordern"/>
                    <input id="fileId" name="fileId" type="hidden" value="<%=obj.getId() %>"/>
                </form>
            </div>
            <h4 class="list-group-item-heading"><%=objtitle %>
            </h4>
            <p class="list-group-item-text"><%=description %>
            </p>
        </a>
        <%
            }
        %>

    </div>
</div>
<div class="content-right">
    <div class="list-group">
        <div class="list-group-item list-group-header"><h2>Filter</h2></div>
        <!-- First Module -->
        <div class="list-group-item">
            <label>
                <input type="checkbox" rel="dab"/>
                Datenbank
            </label><br>
            <label>
                <input type="checkbox" rel="web"/>
                Web
            </label><br>
            <label>
                <input type="checkbox" rel="prog"/>
                Programmieren
            </label><br>
            <label>
                <input type="checkbox" rel="thin"/>
                Theoretische Informatik
            </label><br>
            <label>
                <input type="checkbox" rel="manit"/>
                Mathematik Analysis
            </label><br>
            <label>
                <input type="checkbox" rel="mgmit"/>
                Diskrete Mathematik
            </label><br>
            <label>
                <input type="checkbox" rel="etc"/>
                ...
            </label><br>
        </div>
    </div>
</div>
<%@include file="includes/footer.jsp" %>
