<%@include file="includes/header.jsp"%>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<div class="alert alert-success">
    Sie wurden erfolgreich registriert und sollten in Kürze eine E-Mail erhalten.
</div>
<p>
    <a href="<%=baseUrl%>/">Zurück zur Startseite</a>
</p>
<script type="application/javascript">
    window.setTimeout(function(){
        window.location.href = "<%=baseUrl%>/";
    }, 5000);
</script>
<%@include file="includes/footer.jsp"%>