<%@ page import="ch.avocado.share.model.data.Category" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    {
        String categoryUrl = Encoder.forHtmlAttribute(new UrlHelper(request).getCategoryServletUrl());
        CategoryViewHelper categoryViewHelper = new CategoryViewHelper(viewConfig);
        boolean canEditCategories = viewConfig.getAccess().containsLevel(AccessLevelEnum.WRITE);
%>
<div class="list-group">
    <div class="list-group-item list-group-header">
        <% if (canEditCategories) { %>
        <div class="dropdown pull-xs-right">
            <button class="btn btn-secondary dropdown-toggle" type="button" id="add-category" data-toggle="dropdown"
                    aria-haspopup="true">
                <span class="octicon octicon-plus"></span>
                <span class="sr-only">Kategorie hinzufügen</span>
            </button>
            <div class="dropdown-menu dropdown-menu-right" aria-labelledby="add-category">
                <div class="dropdown-item">
                    <form method="post" action="<%=categoryUrl%>">
                        <div class="input-group">
                            <input type="text" class="add-category" name="category" placeholder="Kategorie"/>
                            <%=categoryViewHelper.getObjectIdField()%>
                            <span class="input-group-btn">
                                <input type="submit" class="btn btn-secondary-outline" value="Hinzufügen"/>
                            </span>
                        </div>
                    </form>
                </div>
            </div>
        </div>
        <% } %>
        <h3>Kategorien</h3>
    </div>
    <div class="list-group-item">
        <div class="categories clearfix">

            <ul>
                <% for (Category category : categoryViewHelper.getSortedCategoryList()) { %>
                <li class="category" data-category-id="<%=Encoder.forHtmlAttribute(file.getId())%>"
                    data-category="<%=Encoder.forHtmlAttribute(category.getName())%>">
                    <span class="category-name"><%=Encoder.forHtml(category.getName()) %></span>
                    <% if (canEditCategories) { %>
                    <div class="delete-category">
                        <form method="post" action="<%=categoryUrl %>">
                            <input type="hidden" name="method" value="delete"/>
                            <%=categoryViewHelper.getObjectIdField()%>
                            <input type="hidden" name="category"
                                   value="<%=Encoder.forHtmlAttribute(category.getName())%>"/>
                            <button type="submit" class="btn btn-secondary-outline" title="Kategorie entfernen">
                                <span class="octicon octicon-remove-close"></span>
                            </button>
                        </form>
                    </div>
                    <% } %>
                </li>
            </ul>
            <% } %>
        </div>
    </div>
</div>
<% } %>