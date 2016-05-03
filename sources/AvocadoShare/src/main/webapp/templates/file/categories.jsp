<%@ page import="ch.avocado.share.model.data.Category" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    {
        String categoryUrl = Encoder.forHtmlAttribute(new UrlHelper(request).getCategoryServletUrl());
        CategoryViewHelper categoryViewHelper = new CategoryViewHelper(viewConfig);
        boolean canEditCategories = viewConfig.getAccess().containsLevel(AccessLevelEnum.WRITE);
%>
<div class="categories clearfix">
    <% if (canEditCategories) { %>
    <div class="add-category pull-xs-right">
        <form method="post" action="<%=categoryUrl%>">
            <div class="input-group">
                <input type="text" class="form-control" name="category" placeholder="Kategorie"/>
                <%=categoryViewHelper.getObjectIdField()%>
                <span class="input-group-btn">
                    <input type="submit" class="btn btn-secondary-outline" value="Hinzufügen"/>
                </span>
            </div>
        </form>
    </div>
    <% } %>
    <% for (Category category : categoryViewHelper.getSortedCategoryList()) { %>
    <span class="category" data-category-id="<%=Encoder.forHtmlAttribute(file.getId())%>"
         data-category="<%=Encoder.forHtmlAttribute(category.getName())%>">
        <span class="label label-default">
            <%=Encoder.forHtml(category.getName()) %>
        </span>
        <% if (canEditCategories) { %>
        <div class="delete-category">
            <form method="post" action="<%=categoryUrl %>">
                <input type="hidden" name="method" value="delete"/>
                <%=categoryViewHelper.getObjectIdField()%>
                <input type="hidden" name="category" value="<%=Encoder.forHtmlAttribute(category.getName())%>"/>
                <input type="submit" class="btn btn-secondary" title="Kategorie entfernen" value="Entfernen"/>
            </form>
        </div>
        <% } %>
    </span>
    <% } %>
</div>
<% } %>