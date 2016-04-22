<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<div class="list-group-item list-group-header">
    <h2>Benutzer hinzufügen</h2>
</div>
<noscript class="list-group-item-danger">
    Für diese Aktion wird JavaScript benötigt.
</noscript>
<div class="list-group-item">
    <form method="post" action="/members">
        <div class="form-group">
            <input type="text" name="<%=MemberServlet.OWNER_ID%>" class="form-control" id="user" data-suggestion="/user_completion">
        </div>
        <input type="hidden" name="<%=MemberServlet.ACCESS_LEVEL%>" value="<%=Encoder.forHtmlAttribute(AccessLevelEnum.READ.name())%>" />
        <input type="hidden" name="<%=MemberServlet.TARGET_ID%>" value="<%=Encoder.forHtmlAttribute(members.getTarget().getId())%>" />
        <input type="submit" class="btn btn-primary" value="Hinzufügen"/>
        <a href="?id=<%=Encoder.forUrlAttribute(members.getTarget().getId())%>"
           class="btn btn-secondary">Abbrechen</a>
    </form>
</div>
<script>
    var a = new Autocomplete(document.getElementById("user"));
</script>