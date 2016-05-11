<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ page import="ch.avocado.share.model.data.AccessControlObjectBase" %>
<%@ page import="ch.avocado.share.model.data.File" %>
<%@ page import="ch.avocado.share.model.data.Group" %>
<%@ page import="ch.avocado.share.model.data.Module" %>
<%@ page import="java.util.List" %>
<jsp:useBean id="searchBean" class="ch.avocado.share.controller.SearchBean"/>
<jsp:setProperty name="searchBean" property="searchString"/>
<%@include file="includes/header.jsp" %>
<%@include file="includes/searchFilter.html" %>

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

                if (obj instanceof File) {
                    url = baseUrl + "/file?id=" + Encoder.forHtmlAttribute(obj.getId());
                    objtitle = Encoder.forHtml(((File) obj).getTitle());
                    cssClass = "FileResult";
                } else if (obj instanceof Group) {
                    url = baseUrl + "/group?id=" + Encoder.forHtmlAttribute(obj.getId());
                    objtitle = Encoder.forHtml(((Group) obj).getName());
                    cssClass = "GroupResult";
                } else if (obj instanceof Module) {
                    url = baseUrl + "/module?id=" + Encoder.forHtmlAttribute(obj.getId());
                    objtitle = Encoder.forHtml(((Module) obj).getName());
                    cssClass = "ModuleResult";
                } else {
                    break;
                }
        %>

        <a data-result-id="<%=Encoder.forHtmlAttribute(obj.getId()) %>" href="<%=url %>" style="visibility: collapse" class="list-group-item <%=cssClass %>">
            <h4 class="list-group-item-heading"><%=objtitle %></h4>
            <p class="list-group-item-text"><%=description %></p>
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
                <input type="checkbox" checked="checked" id="File" onclick="setResultsVisibility()"/>
                Dateien
            </label><br>
            <label>
                <input type="checkbox" checked="checked" id="Module" onclick="setResultsVisibility()"/>
                Module
            </label><br>
            <label>
                <input type="checkbox" checked="checked" id="Group" onclick="setResultsVisibility()"/>
                Gruppen
            </label><br>

        </div>
    </div>
</div>
<form name="request_access" action="<%=baseUrl%>/requestAccess.jsp">
    <input type="hidden" name="fileId" />
</form>
<script type="application/javascript">
    $(function(){
        $("[data-result-id]").each(function(i, obj){
            console.log(obj);
            new SearchResult("<%=baseUrl%>/access/", obj.getAttribute("data-result-id"), obj, document.forms.request_access);
        });
    })
</script>
<%@include file="includes/footer.jsp" %>
