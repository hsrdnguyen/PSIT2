<%@ page import="ch.avocado.share.common.form.FormBuilder" %>
<%@ page import="ch.avocado.share.servlet.resources.base.DetailViewConfig" %>
<%@ page import="ch.avocado.share.servlet.resources.base.HtmlRenderer" %>
<%@ page import="ch.avocado.share.common.form.InputType" %>
<%@ page import="ch.avocado.share.common.HttpMethod" %>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%
    DetailViewConfig viewConfig = (DetailViewConfig) request.getAttribute(HtmlRenderer.ATTRIBUTE_DETAIL_VIEW_CONFIG);
    FormBuilder formBuilder = new FormBuilder(viewConfig, Group.class);
    formBuilder.setReadableFieldName("description", "Beschreibung");
    formBuilder.setReadableFieldName("name", "Name");
%>
<div class="row">
    <div class="content-main">
        <div class="list-group">
            <div class="list-group-item list-group-header">
                <h2>Gruppe bearbeiten</h2>
            </div>
            <div class="list-group-item">
                <%=formBuilder.getFormErrors()%>
                <%=formBuilder.getFormBegin(HttpMethod.PATCH) %>
                <div class="form-group">
                    <%=formBuilder.getLabelFor("name") %>
                    <%=formBuilder.getInputFor("name") %>
                </div>
                <div class="form-group">
                    <%=formBuilder.getLabelFor("description") %>
                    <%=formBuilder.getInputFor("description", InputType.TEXTAREA) %>
                </div>
                <div class="form-group">
                    <%=formBuilder.getSubmit("Gruppe speichern")%>
                </div>
                <%=formBuilder.getFormEnd() %>
            </div>
            <div class="list-group-item">
                <h4>Gruppe löschen</h4>
                <div class="alert alert-danger">
                    Eine gelöschte Gruppe kann nicht wiederhergestellt werden.<br/>
                    Sowohl Sie als auch alle Mitglieder verlieren die Rechte die Sie durch diese Gruppe erworben haben.
                </div>
                <%=formBuilder.getFormBegin(HttpMethod.DELETE)%>
                <%=formBuilder.getSubmit("Gruppe löschen", "btn-danger")%>
                <%=formBuilder.getFormEnd()%>
            </div>
        </div>
    </div>
    <div class="content-right">
        <%@include file="../member/index.jsp" %>
    </div>
</div>