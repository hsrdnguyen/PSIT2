<%@ page import="java.io.*,java.util.*, javax.servlet.*" %>
<%@ page import="javax.servlet.http.*" %>
<%@ page import="org.apache.commons.fileupload.*" %>
<%@ page import="org.apache.commons.fileupload.disk.*" %>
<%@ page import="org.apache.commons.fileupload.servlet.*" %>
<%@ page import="org.apache.commons.io.output.*" %>
<jsp:useBean id="verificationBean" scope="request" class="ch.avocado.share.controller.VerificationBean"/>
<%
    String code = request.getParameter("code");
    String mail = request.getParameter("mail");

    if (code != null && mail != null)
    {
        verificationBean.verifyCode(code, mail);
    }

    String redirectURL = "http://127.0.0.1:8080";
    response.sendRedirect(redirectURL);
%>