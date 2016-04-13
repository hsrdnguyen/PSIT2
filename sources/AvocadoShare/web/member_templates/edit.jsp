<%@ page import="ch.avocado.share.model.data.User" %>
<%@ page import="ch.avocado.share.model.data.Group" %>
<%@ page import="ch.avocado.share.common.Encoder" %>
<%@ page import="ch.avocado.share.model.data.AccessLevelEnum" %>
<%@ page import="ch.avocado.share.model.data.AccessControlObjectBase" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    User user = (User) request.getAttribute("MemberUser");
    Group group = (Group) request.getAttribute("MemberGroup");
    AccessControlObjectBase target = (AccessControlObjectBase) request.getAttribute("Target");
    final String name, type, id, ownerFieldName;
    if (user != null) {
        name = user.getFullName();
        type = "Benutzer";
        ownerFieldName = "userId";
        id = user.getId();
    } else {
        name = group.getName();
        type = "Gruppe";
        ownerFieldName = "groupId";
        id = group.getId();
    }
%>
</div>
<h2>Rechte bearbeiten</h2>
<div class="row">
    <div class="col-md-6">
        <form method="post" action="<%=request.getContextPath() %>members.jsp">
            <div class="form-group">
                <label><%=type%>
                </label>
                <input type="text" class="form-control" value="<%=Encoder.forHtmlAttribute(name) %>"
                       disabled="disabled"/>
            </div>
            <input type="hidden" name="<%=ownerFieldName %>" value="<%=Encoder.forHtmlAttribute(id)%>"/>
            <input type="hidden" name="targetId" value="<%=target.getId()%>"/>
            <div class="form-group">
                <label>Zugriffsrecht</label>
                <select name="level" class="form-control">
                    <option value="<%=AccessLevelEnum.NONE.toString()%>">Keine</option>
                    <option selected="selected" value="<%=AccessLevelEnum.READ.toString()%>">Lesen</option>
                    <option value="<%=AccessLevelEnum.WRITE.toString()%>">Lesen und Schreiben</option>
                </select>
            </div>
            <input type="hidden" name="method" value="put"/>
            <div class="form-group">
                <input type="submit" class="btn btn-primary" value="Speichern"/>
            </div>
        </form>
    </div>
</div>
<div>