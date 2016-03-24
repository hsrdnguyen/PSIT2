<%@ page import="java.io.*,java.util.*, javax.servlet.*" %>
<%@ page import="javax.servlet.http.*" %>
<%@ page import="org.apache.commons.fileupload.*" %>
<%@ page import="org.apache.commons.fileupload.disk.*" %>
<%@ page import="org.apache.commons.fileupload.servlet.*" %>
<%@ page import="org.apache.commons.io.output.*" %>
<jsp:useBean id="uploadBean" scope="request" class="ch.avocado.share.controller.FileUploadBean"/>
<%
    String contentType = request.getContentType();
    FileItem file = null;

    if ((contentType.indexOf("multipart/form-data") >= 0)) {
        List<FileItem> items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);
        for (FileItem item : items) {
            if (item.isFormField()) {
                switch (item.getFieldName()){
                    case "description":
                        uploadBean.setDescription(item.getString());
                        break;
                    case "title":
                        uploadBean.setTitle(item.getString());
                        break;
                    case "author":
                        uploadBean.setAuthor(item.getString());
                        break;
                }
            } else {
                file = item;
            }
        }
        uploadBean.saveFile(file);
    }
%>