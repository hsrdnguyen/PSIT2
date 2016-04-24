<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %><%
    String baseUrlFooter = request.getServletContext().getContextPath();
%>
        </main>
</div> <!-- /#footerpusher -->
<footer class="bg-inverse">
    <div class="container text-muted">
        <ul>
            <li><a href="#about">&Uuml;ber uns</a></li>
            <li><a href="#contact">Kontakt</a></li>
            <li><a href="#something">Fehler melden</a></li>
        </ul>
        <p>Sorgf&auml;lltig mit Liebe erstellt von S. Bergman, L. Kunz, C. M&uuml;ller und D. T. Nguyen.</p>
        <p>&copy; 2016, All rights reserved.
            Ein Dankeschön an John Pugsley für das tolle
            <a title="'yellow white and blue' auf Flickr" href="https://flic.kr/p/76GCDs">Hintergundbild</a>.</p>
    </div>
</footer>
<script type="application/javascript" src="<%=baseUrlFooter%>/components/jquery/dist/jquery.min.js"></script>
<script type="application/javascript" src="<%=baseUrlFooter%>/components/bootstrap/dist/js/bootstrap.min.js"></script>
</body>
</html>