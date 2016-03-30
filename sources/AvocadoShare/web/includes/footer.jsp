<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF8" %>
<%
    String baseUrlFooter = request.getServletContext().getContextPath();
%>
</main>
</div> <!-- /#footerpusher -->
<footer class="text-muted">
    <div class="container">
        <ul>
            <li><a href="#about">Über uns</a></li>
            <li><a href="contact.jsp" href="#contact">Kontakt</a></li>
            <li><a href="#something">Fehler melden</a></li>
        </ul>
        <p>Sorgfälltig mit Liebe erstellt von S. Bergman, L. Kunz, C. Müller und D. T. Nguyen.</p>
        <p>&copy; 2016, All rights reserved.</p>
    </div>

</footer>
<script type="application/javascript" src="<%=baseUrlFooter%>/components/jquery/dist/jquery.min.js"></script>
<script type="application/javascript" src="<%=baseUrlFooter%>/components/bootstrap/dist/js/bootstrap.min.js"></script>
</body>
</html>